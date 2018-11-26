package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {

    private static final long serialVersionUID = 1;
    protected String key;
    protected User user;
    protected int rating;
    protected String comment;
    protected Date dateCreated;


    public Review (int rating, String comment, User user) {
        this.rating = rating;
        this.comment = comment;
        this.user = user;
        dateCreated = new Date();
    }

    public Review (@NonNull String key, Date dateCreated, int rating, String comment, User user) {
        this(rating, comment, user);
        this.key = key;
        this.dateCreated = dateCreated;
    }

    public String getKey() {
        return key;
    }

    public int getRating() { return rating; }

    public String getComment() { return comment; }

    public Date getDateCreated() { return dateCreated; }

    public String getReviewer() { return user.getFirstName() + " " + user.getLastName(); }
}
