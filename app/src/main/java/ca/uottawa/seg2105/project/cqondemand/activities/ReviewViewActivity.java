package ca.uottawa.seg2105.project.cqondemand.activities;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;
/**
 * The class <b>ReviewViewActivity</b> is a UI class that allows a user to see a review.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ReviewViewActivity extends SignedInActivity {

    /**
     * The review that is being dispalyed
     */
    protected Review currentReview;

    /**
     * The service provider that the review is for
     */
    protected ServiceProvider currentProvider;

    /**
     * The view that displays the service provider
     */
    protected TextView txt_service_provider;

    /**
     * The view that displays name of the service that was reviewed
     */
    protected TextView txt_service_name;

    /**
     * The view that displays the homeowner who created the review
     */
    protected TextView txt_created_by;

    /**
     * The view that displays the date and time that the review was created
     */
    protected TextView txt_created_on;

    /**
     * The view that displays the number of stars that were set in the review
     */
    protected RatingBar rating_stars;

    /**
     * The view that displays the homeowner comments for the review
     */
    protected TextView txt_comments;

    /**
     * The format to be used for the date (month day, year  hour:minute am/pm)
     */
    protected SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy  h:mm a", Locale.CANADA);

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);

        Intent intent = getIntent();
        try {
            currentReview = (Review) intent.getSerializableExtra("review");
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (null == currentReview || null == currentProvider) {
            finish();
            return;
        }

        txt_service_provider = findViewById(R.id.txt_service_provider);
        txt_service_name = findViewById(R.id.txt_service_name);
        txt_created_by = findViewById(R.id.txt_created_by);
        txt_created_on = findViewById(R.id.txt_created_on);
        rating_stars = findViewById(R.id.rating_stars);
        txt_comments = findViewById(R.id.txt_comments);

        txt_service_provider.setText("");
        txt_service_name.setText("");
        txt_created_by.setText("");
        txt_created_on.setText("");
        rating_stars.setRating(0);
        txt_comments.setText("");
        if (null != currentReview && null != currentProvider) {
            txt_service_provider.setText(currentProvider.getCompanyName());
            txt_service_name.setText(currentReview.getServiceName());
            txt_created_by.setText(currentReview.getReviewerName());
            txt_created_on.setText(DATE_FORMAT.format(currentReview.getDateCreated()));
            rating_stars.setRating(currentReview.getRating());
            txt_comments.setText(currentReview.getComment());
        }

    }

}
