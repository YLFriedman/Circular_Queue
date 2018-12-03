package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.BookingListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbBooking;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The class <b>BookingListActivity</b> is a UI class that allows a user to see and select from list of bookings.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class BookingListActivity extends SignedInActivity {

    /**
     * An enum defining the view various view states within this activity
     */
    private enum Screen {

        BOOKED, PENDING, CANCELLED, PAST;

        /**
         * Getter for the screen's index
         */
        public int getIdx() {
            switch (this) {
                case BOOKED: return 0;
                case PENDING: return 1;
                case CANCELLED: return 2;
                case PAST: return 3;
                default: throw new IllegalArgumentException("Invalid screen");
            }
        }

    }

    /**
     * The currently active screen
     */
    private Screen screen;

    /**
     * Whether or not relevant onClick actions are enabled for within this activity
     */
    private boolean onClickEnabled = true;

    /**
     * The view that displays the list of bookings
     */
    private RecyclerView recycler_list;

    /**
     * Stores the handle to the database callback so that it can be cleaned up when the activity ends
     */
    private DbListenerHandle<?> dbListenerHandle;

    /**
     * A set of button views that are used to select the active screen
     */
    private Button[] buttons;

    /**
     * A set of labels used for the buttons text
     */
    private String[] tabTitles;

    /**
     * A set of ArrayLists which contain the data for each screen
     */
    private ArrayList<ArrayList<Booking>> dataLists;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) { return; }

        buttons = new Button[4];
        buttons[0] = findViewById(R.id.btn_tab_booked);
        buttons[1] = findViewById(R.id.btn_tab_pending);
        buttons[2] = findViewById(R.id.btn_tab_cancelled);
        buttons[3] = findViewById(R.id.btn_tab_past);

        tabTitles = new String[4];
        tabTitles[0] = getString(R.string.booked_template);
        tabTitles[1] = getString(R.string.requested_template);
        tabTitles[2] = getString(R.string.cancelled_template);
        tabTitles[3] = getString(R.string.past);

        dataLists = new ArrayList<ArrayList<Booking>>(4);
        for (int i = 0; i < 4; i++) { dataLists.add(null); }

        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        setScreen(Screen.BOOKED);

        dbListenerHandle = DbBooking.getBookingsLive(signedInUser, new AsyncValueEventListener<Booking>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Booking> data) {
                for (int i = 0; i < dataLists.size(); i++) {
                    dataLists.set(i, new ArrayList<Booking>());
                }
                Long now = System.currentTimeMillis();
                for (Booking booking: data) {
                    if (booking.getEndTime().getTime() < now) {
                        dataLists.get(Screen.PAST.getIdx()).add(booking);
                    } else {
                        switch (booking.getStatus()) {
                            case CANCELLED: dataLists.get(Screen.CANCELLED.getIdx()).add(booking); break;
                            case REQUESTED: dataLists.get(Screen.PENDING.getIdx()).add(booking); break;
                            case APPROVED:
                            default: dataLists.get(Screen.BOOKED.getIdx()).add(booking); break;
                        }
                    }
                }
                setScreen(screen);
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), getString(R.string.category_list_db_error), Toast.LENGTH_LONG).show();
            }
        });

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
     * Removes the listener for data from the database.
     * This is run during the destroy phase of the activity lifecycle.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    /**
     * The on-click handler for the activity tabs
     * @param view the view object that was clicked
     */
    public void onTabPressed(View view) {
        Screen newScreen;
        switch (view.getId()) {
            case R.id.btn_tab_pending: newScreen = Screen.PENDING; break;
            case R.id.btn_tab_cancelled: newScreen = Screen.CANCELLED; break;
            case R.id.btn_tab_past: newScreen = Screen.PAST; break;
            case R.id.btn_tab_booked:
            default: newScreen = Screen.BOOKED; break;
        }
        if (screen != newScreen) { setScreen(newScreen); }
    }

    /**
     * Sets up the views contained within the activity for the specified screen
     * @param screen the type of screen to be setup
     */
    private void setScreen(Screen screen) {
        for (int i = 0; i < Screen.values().length; i++) {
            buttons[i].setBackgroundColor(getResources().getColor(R.color.transparent));
            buttons[i].setTextColor(getResources().getColor(R.color.White));
            buttons[i].setText(String.format(tabTitles[i], (null == dataLists.get(i) ? 0 : dataLists.get(i).size())));
        }
        buttons[screen.getIdx()].setBackgroundColor(getResources().getColor(R.color.background_default));
        buttons[screen.getIdx()].setTextColor(getResources().getColor(R.color.text_primary_dark));
        this.screen = screen;

        if (null == dataLists.get(screen.getIdx())) { return; }
        recycler_list.setAdapter(new BookingListAdapter(getApplicationContext(), dataLists.get(screen.getIdx()), new View.OnClickListener() {
            public void onClick(final View view) {
                if (!onClickEnabled) { return; }
                onClickEnabled = false;
                Intent intent = new Intent(getApplicationContext(), BookingViewActivity.class);
                intent.putExtra("booking", (Serializable) view.getTag());
                startActivityForResult(intent, 0);
            }
        }));
    }

}
