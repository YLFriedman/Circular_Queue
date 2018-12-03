package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

/**
 * The class <b>HomeActivity</b> is a UI class that presents the user with buttons that launch the main functions of the application.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    protected boolean onClickEnabled = true;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User signedInUser = State.getInstance(getApplicationContext()).getSignedInUser();
        if (null == signedInUser) { return; }
        switch (signedInUser.getType()) {
            case ADMIN: setContentView(R.layout.activity_home_admin); break;
            case SERVICE_PROVIDER: setContentView(R.layout.activity_home_service_provider); break;
            case HOMEOWNER: setContentView(R.layout.activity_home_homeowner); break;
            default: onClickHandler(findViewById(R.id.btn_sign_out));
        }

    }

    /**
     * Overrides the back button to move the activity to the background instead of finishing it
     */
    @Override
    public void onBackPressed () {
        moveTaskToBack(false);
    }

    /**
     * Enables the relevant onClick actions within this activity and signs out of the app if there is no signed in user.
     * This is run during the resume phase of the activity lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        onClickEnabled = true;
        User user = State.getInstance().getSignedInUser();
        if (null == user) {
            onClickHandler(findViewById(R.id.btn_sign_out));
        } else {
            onClickEnabled = true;
        }
    }

    /**
     * The on-click handler for the main function buttons
     * @param view the view object that was clicked
     */
    public void onClickHandler(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Context ctx = getApplicationContext();
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_my_account:
                startActivity(new Intent(ctx, UserViewActivity.class));
                break;
            case R.id.btn_sign_out:
                State.getInstance().setSignedInUser(null);
                startActivity(new Intent(ctx, SignInActivity.class));
                finish();
                break;
            case R.id.btn_admin_manage_users:
                startActivity(new Intent(ctx, UserListActivity.class));
                break;
            case R.id.btn_admin_manage_services:
                startActivity(new Intent(ctx, CategoryListActivity.class));
                break;
            case R.id.btn_sp_availability:
                startActivity(new Intent(ctx, WeekViewActivity.class));
                break;
            case R.id.btn_sp_services:
                intent = new Intent(ctx, ServiceListActivity.class);
                intent.putExtra("user", State.getInstance().getSignedInUser());
                startActivity(intent);
                break;
            case R.id.btn_ho_book_service:
                startActivity(new Intent(ctx, CategoryListActivity.class));
                break;
            case R.id.btn_sp_bookings:
            case R.id.btn_ho_bookings:
                startActivity(new Intent(ctx, BookingListActivity.class));
                break;
        }
    }

}

