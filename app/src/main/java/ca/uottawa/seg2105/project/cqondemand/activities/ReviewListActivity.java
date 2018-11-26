package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtilRelational;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * work in progress
 * might need something like DbCategory.getReviewsLive
 */

public class ReviewListActivity extends SignedInActivity {

    protected boolean itemClickEnabled = true;
    protected boolean useReview;
    TextView txt_sub_title;
    View divider_txt_sub_title;
    protected RecyclerView recycler_list;
    protected Review currentReview;
    protected User currentUser;
    protected ServiceProvider currentProvider;
    protected DbListenerHandle<?> dbListenerHandle;
    int itemActionIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        User signedInUser = State.getInstance().getSignedInUser();

        if (null == signedInUser) {
            return;
        }

        txt_sub_title = findViewById(R.id.txt_sub_title);
        divider_txt_sub_title = findViewById(R.id.divider_txt_sub_title);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize the review components as hidden
        txt_sub_title.setVisibility(View.GONE);
        divider_txt_sub_title.setVisibility(View.GONE);
        Intent intent = getIntent();

        // Get the current review and current user
        currentReview = (Review) intent.getSerializableExtra("review");
        currentUser = (User) intent.getSerializableExtra("user");

        // Get the action bar
        ActionBar actionBar = getSupportActionBar();

        //dbListenerHandle = DbCategory.getReviewsLive(new AsyncValueEventListener<Review>() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the services list
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

    private void setSubTitle(@NonNull String subTitle) {
        txt_sub_title.setVisibility(View.VISIBLE);
        divider_txt_sub_title.setVisibility(View.VISIBLE);
        txt_sub_title.setText(subTitle);
    }
}
