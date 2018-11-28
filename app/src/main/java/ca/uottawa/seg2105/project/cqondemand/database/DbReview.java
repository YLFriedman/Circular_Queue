package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

public class DbReview extends DbItem<Review> {

    public Integer rating;
    public String comment;
    public Long date_created;
    public String reviewer_name;
    public String reviewer_key;
    public String service_name;

    public DbReview() {}

    public DbReview(Review item) {
        super(item.getKey());
        rating = item.getRating();
        comment = item.getComment();
        date_created = item.getDateCreated().getTime();
        reviewer_name = item.getReviewerName();
        reviewer_key = item.getReviewerKey();
        service_name = item.getServiceName();
    }

    @NonNull
    public Review toDomainObj() {
        return new Review(key, new Date(date_created), rating, comment, service_name, reviewer_name, reviewer_key);
    }

    public static void createReview(@NonNull Review review, @NonNull ServiceProvider provider, @Nullable AsyncActionEventListener listener) {
        if (null == review.getKey()) { throw new IllegalArgumentException("Review key required to update database"); }
        if (null == provider.getKey()) { throw new IllegalArgumentException("Review key required to update database"); }
        getReview(provider.getKey(), review.getKey(), new AsyncSingleValueEventListener<Review>() {
            @Override
            public void onSuccess(@NonNull Review item) {
                // Failure Condition: Booking already reviewed
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    // Do the update
                    Map<String, Object> pathMap = new HashMap<String, Object>();
                    pathMap.put(String.format("provider_reviews/%s/%s", provider.getKey(), review.getKey()), new DbReview(review));
                    // TODO: Apply review to service provider
                    DbUser.updateUser(provider, pathMap, listener);
                } else {
                    if (null != listener) { listener.onFailure(reason); }
                }
            }
        });

        /*DatabaseReference baseRef = DbUtil.getRef(String.format("provider_reviews/%s/%s", serviceProviderKey, reviewKey));
        DbReview dbVersion = new DbReview(review);
        baseRef.setValue(dbVersion, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                listener.onSuccess();
            } else {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });*/
    }




    public static void getReview(@NonNull String serviceProviderKey, @NonNull String bookingKey, AsyncSingleValueEventListener<Review> listener) {
        String path = String.format("%s/%s", serviceProviderKey, bookingKey);
        DbUtilRelational.getItemRelational(DbUtilRelational.RelationType.PROVIDER_REVIEWS, path, listener);
    }

    public static void getReviews(@NonNull String serviceProviderKey, @NonNull AsyncValueEventListener<Review> listener) {
        DbQuery query = DbQuery.createChildValueQuery("date_created");
        DbUtilRelational.getItemsRelational(DbUtilRelational.RelationType.PROVIDER_REVIEWS, serviceProviderKey, query, listener);
    }
    public static DbListenerHandle<?> getReviewsLive(@NonNull String serviceProviderKey, @NonNull AsyncValueEventListener<Review> listener) {
        DbQuery query = DbQuery.createChildValueQuery("date_created");
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.PROVIDER_REVIEWS, serviceProviderKey, query, listener);
    }

}
