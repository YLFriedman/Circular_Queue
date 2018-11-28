package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class BookingViewActivity extends SignedInActivity {

    private enum Mode { HOMEOWNER, SERVICE_PROVIDER }
    private Mode mode;
    private boolean itemClickEnabled;
    private Booking currentBooking;
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, YYYY  h:mm a", Locale.CANADA);
    private SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("MMMM d, YYYY", Locale.CANADA);
    private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.CANADA);
    private LinearLayout grp_provider;
    private LinearLayout grp_homeowner;
    private TextView txt_homeowner;
    private TextView txt_service_provider;
    private TextView txt_service_name;
    private TextView txt_date;
    private TextView txt_time;
    private TextView txt_service_rate;
    private TextView txt_created_on;
    private TextView txt_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);

        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) { return; }

        Intent intent = getIntent();
        try {
            currentBooking = (Booking) intent.getSerializableExtra("booking");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (signedInUser instanceof ServiceProvider) {
            mode = Mode.SERVICE_PROVIDER;
            if (null == currentBooking.getHomeowner()) {
                Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            mode = Mode.HOMEOWNER;
            if (null == currentBooking.getServiceProvider()) {
                Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        grp_homeowner = findViewById(R.id.grp_homeowner);
        grp_provider = findViewById(R.id.grp_provider);
        txt_homeowner = findViewById(R.id.txt_homeowner);
        txt_service_provider = findViewById(R.id.txt_service_provider);
        txt_service_name = findViewById(R.id.txt_service_name);
        txt_date = findViewById(R.id.txt_date);
        txt_time = findViewById(R.id.txt_time);
        txt_service_rate = findViewById(R.id.txt_service_rate);
        txt_created_on = findViewById(R.id.txt_created_on);
        txt_status = findViewById(R.id.txt_status);

        setFields();

    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

    private void setFields() {
        txt_service_name.setText(currentBooking.getServiceName());
        txt_date.setText(DAY_FORMAT.format(currentBooking.getStartTime()));
        txt_time.setText(String.format("%s to %s", TIME_FORMAT.format(currentBooking.getStartTime()), TIME_FORMAT.format(currentBooking.getEndTime())));
        if (0 == currentBooking.getServiceRate()) { txt_service_rate.setText(getString(R.string.zero_value_service)); }
        else { txt_service_rate.setText(String.format(Locale.CANADA, getString(R.string.service_rate_template), currentBooking.getServiceRate())); }
        txt_created_on.setText(DATE_FORMAT.format(currentBooking.getDateCreated()));
        if (Booking.Status.CANCELLED != currentBooking.getStatus() && currentBooking.getEndTime().getTime() >= System.currentTimeMillis()) { txt_status.setText(getString(R.string.completed)); }
        else { txt_status.setText(currentBooking.getStatus().toString()); }

        if (Mode.HOMEOWNER == mode) {
            txt_service_provider.setText(currentBooking.getServiceProvider().getCompanyName());
            grp_homeowner.setVisibility(View.GONE);

        } else if (Mode.SERVICE_PROVIDER == mode) {
            txt_homeowner.setText(currentBooking.getHomeowner().getFullName());
            grp_provider.setVisibility(View.GONE);

        }
    }

    public void onSeeProviderProfileClick(View v) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceProviderProfileActivity.class);
        intent.putExtra("provider", currentBooking.getServiceProvider());
        //intent.putExtra("service", currentService);
        startActivity(intent);
    }

    public void onRateServiceClick(View v) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewCreateActivity.class);
        intent.putExtra("booking", currentBooking);
        startActivity(intent);
    }

}
