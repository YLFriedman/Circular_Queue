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

    private TableRow btns_admin_1;

    /*
     * Fills in layout for UserHome activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btns_admin_1 = findViewById(R.id.btns_admin_1);
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = State.getState().getSignedInUser();
        if (null == user) {
            onSignOutClick(null);
        } else {
            // Hide all non-shared button rows
            btns_admin_1.setVisibility(View.GONE);
            // Enable the relevant button rows
            switch (user.getType()) {
                case ADMIN:
                    btns_admin_1.setVisibility(View.VISIBLE);
                    break;
                case HOMEOWNER:
                    //btns_admin_1.setVisibility(View.VISIBLE);
                    break;
                case SERVICE_PROVIDER:
                    //btns_admin_1.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    public void onServicesClick(View view) {
        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
    }

    public void onUserListClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserAccountListActivity.class));
    }

    /*
     * Navigates to AccountDetails when clicked
     * Displays Account Details
     */
    public void onMyAccountClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserAccountViewActivity.class));
    }

    public void onSignOutClick(View view) {
        State.getState().setSignedInUser(null);
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

}

