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

public class ReviewViewActivity extends SignedInActivity {

    protected Review currentReview;
    protected ServiceProvider currentProvider;
    protected TextView txt_service_provider;
    protected TextView txt_service_name;
    protected TextView txt_created_by;
    protected TextView txt_created_on;
    protected RatingBar rating_stars;
    protected TextView txt_comments;
    protected SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy  h:m a", Locale.CANADA);

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

        setupFields();
    }

    private void setupFields() {
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
