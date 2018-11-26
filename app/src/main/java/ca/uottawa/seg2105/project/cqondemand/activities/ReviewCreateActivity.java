package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.annotation.NonNull;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbReview;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
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
import android.widget.Toast;

public class ReviewCreateActivity extends SignedInActivity {

    protected ServiceProvider currentProvider;
    protected Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create);

        Intent intent = getIntent();
        currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
        currentService = (Service) intent.getSerializableExtra("service");

        if (null == currentService || null == currentProvider) {
            finish();
            return;
        }
    }

    public void onCreateReviewClick(View view) {

        RatingBar stars_rating = findViewById(R.id.rating_stars);
        EditText field_comments = findViewById(R.id.field_comments);
        String comments = field_comments.getText().toString().trim();
        final Button btn_create_review = findViewById(R.id.btn_create_review);
        String reviewerName = State.getInstance().getSignedInUser().getFullName();
        String reviewerKey = State.getInstance().getSignedInUser().getKey();

        final Review newReview = new Review((int)stars_rating.getRating(), comments, currentService.getName(), reviewerName, reviewerKey);

        btn_create_review.setEnabled(false);

        DbReview.createReview(newReview, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getApplicationContext(), String.format(getString(R.string.category_create_success), newReview.getName()), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ReviewViewActivity.class);
                intent.putExtra("review", newReview);
                intent.putExtra("provider", currentProvider);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.create_db_error_template), getString(R.string.rating).toLowerCase()), Toast.LENGTH_LONG).show();
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.create_generic_error_template), getString(R.string.rating).toLowerCase()), Toast.LENGTH_LONG).show();
                }
                btn_create_review.setEnabled(true);
            }
        });
    }

}
