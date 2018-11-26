package ca.uottawa.seg2105.project.cqondemand.database;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;

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

    public static void createReview(@NonNull Review review, @Nullable AsyncActionEventListener listener) {

    }
}
