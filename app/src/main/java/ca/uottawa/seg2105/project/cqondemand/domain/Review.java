package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {

    private static final long serialVersionUID = 1;
    protected String key;
    protected int rating;
    protected String comment;
    protected Date dateCreated;


    public Review (int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
        dateCreated = new Date();
    }

    public Review (@NonNull String key, Date dateCreated, int rating, String comment) {
        this(rating, comment);
        this.key = key;
        this.dateCreated = dateCreated;
    }

    public String getKey() {
        return key;
    }

    public int getRating() { return rating; }

    public String getComment() { return comment; }

    public Date getDateCreated() { return dateCreated; }
}
