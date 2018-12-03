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

/**
 * The class <b>UserViewActivity</b> is a UI class that allows a user to view their account details.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class UserViewActivity extends SignedInActivity {

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    protected boolean onClickEnabled = true;

    /**
     * A view that displays the account type
     */
    protected TextView txt_account_type;

    /**
     * A view that displays the username
     */
    protected TextView txt_username;

    /**
     * A view that displays the user's full name
     */
    protected TextView txt_full_name;

    /**
     * A view that displays the email address
     */
    protected TextView txt_email;

    /**
     * A view that displays the company name
     */
    protected TextView txt_company_name;

    /**
     * A view that displays the user rating
     */
    RatingBar rating_stars;

    /**
     * A view that displays the see reviews link
     */
    protected TextView txt_see_reviews;

    /**
     * A view that displays the number of ratings
     */
    protected TextView txt_num_ratings;

    /**
     * A view that displays the phone number
     */
    protected TextView txt_phone;

    /**
     * A view that displays the description
     */
    protected TextView txt_description;

    /**
     * A view that displays the licensed status
     */
    protected TextView txt_licensed;

    /**
     * A view that displays the address
     */
    protected TextView txt_address;

    /**
     * A view group containing the service provider views
     */
    protected LinearLayout service_provider_info;

    /**
     * A view group containing the rating related views
     */
    protected LinearLayout grp_rating;

    /**
     * The user being viewed
     */
    protected User currentUser;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
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
            configureViews();
        } else {
            currentUser = State.getInstance().getSignedInUser();
            configureViews();
        }

    }

    /**
     * Enables the relevant onClick actions within this activity.
     * This is run during the resume phase of the activity lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        onClickEnabled = true;
    }

    /**
     * Refreshes the activity when a new intent is received
     * @param intent the intent containing the new information used to update the activity
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentUser = (User) intent.getSerializableExtra("user");
        if (null != currentUser) { configureViews(); }
    }

    /**
     * Configures the view items within this activity
     */
    private void configureViews() {
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

    /**
     * Sets the menu to be used in the action bar
     * @return true if the options menu is created, false otherwise
     */
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

    /**
     * The onClick handler for the action bar menu items
     * @param item the menu item that was clicked
     * @return true if the menu item onClick was handled, the result of the super class method otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_user_edit: onEditAccountClick(); return true;
            case R.id.menu_item_user_change_password: onChangePasswordClick(); return true;
            case R.id.menu_item_user_delete: onDeleteAccountClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads the user edit activity
     */
    public void onEditAccountClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), UserEditActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    /**
     * Loads the change password activity
     */
    public void onChangePasswordClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), UserChangePasswordActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    /**
     * Prompts the user with the delete user confirmation screen and triggers the deletion process if confirmed.
     */
    public void onDeleteAccountClick() {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
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
                        public void onDismiss(DialogInterface dialog) { onClickEnabled = true; }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
        }
    }

    /**
     * The on-click handler for the see reviews link
     * @param view the view object that was clicked
     */
    public void onSeeReviewsClick(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
        intent.putExtra("provider", currentUser);
        startActivity(intent);
    }

}
