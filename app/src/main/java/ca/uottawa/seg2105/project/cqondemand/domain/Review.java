package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import java.io.Serializable;
import java.util.Date;

/**
 * Class to represent a user review of a service that has been provided
 */
public class Review implements Serializable {

    private static final long serialVersionUID = 1;
    //Associated database key
    protected String key;

    protected String serviceName;
    protected String reviewerName;
    protected String reviewerKey;
    protected int rating;
    protected String comment;
    protected Date dateCreated;

    /**
     * Constructor which does not require a key. Intended to be used before this review has been stored
     * in the database.
     * @param rating integer rating, between 0-5
     * @param comment user comment/description
     * @param reviewer the name of the user who wrote this review
     * @param booking the booking associated with this review
     */
    public Review(int rating, @Nullable String comment, User reviewer, Booking booking) {

        if (!FieldValidation.ratingIsValid(rating)) { throw new InvalidDataException("Invalid rating. Must be between 0 to 5 stars. ");}
        if (comment == null) { comment = ""; }
        this.rating = rating;
        this.comment = comment;
        this.serviceName = booking.getServiceName();
        this.reviewerName = reviewer.getFullName();
        this.reviewerKey = reviewer.getKey();
        this.key = booking.getKey();
        dateCreated = new Date();
    }

    /**
     * Constructor which requires a key. Intended to convert Database information into a Booking object
     * @param key the review key
     * @param dateCreated the Date this review was created
     * @param rating integer rating between 0-5
     * @param comment user comment/description
     * @param serviceName the name of the service this review is for
     * @param reviewerName the name of the User who wrote this review
     * @param reviewerKey the key associated with the reviewer
     */
    public Review(@NonNull String key, @NonNull Date dateCreated, int rating, @Nullable String comment, @NonNull String serviceName, @NonNull String reviewerName, @NonNull String reviewerKey) {

        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
        this.dateCreated = dateCreated;
        this.rating = rating;
        this.comment = comment;
        this.serviceName = serviceName;
        this.reviewerName = reviewerName;
        this.reviewerKey = reviewerKey;
    }

    /**
     * Gets the key associated with a particular review
     * @return the associated key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the rating associated with a particular review
     * @return integer rating between 0-5
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the service name associated with a particular review
     * @return the user's comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the service name associated with a particular review
     * @return the name of the associated service
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the date this review was created
     * @return the creation Date
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Gets the name of a particular review's author
     * @return the reviewer name
     */
    public String getReviewerName() {
        return reviewerName;
    }

    /**
     * Gets the key associated with a particular reviewer
     * @return the reviewer key
     */
    public String getReviewerKey() {
        return reviewerKey;
    }

}
