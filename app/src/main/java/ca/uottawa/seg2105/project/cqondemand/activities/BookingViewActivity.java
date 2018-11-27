package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class BookingViewActivity extends SignedInActivity {

    protected enum Mode { HOMEOWNER, SERVICE_PROVIDER }
    protected Mode mode;
    protected boolean itemClickEnabled;
    protected Booking currentBooking;
    protected SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("MMMM d, YYYY", Locale.CANADA);
    protected SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.CANADA);
    protected TextView txt_service_provider;
    protected TextView txt_service_name;
    protected TextView txt_date;
    protected TextView txt_time;

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

        txt_service_provider = findViewById(R.id.txt_service_provider);
        txt_service_name = findViewById(R.id.txt_service_name);
        txt_date = findViewById(R.id.txt_date);
        txt_time = findViewById(R.id.txt_time);

        setFields();

    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

    protected void setFields() {
        if (Mode.HOMEOWNER == mode) {
            txt_service_provider.setText(currentBooking.getServiceProvider().getCompanyName());
            txt_service_name.setText(currentBooking.getServiceName());
            txt_date.setText(DAY_FORMAT.format(currentBooking.getStartTime()));
            txt_time.setText(String.format("%s to %s", TIME_FORMAT.format(currentBooking.getStartTime()), TIME_FORMAT.format(currentBooking.getEndTime())));
        }
    }

}
