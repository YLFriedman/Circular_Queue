package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserViewActivity extends SignedInActivity {

    protected boolean itemClickEnabled = true;
    protected TextView txt_account_type;
    protected TextView txt_username;
    protected TextView txt_full_name;
    protected TextView txt_email;
    protected TextView txt_company_name;
    RatingBar rating_stars;
    protected TextView txt_see_reviews;
    protected TextView txt_num_ratings;
    protected TextView txt_phone;
    protected TextView txt_description;
    protected TextView txt_licensed;
    protected TextView txt_address;
    protected LinearLayout service_provider_info;
    protected LinearLayout grp_rating;
    protected User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);
        // Set references to the UI objects
        txt_account_type = findViewById(R.id.txt_account_type);
        txt_username = findViewById(R.id.txt_username);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_email = findViewById(R.id.txt_email);
        service_provider_info = findViewById(R.id.service_provider_info);
        grp_rating = findViewById(R.id.grp_rating);
        txt_company_name = findViewById(R.id.txt_company_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_description = findViewById(R.id.txt_description);
        rating_stars = findViewById(R.id.rating_stars);
        txt_see_reviews = findViewById(R.id.txt_see_reviews);
        txt_num_ratings = findViewById(R.id.txt_num_ratings);
        txt_licensed = findViewById(R.id.txt_licensed);
        txt_address = findViewById(R.id.txt_address);

        Intent intent = getIntent();
        try {
            currentUser = (User) intent.getSerializableExtra("user");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (null != currentUser) {
            setupFields();
        } else {
            currentUser = State.getInstance().getSignedInUser();
            setupFields();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        itemClickEnabled = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentUser = (User) intent.getSerializableExtra("user");
        if (null != currentUser) { setupFields(); }
    }

    private void setupFields() {
        service_provider_info.setVisibility(View.GONE);
        grp_rating.setVisibility(View.GONE);
        if (null == currentUser) {
            txt_account_type.setText("");
            txt_username.setText("");
            txt_full_name.setText("");
            txt_email.setText("");
        } else {
            txt_account_type.setText(currentUser.getType().toString());
            txt_username.setText(currentUser.getUsername());
            txt_full_name.setText(currentUser.getFullName());
            txt_email.setText(currentUser.getEmail());
            if (currentUser instanceof ServiceProvider) {
                ServiceProvider provider = (ServiceProvider) currentUser;
                service_provider_info.setVisibility(View.VISIBLE);
                grp_rating.setVisibility(View.VISIBLE);
                txt_company_name.setText(provider.getCompanyName());
                txt_phone.setText(provider.getPhoneNumber());
                txt_description.setText(provider.getDescription());
                rating_stars.setRating((float)provider.getRating() / 100);
                if (provider.getNumRatings() > 0) { txt_see_reviews.setVisibility(View.VISIBLE); }
                else { txt_see_reviews.setVisibility(View.GONE); }
                String numRatings = "";
                if (0 == provider.getNumRatings()) { numRatings = getString(R.string.rating_template_none); }
                else if (1 == provider.getNumRatings()) { numRatings = getString(R.string.rating_template_single); }
                else { numRatings = String.format(Locale.CANADA, getString(R.string.rating_template), provider.getNumRatings()); }
                txt_num_ratings.setText(numRatings);
                txt_licensed.setText(provider.isLicensed() ? getText(R.string.yes) : getText(R.string.no));
                txt_address.setText(provider.getAddress().toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getInstance().getSignedInUser();
        getMenuInflater().inflate(R.menu.user_options, menu);
        if (null != user && user.getType() == User.Type.ADMIN) {
            menu.setGroupVisible(R.id.grp_user_edit_controls, false);
        }
        if (!currentUser.equals(State.getInstance().getSignedInUser())) {
            menu.setGroupVisible(R.id.grp_user_password_controls, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_user_edit: onEditAccountClick(); return true;
            case R.id.menu_item_user_change_password: onChangePasswordClick(); return true;
            case R.id.menu_item_user_delete: onDeleteAccountClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEditAccountClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), UserEditActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    public void onChangePasswordClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), UserChangePasswordActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    public void onDeleteAccountClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        if (null != currentUser) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_account)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentUser.getUsername(), getString(R.string.account).toLowerCase()))
                    .setIcon(R.drawable.ic_report_red_30)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbUser.deleteUser(currentUser, new AsyncActionEventListener() {
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.account_delete_success), currentUser.getUsername()), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), R.string.account_delete_db_error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
        }
    }

    public void onSeeReviewsClick(View v) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
        intent.putExtra("provider", currentUser);
        startActivity(intent);
    }

}
