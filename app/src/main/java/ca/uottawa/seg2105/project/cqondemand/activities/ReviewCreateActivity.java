package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.appcompat.app.AppCompatActivity;
import ca.uottawa.seg2105.project.cqondemand.R;

import android.os.Bundle;

public class ReviewCreateActivity extends SignedInActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create);
    }
}
