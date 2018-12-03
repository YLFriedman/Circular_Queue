package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.CategoryListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * The class <b>CategoryListActivity</b> is a UI class that allows a user to see a list of bookings.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class CategoryListActivity extends SignedInActivity {

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    protected boolean onClickEnabled = true;

    /**
     * The view that displays the list of categories
     */
    protected RecyclerView recycler_list;

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
        setContentView(R.layout.activity_category_list);
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dbListenerHandle = DbCategory.getCategoriesLive(new AsyncValueEventListener<Category>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Category> data) {
                recycler_list.setAdapter(new CategoryListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                    public void onClick(final View view) {
                        if (!onClickEnabled) { return; }
                        onClickEnabled = false;
                        Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
                        intent.putExtra("category", (Serializable) view.getTag());
                        startActivityForResult(intent, 0);
                    }
                }));
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), getString(R.string.category_list_db_error), Toast.LENGTH_LONG).show();
            }
        });
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
     * Loads the category creation activity
     */
    public void onCreateCategoryClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), CategoryCreateActivity.class));
    }

    /**
     * Loads the service creation activity
     */
    public void onCreateServiceClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), ServiceCreateActivity.class));
    }

    /**
     * Sets the menu to be used in the action bar
     * @return true if the options menu is created, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getInstance().getSignedInUser();
        if (null != user && user.getType() == User.Type.ADMIN) {
            getMenuInflater().inflate(R.menu.category_list_options, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The onClick handler for the action bar menu items
     * @param item the menu item that was clicked
     * @return true if the menu item onClick was handled, the result of the super class method otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_category_create: onCreateCategoryClick(); return true;
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles results from an activity that was launched and finished
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (RESULT_OK == resultCode && intent.getBooleanExtra("finish", false)) {
           finish();
        }
    }

}
