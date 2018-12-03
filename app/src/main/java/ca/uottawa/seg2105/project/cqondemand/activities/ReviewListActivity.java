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
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * work in progress
 * might need something like DbCategory.getReviewsLive
 */

public class ReviewListActivity extends SignedInActivity {

    protected boolean onClickEnabled = true;
    protected TextView txt_sub_title;
    protected View divider_txt_sub_title;
    protected RecyclerView recycler_list;
    protected ReviewListAdapter review_list_adapter;
    protected ServiceProvider currentProvider;
    protected DbListenerHandle<?> dbListenerHandle;

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
                review_list_adapter = new ReviewListAdapter(getApplicationContext(), data, new View.OnClickListener() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        onClickEnabled = true;
    }

    private void setSubTitle(@NonNull String subTitle) {
        txt_sub_title.setVisibility(View.VISIBLE);
        divider_txt_sub_title.setVisibility(View.VISIBLE);
        txt_sub_title.setText(subTitle);
    }

}
