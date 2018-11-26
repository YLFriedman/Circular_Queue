package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {

    private static final long serialVersionUID = 1;
    protected String key;
    protected String serviceName;
    protected String reviewerName;
    protected String reviewerKey;
    protected int rating;
    protected String comment;
    protected Date dateCreated;


    public Review(int rating, @Nullable String comment, @NonNull User reviewer, @NonNull Booking booking) {
        this.rating = rating;
        this.comment = comment;
        this.serviceName = booking.getServiceName();
        this.reviewerName = reviewer.getFullName();
        this.reviewerKey = reviewer.getKey();
        dateCreated = new Date();
    }

    public Review(@NonNull String key, @NonNull Date dateCreated, int rating, @Nullable String comment, @NonNull String serviceName, @NonNull String reviewerName, @NonNull String reviewerKey) {
        this.key = key;
        this.dateCreated = dateCreated;
        this.rating = rating;
        this.comment = comment;
        this.serviceName = serviceName;
        this.reviewerName = reviewerName;
        this.reviewerKey = reviewerKey;
    }

    public String getKey() {
        return key;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewerKey() {
        return reviewerKey;
    }

}
