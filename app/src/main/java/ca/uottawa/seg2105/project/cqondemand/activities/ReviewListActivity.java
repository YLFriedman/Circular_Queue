package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import androidx.annotation.NonNull;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ReviewListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.database.DbReview;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

/**
 * The class <b>ReviewListActivity</b> is a UI class that allows a user to see a list of reviews for a given service provider.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ReviewListActivity extends SignedInActivity {

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    protected boolean onClickEnabled = true;

    /**
     * A view that displays the service provider that the reviews are for
     */
    protected TextView txt_sub_title;

    /**
     * A view that divides the subtitle and the list
     */
    protected View divider_txt_sub_title;

    /**
     * The view that displays the list of reviews
     */
    protected RecyclerView recycler_list;

    /**
     * The service provider that the reviews are for
     */
    protected ServiceProvider currentProvider;

    /**
     * Stores the handle to the database callback so that it can be cleaned up when the activity ends
     */
    protected DbListenerHandle<?> dbListenerHandle;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        txt_sub_title = findViewById(R.id.txt_sub_title);
        divider_txt_sub_title = findViewById(R.id.divider_txt_sub_title);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_list.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        try {
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setSubTitle(String.format(getString(R.string.review_template), currentProvider.getCompanyName()));

        //Defining UI behaviour when list is received
        dbListenerHandle = DbReview.getReviewsLive(currentProvider.getKey(), new AsyncValueEventListener<Review>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Review> data) {
                ReviewListAdapter review_list_adapter = new ReviewListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!onClickEnabled) { return; }
                        onClickEnabled = false;
                        Intent intent = new Intent(getApplicationContext(), ReviewViewActivity.class);
                        intent.putExtra("review", (Serializable) view.getTag());
                        intent.putExtra("provider", currentProvider);
                        startActivity(intent);
                    }
                });
                recycler_list.setAdapter(review_list_adapter);
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), "Cannot access review at this time" , Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Removes the listener for data from the database.
     * This is run during the destroy phase of the activity lifecycle.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    /**
     * Enables the relevant onClick actions within this activity.
     * This is run during the resume phase of the activity lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        onClickEnabled = true;
    }

    /**
     * Makes the subtitle views visible and sets the subtitle text
     * @param subTitle the subtitle to be set in the sub_title view
     */
    private void setSubTitle(@NonNull String subTitle) {
        txt_sub_title.setVisibility(View.VISIBLE);
        divider_txt_sub_title.setVisibility(View.VISIBLE);
        txt_sub_title.setText(subTitle);
    }

}
