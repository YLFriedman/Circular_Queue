package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.annotation.NonNull;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbReview;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The class <b>ReviewCreateActivity</b> is a UI class that allows a user to create a review of a booking.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ReviewCreateActivity extends SignedInActivity {

    /**
     * The service provider that is being reviewed
     */
    protected ServiceProvider currentProvider;

    /**
     * The booking that contains the details of the job that is being reviewed
     */
    protected Booking currentBooking;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create);

        Intent intent = getIntent();
        try {
            currentBooking = (Booking) intent.getSerializableExtra("booking");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (null == currentBooking || null == currentBooking.getServiceProvider()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
        }

        currentProvider = currentBooking.getServiceProvider();
        TextView txt_service_provider = findViewById(R.id.txt_service_provider);
        TextView txt_service_name = findViewById(R.id.txt_service_name);
        txt_service_provider.setText(currentProvider.getCompanyName());
        txt_service_name.setText(currentBooking.getServiceName());
    }

    /**
     * The on-click handler for the create review button
     * @param view the view object that was clicked
     */
    public void onCreateReviewClick(View view) {

        RatingBar stars_rating = findViewById(R.id.rating_stars);
        EditText field_comments = findViewById(R.id.field_comments);
        String comments = field_comments.getText().toString().trim();
        final Button btn_create_review = findViewById(R.id.btn_create_review);

        if (!FieldValidation.ratingIsValid((int)stars_rating.getRating())) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_rating_message), Toast.LENGTH_LONG).show();
            stars_rating.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        final Review newReview = new Review((int)stars_rating.getRating(), comments, State.getInstance().getSignedInUser(), currentBooking);

        btn_create_review.setEnabled(false);

        DbReview.createReview(newReview, currentProvider, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.item_create_success_template), currentProvider.getCompanyName(), getString(R.string.review).toLowerCase()), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ReviewViewActivity.class);
                intent.putExtra("review", newReview);
                intent.putExtra("provider", currentProvider);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case ALREADY_EXISTS:
                        Toast.makeText(getApplicationContext(), getString(R.string.already_rated), Toast.LENGTH_LONG).show();
                        break;
                    case DOES_NOT_EXIST:
                        Toast.makeText(getApplicationContext(), getString(R.string.user_does_not_exist_review_create), Toast.LENGTH_LONG).show();
                        break;
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.create_db_error_template), getString(R.string.review).toLowerCase()), Toast.LENGTH_LONG).show();
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.create_generic_error_template), getString(R.string.review).toLowerCase()), Toast.LENGTH_LONG).show();
                }
                btn_create_review.setEnabled(true);
            }
        });
    }

}
