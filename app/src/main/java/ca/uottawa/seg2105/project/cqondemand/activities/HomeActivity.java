package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.activities.CategoryListActivity;
import ca.uottawa.seg2105.project.cqondemand.activities.SignInActivity;
import ca.uottawa.seg2105.project.cqondemand.activities.UserAccountListActivity;
import ca.uottawa.seg2105.project.cqondemand.activities.UserAccountViewActivity;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class HomeActivity extends AppCompatActivity {

    protected boolean itemClickEnabled = true;
    protected TableRow btns_admin_1;
    protected TableRow btns_service_provider_1;

    /*
     * Fills in layout for UserHome activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Get references to the view items
        btns_admin_1 = findViewById(R.id.btns_admin_1);
        btns_service_provider_1 = findViewById(R.id.btns_service_provider_1);
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = State.getState().getSignedInUser();
        if (null == user) {
            onSignOutClick(null);
        } else {
            itemClickEnabled = true;
            // Hide all non-shared admin button rows
            btns_admin_1.setVisibility(View.GONE);
            btns_service_provider_1.setVisibility(View.GONE);

            // Enable the relevant button rows
            switch (user.getType()) {
                case ADMIN:
                    btns_admin_1.setVisibility(View.VISIBLE);
                    break;
                case HOMEOWNER:
                    break;
                case SERVICE_PROVIDER:
                    btns_service_provider_1.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    // Admin Only Button Functions

    public void onServicesClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
    }

    public void onUserListClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), UserAccountListActivity.class));
    }

    // Service Provider Only Button Functions

    public void onMyAvailabilityClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), AvailabilityWeekViewActivity.class));
    }
    public void onMyServicesClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
        intent.putExtra("user", (User) State.getState().getSignedInUser());
        startActivity(intent);
    }


    // ALL Users Button Functions
    /*
     * Navigates to AccountDetails when clicked
     * Displays Account Details
     */
    public void onMyAccountClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        startActivity(new Intent(getApplicationContext(), UserAccountViewActivity.class));
    }

    public void onSignOutClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        State.getState().setSignedInUser(null);
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

}

