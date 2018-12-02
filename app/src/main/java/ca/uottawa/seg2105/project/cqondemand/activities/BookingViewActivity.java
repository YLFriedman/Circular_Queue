package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

/**
 * The class <b>BookingViewActivity</b> is a UI class that allows a user to see the details of an individual booking.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class BookingViewActivity extends SignedInActivity {

    /**
     * An enum defining the view various modes of this activity
     */
    private enum Mode { HOMEOWNER, SERVICE_PROVIDER }

    /**
     * The currently active mode
     */
    private Mode mode;

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    private boolean onClickEnabled;

    /**
     * The booking that is being displayed
     */
    private Booking currentBooking;

    /**
     * The review associated with the current booking
     */
    private Review currentReview;

    /**
     * The format to be used for the date (month day, year  hour:minute am/pm)
     */
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy  h:mm a", Locale.CANADA);

    /**
     *The format to be used for the day (month day, year)
     */
    private SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("MMMM d, yyyy", Locale.CANADA);

    /**
     * The format to be used for the time (hour:minute am/pm)
     */
    private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.CANADA);

    /**
     * A view group for service provider related views
     */
    private LinearLayout grp_provider;

    /**
     * A view group for homeowner related views
     */
    private LinearLayout grp_homeowner;

    /**
     * A view group for views related to a booking with a cancelled status
     */
    private LinearLayout grp_cancelled;

    /**
     * A view group for views related to a booking with an approved status
     */
    private LinearLayout grp_approved;

    /**
     * A view that displays the homeowner
     */
    private TextView txt_homeowner;

    /**
     * A view that displays the service provider
     */
    private TextView txt_service_provider;

    /**
     * A view that displays the service name
     */
    private TextView txt_service_name;

    /**
     * A view that displays the date of the booking
     */
    private TextView txt_date;

    /**
     * A view that displays the time of the booking
     */
    private TextView txt_time;

    /**
     * A view that displays the rate of the service
     */
    private TextView txt_service_rate;

    /**
     * A view that displays the date that the booking was created
     */
    private TextView txt_created_on;

    /**
     * A view that displays the status of the booking
     */
    private TextView txt_status;

    /**
     * A view that displays the status icon of the booking
     */
    private ImageView img_status_icon;

    /**
     * A view that displays the date/time that the booking was approved
     */
    private TextView txt_approved_on;

    /**
     * A view that displays the date/time that the booking was cancelled
     */
    private TextView txt_cancelled_on;

    /**
     * A view that displays the reason that the booking was cancelled
     */
    private TextView txt_cancelled_reason;

    /**
     * A view that displays who cancelled the booking (homeowner name or service provider company name)
     */
    private TextView txt_cancelled_by;

    /**
     * A view that loads the review view activity when clicked
     */
    private TextView txt_see_review;

    /**
     * A button view that loads the review creation activity
     */
    private Button btn_submit_review;

    /**
     * A button view that changes the booking status to approved
     */
    private Button btn_approve_booking;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
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
        img_status_icon = findViewById(R.id.img_status_icon);
        txt_approved_on = findViewById(R.id.txt_approved_on);
        txt_cancelled_on = findViewById(R.id.txt_cancelled_on);
        txt_cancelled_reason = findViewById(R.id.txt_cancelled_reason);
        txt_cancelled_by = findViewById(R.id.txt_cancelled_by);
        txt_see_review = findViewById(R.id.txt_see_review);
        txt_see_review.setVisibility(View.GONE);

        configureViews();

    }

    /**
     * Enables the relevant onClick actions within this activity and checks if the submit review button can be displayed.
     * This is run during the resume phase of the activity lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        btn_submit_review.setVisibility(View.GONE);
        onClickEnabled = true;
        if (Booking.Status.COMPLETED == currentBooking.getStatus()) {
            DbReview.getReview(currentBooking.getServiceProviderKey(), currentBooking.getKey(), new AsyncSingleValueEventListener<Review>() {
                @Override
                public void onSuccess(@NonNull Review item) {
                    // If a review already exists for this booking, show the see review button
                    txt_see_review.setVisibility(View.VISIBLE);
                    currentReview = item;
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                    // If there is no review, and we are in homeowner mode, show the button to submit a review
                    if (AsyncEventFailureReason.DOES_NOT_EXIST == reason && Mode.HOMEOWNER == mode) {
                        btn_submit_review.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    /**
     * Sets the menu to be used in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Booking.Status.REQUESTED == currentBooking.getStatus() || Booking.Status.APPROVED == currentBooking.getStatus()) {
            getMenuInflater().inflate(R.menu.booking_options, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The onClick handler for the action bar menu items
     * @param item the menu item that was clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_booking_cancel: cancelBooking(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the views contained within the activity based on the current mode and the booking status
     */
    private void configureViews() {

        btn_submit_review.setVisibility(View.GONE);
        btn_approve_booking.setVisibility(View.GONE);
        grp_cancelled.setVisibility(View.GONE);
        grp_approved.setVisibility(View.GONE);

        txt_service_name.setText(currentBooking.getServiceName());
        txt_date.setText(DAY_FORMAT.format(currentBooking.getStartTime()));

        Calendar cal = Calendar.getInstance(Locale.CANADA);
        cal.setTime(currentBooking.getEndTime());
        String endTimeStr = cal.get(Calendar.HOUR_OF_DAY) == 0 ? "Midnight" : TIME_FORMAT.format(currentBooking.getEndTime());
        txt_time.setText(String.format("%s to %s", TIME_FORMAT.format(currentBooking.getStartTime()), endTimeStr));
        if (0 == currentBooking.getServiceRate()) { txt_service_rate.setText(getString(R.string.zero_value_service)); }
        else { txt_service_rate.setText(String.format(Locale.CANADA, getString(R.string.service_rate_template), currentBooking.getServiceRate())); }
        txt_created_on.setText(DATE_FORMAT.format(currentBooking.getDateCreated()));
        txt_status.setText(currentBooking.getStatus().toString());
        switch (currentBooking.getStatus()) {
            case CANCELLED: img_status_icon.setImageResource(R.drawable.ic_delete_med_24); break;
            case APPROVED: img_status_icon.setImageResource(R.drawable.ic_event_available_green_24); break;
            case REQUESTED: img_status_icon.setImageResource(R.drawable.ic_event_orange_24); break;
            case EXPIRED: img_status_icon.setImageResource(R.drawable.ic_event_busy_red_24); break;
            case COMPLETED: img_status_icon.setImageResource(R.drawable.ic_check_circle_green_24); break;
            default: img_status_icon.setVisibility(View.GONE);
        }

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

    /**
     * The on-click handler for the see provider profile link
     * @param view the view object that was clicked
     */
    public void onSeeProviderProfileClick(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ServiceProviderProfileActivity.class);
        intent.putExtra("provider", currentBooking.getServiceProvider());
        startActivity(intent);
    }

    /**
     * The on-click handler for the create rating button
     * @param view the view object that was clicked
     */
    public void onRateServiceClick(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewCreateActivity.class);
        intent.putExtra("booking", currentBooking);
        startActivity(intent);
    }

    /**
     * Prompts the user with the cancel booking confirmation screen and triggers the cancellation process if confirmed.
     */
    private void cancelBooking() {
        if (Booking.Status.REQUESTED != currentBooking.getStatus() && Booking.Status.APPROVED != currentBooking.getStatus()) { configureViews(); return; }
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        View cancelView = getLayoutInflater().inflate(R.layout.dialog_cancel_booking, null);
        EditText cancelReasonField = cancelView.findViewById(R.id.field_cancellation_reason);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_booking)
                .setMessage(String.format(getString(R.string.cancel_confirm_dialog_template), getString(R.string.booking).toLowerCase()))
                .setView(cancelView)
                .setIcon(R.drawable.ic_report_red_30)
                .setPositiveButton(R.string.cancel_booking, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DbBooking.cancelBooking(currentBooking, cancelReasonField.getText().toString().trim(), new AsyncActionEventListener() {
                            @Override
                            public void onSuccess() {
                                onClickEnabled = true;
                                configureViews();
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
                                onClickEnabled = true;
                            }
                        });
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) { onClickEnabled = true; }
                })
                .setNegativeButton(R.string.close, null).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
    }

    /**
     * The on-click handler for the approve booking button.
     * @param view the view object that was clicked
     */
    public void onApproveBookingClick(View view) {
        if (Booking.Status.REQUESTED != currentBooking.getStatus()) { configureViews(); return; }
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        btn_approve_booking.setEnabled(false);
        DbBooking.approveBooking(currentBooking, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                onClickEnabled = true;
                configureViews();
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
                onClickEnabled = true;
            }
        });
    }

    /**
     * The on-click handler for the see review link
     * @param view the view object that was clicked
     */
    public void onSeeReviewClick(View view) {
        if (!onClickEnabled) { return; }
        onClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), ReviewViewActivity.class);
        intent.putExtra("review", currentReview);
        if (State.getInstance().getSignedInUser() instanceof ServiceProvider) {
            intent.putExtra("provider", State.getInstance().getSignedInUser());
        } else {
            intent.putExtra("provider", currentBooking.getServiceProvider());
        }
        startActivity(intent);
    }

}
