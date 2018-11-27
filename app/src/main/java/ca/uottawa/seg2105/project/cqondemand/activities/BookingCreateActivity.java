package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbBooking;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class BookingCreateActivity extends SignedInActivity {

    protected boolean itemClickEnabled;
    protected ServiceProvider currentProvider;
    protected Service currentService;
    protected Date startTime;
    protected Date endTime;
    protected SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, YYYY  hh:mm a", Locale.CANADA);
    protected SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("MMMM d, YYYY", Locale.CANADA);
    protected SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.CANADA);
    protected TextView txt_service_provider;
    protected TextView txt_service_name;
    protected TextView txt_date;
    protected TextView txt_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_create);

        Intent intent = getIntent();
        try {
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        long startTimeMills = intent.getLongExtra("start_time", 0);
        long endTimeMills = intent.getLongExtra("end_time", 0);
        if (0 == startTimeMills || 0 == endTimeMills || null == currentProvider || null == currentService) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        startTime = new Date(startTimeMills);
        endTime = new Date(endTimeMills);

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
        txt_service_provider.setText(currentProvider.getCompanyName());
        txt_service_name.setText(currentService.getName());
        txt_date.setText(DAY_FORMAT.format(startTime));
        txt_time.setText(String.format("%s to %s", TIME_FORMAT.format(startTime), TIME_FORMAT.format(endTime)));

    }

    public void onSubmitBookingClick(View v) {

        User homeowner = State.getInstance().getSignedInUser();
        Booking withServiceProvider = new Booking(startTime, endTime, currentProvider, currentProvider.getKey(), homeowner.getKey(), currentService, true);
        Booking withHomeowner = new Booking(startTime, endTime, homeowner, currentProvider.getKey(), homeowner.getKey(), currentService, true);

        DbBooking.createBooking(withServiceProvider, withHomeowner, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {

            }
        });
    }

}
