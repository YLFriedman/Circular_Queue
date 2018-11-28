package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbBooking;
import ca.uottawa.seg2105.project.cqondemand.database.DbReview;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
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
    private LinearLayout grp_cancelled;
    private LinearLayout grp_approved;
    private TextView txt_homeowner;
    private TextView txt_service_provider;
    private TextView txt_service_name;
    private TextView txt_date;
    private TextView txt_time;
    private TextView txt_service_rate;
    private TextView txt_created_on;
    private TextView txt_status;
    private TextView txt_approved_on;
    private TextView txt_cancelled_on;
    private TextView txt_cancelled_reason;
    private TextView txt_cancelled_by;
    private Button btn_submit_review;
    private Button btn_approve_booking;


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

        btn_submit_review = findViewById(R.id.btn_submit_review);
        btn_approve_booking = findViewById(R.id.btn_approve_booking);
        grp_homeowner = findViewById(R.id.grp_homeowner);
        grp_provider = findViewById(R.id.grp_provider);
        grp_cancelled = findViewById(R.id.grp_cancelled);
        grp_approved = findViewById(R.id.grp_approved);
        txt_homeowner = findViewById(R.id.txt_homeowner);
        txt_service_provider = findViewById(R.id.txt_service_provider);
        txt_service_name = findViewById(R.id.txt_service_name);
        txt_date = findViewById(R.id.txt_date);
        txt_time = findViewById(R.id.txt_time);
        txt_service_rate = findViewById(R.id.txt_service_rate);
        txt_created_on = findViewById(R.id.txt_created_on);
        txt_status = findViewById(R.id.txt_status);
        txt_approved_on = findViewById(R.id.txt_approved_on);
        txt_cancelled_on = findViewById(R.id.txt_cancelled_on);
        txt_cancelled_reason = findViewById(R.id.txt_cancelled_reason);
        txt_cancelled_by = findViewById(R.id.txt_cancelled_by);

        configureView();

    }

    @Override
    public void onResume() {
        super.onResume();
        btn_submit_review.setVisibility(View.GONE);
        itemClickEnabled = true;
        if (Mode.HOMEOWNER == mode && Booking.Status.COMPLETED == currentBooking.getStatus()) {
            DbReview.getReview(currentBooking.getServiceProvider().getKey(), currentBooking.getKey(), new AsyncSingleValueEventListener<Review>() {
                @Override
                public void onSuccess(@NonNull Review item) {
                    // If a review already exists for this booking, do nothing
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                    // If there is no review, show the button to submit a review
                    if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                        btn_submit_review.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Booking.Status.REQUESTED == currentBooking.getStatus() || Booking.Status.APPROVED == currentBooking.getStatus()) {
            getMenuInflater().inflate(R.menu.booking_options, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_booking_cancel: cancelBooking(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureView() {

        btn_submit_review.setVisibility(View.GONE);
        btn_approve_booking.setVisibility(View.GONE);
        grp_cancelled.setVisibility(View.GONE);
        grp_approved.setVisibility(View.GONE);

        txt_service_name.setText(currentBooking.getServiceName());
        txt_date.setText(DAY_FORMAT.format(currentBooking.getStartTime()));
        txt_time.setText(String.format("%s to %s", TIME_FORMAT.format(currentBooking.getStartTime()), TIME_FORMAT.format(currentBooking.getEndTime())));
        if (0 == currentBooking.getServiceRate()) { txt_service_rate.setText(getString(R.string.zero_value_service)); }
        else { txt_service_rate.setText(String.format(Locale.CANADA, getString(R.string.service_rate_template), currentBooking.getServiceRate())); }
        txt_created_on.setText(DATE_FORMAT.format(currentBooking.getDateCreated()));
        txt_status.setText(currentBooking.getStatus().toString());

        if (Booking.Status.CANCELLED == currentBooking.getStatus()) {
            txt_cancelled_on.setText(DATE_FORMAT.format(currentBooking.getDateCancelledOrApproved()));
            txt_cancelled_reason.setText(currentBooking.getCancelledReason());
            txt_cancelled_by.setText(currentBooking.getCancelledBy());
            grp_cancelled.setVisibility(View.VISIBLE);
        } else if (Booking.Status.APPROVED == currentBooking.getStatus()) {
            txt_approved_on.setText(DATE_FORMAT.format(currentBooking.getDateCancelledOrApproved()));
            grp_approved.setVisibility(View.VISIBLE);
        }

        if (Mode.HOMEOWNER == mode) {
            txt_service_provider.setText(currentBooking.getServiceProvider().getCompanyName());
            grp_homeowner.setVisibility(View.GONE);

        } else if (Mode.SERVICE_PROVIDER == mode) {
            txt_homeowner.setText(currentBooking.getHomeowner().getFullName());
            grp_provider.setVisibility(View.GONE);
            if (Booking.Status.REQUESTED == currentBooking.getStatus()) { btn_approve_booking.setVisibility(View.VISIBLE); }
        }

    }

    public void onSeeProviderProfileClick(View v) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceProviderProfileActivity.class);
        intent.putExtra("provider", currentBooking.getServiceProvider());
        startActivity(intent);
    }

    public void onRateServiceClick(View v) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewCreateActivity.class);
        intent.putExtra("booking", currentBooking);
        startActivity(intent);
    }

    private void cancelBooking() {
        if (Booking.Status.REQUESTED != currentBooking.getStatus() && Booking.Status.APPROVED != currentBooking.getStatus()) { configureView(); return; }
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        View cancelView = getLayoutInflater().inflate(R.layout.dialog_cancel_booking, null);
        EditText cancelReasonField = cancelView.findViewById(R.id.field_cancellation_reason);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_booking)
                .setMessage(String.format(getString(R.string.cancel_confirm_dialog_template), getString(R.string.booking).toLowerCase()))
                .setView(cancelView)
                .setIcon(R.drawable.ic_report_red_30)
                .setPositiveButton(R.string.cancel_booking, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DbBooking.cancelBooking(currentBooking, cancelReasonField.getText().toString(), new AsyncActionEventListener() {
                            @Override
                            public void onSuccess() {
                                itemClickEnabled = true;
                                configureView();
                                Toast.makeText(getApplicationContext(), getString(R.string.booking_cancelled_successfully), Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                switch (reason) {
                                    case DATABASE_ERROR:
                                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.update_db_error_template), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                                        break;
                                    default: // Some other kind of error
                                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.update_generic_error_template), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                                }
                                itemClickEnabled = true;
                            }
                        });
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                })
                .setNegativeButton(R.string.close, null).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
    }

    public void onApproveBookingClick(View v) {
        if (Booking.Status.REQUESTED != currentBooking.getStatus()) { configureView(); return; }
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        btn_approve_booking.setEnabled(false);
        DbBooking.approveBooking(currentBooking, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                itemClickEnabled = true;
                configureView();
                Toast.makeText(getApplicationContext(), getString(R.string.booking_approved_successfully), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.update_db_error_template), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), String.format(getString(R.string.update_generic_error_template), getString(R.string.booking).toLowerCase()), Toast.LENGTH_LONG).show();
                }
                btn_approve_booking.setEnabled(true);
                itemClickEnabled = true;
            }
        });
    }

}
