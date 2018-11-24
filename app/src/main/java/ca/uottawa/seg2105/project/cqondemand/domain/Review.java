package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {

    protected String key;
    protected int rating;
    protected String comment;
    protected Date date;

    public Review (@NonNull String key, int rating, String comment){

        this.rating = rating;
        this.comment = comment;
        date = new Date();
    }

    public Review (int rating, String comment){

        this.rating = rating;
        this.comment = comment;
        date = new Date();
    }

    public String getKey() {
        return key;
    }

    public int getRating() { return rating; }

    public String getComment() { return comment; }

    public Date getDate() { return date; }
}
