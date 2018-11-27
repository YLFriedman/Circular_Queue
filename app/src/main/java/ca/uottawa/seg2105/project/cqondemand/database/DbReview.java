package ca.uottawa.seg2105.project.cqondemand.database;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
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

    public static void createReview(@NonNull Review review, String serviceProviderKey, @Nullable AsyncActionEventListener listener) {
        String reviewKey = review.getKey();
        if(reviewKey == null) {
            throw new IllegalArgumentException("Review key required to update database");
        }
        DatabaseReference baseRef = DbUtil.getRef(String.format("provider_reviews/%s/%s", serviceProviderKey, reviewKey));
        DbReview dbVersion = new DbReview(review);
        baseRef.setValue(dbVersion, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                listener.onSuccess();
            } else {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    public static void getReviews(String serviceProviderKey, AsyncValueEventListener<Review> listener) {
        DbQuery query = DbQuery.createChildValueQuery("date_created");
        DbUtilRelational.getItemsRelational(DbUtilRelational.RelationType.PROVIDER_REVIEWS, serviceProviderKey, query, listener);
    }
    public static DbListenerHandle<?> getReviewsLive(@NonNull String serviceProviderKey, @NonNull AsyncValueEventListener<Review> listener) {
        DbQuery query = DbQuery.createChildValueQuery("date_created");
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.PROVIDER_REVIEWS, serviceProviderKey, query, listener);
    }

}
