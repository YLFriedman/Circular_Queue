package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.appcompat.app.AppCompatActivity;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Locale;

public class ServiceProviderProfileActivity extends SignedInActivity {

    protected boolean itemClickEnabled = true;
    protected ServiceProvider currentProvider;
    protected Service currentService;


    protected TextView txt_company_name;
    RatingBar rating_stars;
    protected TextView txt_see_reviews;
    protected TextView txt_num_ratings;
    protected TextView txt_title_description;
    protected TextView txt_description;
    protected TextView txt_full_name;
    protected LinearLayout grp_licensed;
    protected TextView txt_email;
    protected TextView txt_phone;
    protected TextView txt_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_profile);

        Intent intent = getIntent();
        try {
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            // TODO: Toast message for invalid type
            finish();
            return;
        }

        if (null == currentProvider) {
            // TODO: Toast message for no provider
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

        setupFields();
    }

    private void setupFields() {
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
            txt_full_name.setText(String.format(getString(R.string.full_name_template), currentProvider.getFirstName(), currentProvider.getLastName()));
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

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

}
