package ca.uottawa.seg2105.project.cqondemand.activities;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * The class <b>ServiceProviderProfileActivity</b> is a UI class that displays a service provider's profile information to a homeowner.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ServiceProviderProfileActivity extends SignedInActivity {

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    protected boolean onClickEnabled = true;

    /**
     * The Service Provider that is being displayed
     */
    protected ServiceProvider currentProvider;

    /**
     * The Service to be used when booking an availability from this activity
     */
    protected Service currentService;

    /**
     * A view that displays the provider's company name
     */
    protected TextView txt_company_name;

    /**
     * A view that displays the provider's rating
     */
    RatingBar rating_stars;

    /**
     * A view that displays a link to see the list of reviews for the service provider
     */
    protected TextView txt_see_reviews;

    /**
     * A view that displays the provider's number of ratings
     */
    protected TextView txt_num_ratings;

    /**
     * A view that displays the title for the provider's description
     */
    protected TextView txt_title_description;

    /**
     * A view that displays the provider's description
     */
    protected TextView txt_description;

    /**
     * A view that displays the provider's full name
     */
    protected TextView txt_full_name;

    /**
     * A view group that contains the licensed image and title views
     */
    protected LinearLayout grp_licensed;

    /**
     * A view that displays the provider's email address
     */
    protected TextView txt_email;

    /**
     * A view that displays the provider's phone number
     */
    protected TextView txt_phone;

    /**
     * A view that displays the provider's address
     */
    protected TextView txt_address;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_profile);

        Intent intent = getIntent();
        try {
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (null == currentProvider) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rating_stars = findViewById(R.id.rating_stars);
        txt_see_reviews = findViewById(R.id.txt_see_reviews);
        txt_num_ratings = findViewById(R.id.txt_num_ratings);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_email = findViewById(R.id.txt_email);
        txt_company_name = findViewById(R.id.txt_company_name);
        txt_phone = findViewById(R.id.txt_phone);
        grp_licensed = findViewById(R.id.grp_licensed);
        txt_title_description = findViewById(R.id.txt_title_description);
        txt_description = findViewById(R.id.txt_description);
        txt_address = findViewById(R.id.txt_address);

        if (null == currentService) {
            Button btn_find_availability = findViewById(R.id.btn_find_availability);
            btn_find_availability.setVisibility(View.GONE);
        }

        txt_see_reviews.setVisibility(View.GONE);
        grp_licensed.setVisibility(View.GONE);
        txt_description.setVisibility(View.GONE);
        txt_title_description.setVisibility(View.GONE);
        rating_stars.setRating(0);
        txt_num_ratings.setText("");
        txt_full_name.setText("");
        txt_email.setText("");
        txt_company_name.setText("");
        txt_phone.setText("");
        txt_address.setText("");

        if (null != currentProvider) {
            rating_stars.setRating((float)currentProvider.getRating() / 100);
            if (currentProvider.getNumRatings() > 0) { txt_see_reviews.setVisibility(View.VISIBLE); }
            String numRatings = "";
            if (0 == currentProvider.getNumRatings()) { numRatings = getString(R.string.rating_template_none); }
            else if (1 == currentProvider.getNumRatings()) { numRatings = getString(R.string.rating_template_single); }
            else { numRatings = String.format(Locale.CANADA, getString(R.string.rating_template), currentProvider.getNumRatings()); }
            txt_num_ratings.setText(numRatings);
            txt_full_name.setText(currentProvider.getFullName());
            txt_email.setText(currentProvider.getEmail());
            txt_company_name.setText(currentProvider.getCompanyName());
            txt_phone.setText(currentProvider.getPhoneNumber());
            if (null != currentProvider.getDescription() && !currentProvider.getDescription().isEmpty()) {
                txt_description.setText(currentProvider.getDescription());
                txt_description.setVisibility(View.VISIBLE);
                txt_title_description.setVisibility(View.VISIBLE);
            }
            if (currentProvider.isLicensed()) { grp_licensed.setVisibility(View.VISIBLE); }
            txt_address.setText(currentProvider.getAddress().toString());
        }

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
     * The on-click handler for the find availabilities button
     * @param view the view object that was clicked
     */
    public void onFindAvailabilityClick(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), WeekViewActivity.class);
        intent.putExtra("provider", currentProvider);
        intent.putExtra("service", currentService);
        startActivity(intent);
    }

    /**
     * The on-click handler for the see reviews link
     * @param view the view object that was clicked
     */
    public void onSeeReviewsClick(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
        intent.putExtra("provider", currentProvider);
        startActivity(intent);
    }

}
