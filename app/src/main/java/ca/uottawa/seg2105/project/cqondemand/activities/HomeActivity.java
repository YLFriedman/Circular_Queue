package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class HomeActivity extends AppCompatActivity {

    protected boolean itemClickEnabled = true;

    /*
     * Fills in layout for UserHome activity
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

    @Override
    public void onBackPressed () {
        moveTaskToBack(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
        User user = State.getInstance().getSignedInUser();
        if (null == user) {
            onClickHandler(findViewById(R.id.btn_sign_out));
        } else {
            itemClickEnabled = true;
        }
    }

    public void onClickHandler(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
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
            case R.id.btn_ho_bookings:
                startActivity(new Intent(ctx, BookingListActivity.class));
                break;
        }
    }

}

