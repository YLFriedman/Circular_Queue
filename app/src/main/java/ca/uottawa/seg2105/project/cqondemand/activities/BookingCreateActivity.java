package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    protected SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.CANADA);
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

        if (startTime.compareTo(new Date()) <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.start_time_in_the_past), Toast.LENGTH_LONG).show();
            return;
        }

        User homeowner = State.getInstance().getSignedInUser();

        final Booking newBooking = new Booking(startTime, endTime, homeowner, currentProvider, currentService);

        final Button btn_submit_booking = findViewById(R.id.btn_submit_booking);
        btn_submit_booking.setEnabled(false);

        DbBooking.createBooking(newBooking, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.item_create_success_template), currentService.getName(), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                intent = new Intent(getApplicationContext(), BookingListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                intent = new Intent(getApplicationContext(), BookingViewActivity.class);
                intent.putExtra("booking", newBooking);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.item_create_db_error_template), currentService.getName(), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.item_create_error_template), currentService.getName(), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                }
                btn_submit_booking.setEnabled(true);
            }
        });
    }

}
