package ca.uottawa.seg2105.project.cqondemand.database;

import java.util.Date;

import androidx.annotation.NonNull;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;

public class DbReview extends DbItem<Review> {

    public Integer rating;
    public String comment;
    public Date date_created;

    public DbReview() {}

    public DbReview(Review item) {
        super(item.getKey());
        rating = item.getRating();
        comment = item.getComment();
        date_created = item.getDateCreated();
    }

    @NonNull
    public Review toDomainObj() {
        return new Review(key, date_created, rating, comment);
    }

}
