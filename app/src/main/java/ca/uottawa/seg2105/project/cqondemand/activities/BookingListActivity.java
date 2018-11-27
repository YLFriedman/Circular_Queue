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

public class BookingListActivity extends SignedInActivity {

    protected enum Screen {
        BOOKED, PENDING, CANCELLED, PAST;
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
    protected Screen screen;
    protected boolean itemClickEnabled = true;
    protected RecyclerView recycler_list;
    protected DbListenerHandle<?> dbListenerHandle;
    protected Button[] buttons;
    protected String[] tabTitles;
    protected ArrayList<ArrayList<Booking>> dataLists;

    @SuppressWarnings("unchecked")
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
        tabTitles[1] = getString(R.string.pending_template);
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

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the categories list
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

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

    protected void setScreen(Screen screen) {
        for (int i = 0; i < Screen.values().length; i++) {
            buttons[i].setBackgroundColor(getResources().getColor(R.color.transparent));
            buttons[i].setTextColor(getResources().getColor(R.color.White));
            buttons[i].setText(String.format(tabTitles[i], (null == dataLists.get(i) ? 0 : dataLists.get(i).size())));
        }
        //buttons[screen.getIdx()].setPaintFlags(buttons[bIdx].getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        buttons[screen.getIdx()].setBackgroundColor(getResources().getColor(R.color.background_default));
        buttons[screen.getIdx()].setTextColor(getResources().getColor(R.color.text_primary_dark));
        this.screen = screen;
        setListData();
    }

    protected void setListData() {
        if (null == dataLists.get(screen.getIdx())) { return; }
        recycler_list.setAdapter(new BookingListAdapter(getApplicationContext(), dataLists.get(screen.getIdx()), new View.OnClickListener() {
            public void onClick(final View view) {
                if (!itemClickEnabled) { return; }
                itemClickEnabled = false;
                Intent intent = new Intent(getApplicationContext(), BookingViewActivity.class);
                intent.putExtra("booking", (Serializable) view.getTag());
                startActivityForResult(intent, 0);
            }
        }));
    }

}
