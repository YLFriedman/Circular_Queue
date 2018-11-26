package ca.uottawa.seg2105.project.cqondemand.database;

import java.util.Date;

import androidx.annotation.NonNull;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;

public class DbReview extends DbItem<Review> {

    public Integer rating;
    public String comment;
    public Long date_created;
    public String reviewerName;
    public String reviewerKey;

    public DbReview() {}

    public DbReview(Review item) {
        super(item.getKey());
        rating = item.getRating();
        comment = item.getComment();
        date_created = item.getDateCreated().getTime();
        reviewerName = item.getReviewerName();
        reviewerKey = item.getReviewerKey();
    }

    @NonNull
    public Review toDomainObj() {
        return new Review(key, new Date(date_created), rating, comment, reviewerName, reviewerKey);
    }

}
