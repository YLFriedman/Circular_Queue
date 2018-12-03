package ca.uottawa.seg2105.project.cqondemand.database;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

/**
 * The class <b> DbReview </b> is a class used to take information from a Review object, and put it into
 * a form more easily stored in the database. Methods for moving between Review and DbReview are provided,
 * as well as database read/write operations
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbReview extends DbItem<Review> {

    /**
     * The rating associated with a DbReview
     */
    public Integer rating;

    /**
     * The comment associated with a DbReview
     */
    public String comment;

    /**
     * The creation date associated with a DbReview
     */
    public Long date_created;

    /**
     * The reviewer name associated with a DbReview
     */
    public String reviewer_name;

    /**
     * The reviewer key associated with a DbReview
     */
    public String reviewer_key;

    /**
     * The service name associated with a DbReview
     */
    public String service_name;

    /**
     * Empty constructor for DbReview object
     */
    public DbReview() {}

    /**
     * Constructor for DbReview. Creates a new DbReview based on a Review object
     * @param item the Review object the new DbReview will be based off of
     */
    public DbReview(Review item) {
        super(item.getKey());
        rating = item.getRating();
        comment = item.getComment();
        date_created = item.getDateCreated().getTime();
        reviewer_name = item.getReviewerName();
        reviewer_key = item.getReviewerKey();
        service_name = item.getServiceName();
    }

    /**
     * Method to create a Review based off of a particular DbReview
     * @return a Review based off of this dbReview
     */
    @NonNull
    public Review toDomainObj() {
        return new Review(key, new Date(date_created), rating, comment, service_name, reviewer_name, reviewer_key);
    }

    /**
     * Adds a Review to the database, updating all relevant locations
     *
     * @param review the review to be added
     * @param provider the provider associated with the review
     * @param listener the listener that will handle the failure/success of this operation
     */
    public static void createReview(final @NonNull Review review, final @NonNull ServiceProvider provider, @Nullable AsyncActionEventListener listener) {
        if (null == review.getKey()) { throw new IllegalArgumentException("Review key required to update database"); }
        if (null == provider.getKey()) { throw new IllegalArgumentException("Review key required to update database"); }
        // Make sure the review has not already been submitted for the booking
        getReview(provider.getKey(), review.getKey(), new AsyncSingleValueEventListener<Review>() {
            @Override
            public void onSuccess(@NonNull Review item) {
                // Failure Condition: Booking already reviewed
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Success Condition: Booking has not been reviewed
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    // Get the latest version of the user object in-case a rating as applied before this one was submitted
                    DbUser.getUser(provider.getKey(), new AsyncSingleValueEventListener<User>() {
                        @Override
                        public void onSuccess(@NonNull User item) {
                            Map<String, Object> pathMap = new HashMap<String, Object>();
                            pathMap.put(String.format("%s/%s/%s", DbUtilRelational.RelationType.PROVIDER_REVIEWS, provider.getKey(), review.getKey()), new DbReview(review));
                            if (item instanceof ServiceProvider) {
                                ServiceProvider itemProvider = (ServiceProvider) item;
                                itemProvider.applyRating(review.getRating());
                                DbUser.updateUser(itemProvider, pathMap, new AsyncActionEventListener() {
                                    @Override
                                    public void onSuccess() {
                                        // Note: Potential issue: provider rating could be out of sync 'til client refreshes
                                        // On success, apply the rating to the object that the current client is using
                                        provider.applyRating(review.getRating());
                                        if (null != listener) { listener.onSuccess(); }
                                    }
                                    @Override
                                    public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                        if (null != listener) { listener.onFailure(reason); }
                                    }
                                });
                            } else {
                                if (null != listener) { listener.onFailure(AsyncEventFailureReason.INVALID_DATA); }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull AsyncEventFailureReason reason) {
                            if (null != listener) { listener.onFailure(reason); }
                        }
                    });
                } else {
                    if (null != listener) { listener.onFailure(reason); }
                }
            }
        });
    }

    /**
     * Method to get a specific Review from the database
     *
     * @param serviceProviderKey the service provider associated with the desired review
     * @param bookingKey the booking key associated with the desired review
     * @param listener the listener that will handle the success/failure of this operations
     */
    public static void getReview(@NonNull String serviceProviderKey, @NonNull String bookingKey, AsyncSingleValueEventListener<Review> listener) {
        String path = String.format("%s/%s", serviceProviderKey, bookingKey);
        DbUtilRelational.getItemRelational(DbUtilRelational.RelationType.PROVIDER_REVIEWS, path, listener);
    }

    /**
     * Method to get all the review associated with a specific service provider. Data is updated in real time
     * every time a change is made to the database
     *
     * @param serviceProviderKey the key of the service provider who's review will be accessed
     * @param listener the listener that will handle the success/failure of this operation
     * @return a DbListenerHandle that handles the ValueEventListener attached to the database
     */
    public static DbListenerHandle<?> getReviewsLive(@NonNull String serviceProviderKey, @NonNull AsyncValueEventListener<Review> listener) {
        DbQuery query = DbQuery.createChildValueQuery("date_created");
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.PROVIDER_REVIEWS, serviceProviderKey, query, listener);
    }

}
