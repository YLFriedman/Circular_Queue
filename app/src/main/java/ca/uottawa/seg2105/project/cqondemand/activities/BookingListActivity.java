package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class BookingListActivity extends SignedInActivity {

    protected enum Screen { BOOKED, PENDING, CANCELLED, PAST }
    protected Screen screen;
    protected boolean itemClickEnabled = true;
    protected RecyclerView recycler_list;
    protected DbListenerHandle<?> dbListenerHandle;
    protected ArrayList<Booking> dataBooked;
    protected ArrayList<Booking> dataPending;
    protected ArrayList<Booking> dataCancelled;
    protected ArrayList<Booking> dataPast;
    protected Button btn_tab_booked;
    protected Button btn_tab_pending;
    protected Button btn_tab_cancelled;
    protected Button btn_tab_past;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) { return; }

        btn_tab_booked = findViewById(R.id.btn_tab_booked);
        btn_tab_pending = findViewById(R.id.btn_tab_pending);
        btn_tab_cancelled = findViewById(R.id.btn_tab_cancelled);
        btn_tab_past = findViewById(R.id.btn_tab_past);

        btn_tab_booked.setPaintFlags(btn_tab_booked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        screen = Screen.BOOKED;
        recycler_list = findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        dbListenerHandle = DbBooking.getBookingsLive(signedInUser, new AsyncValueEventListener<Booking>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Booking> data) {
                dataBooked = new ArrayList<Booking>();
                dataPending = new ArrayList<Booking>();
                dataCancelled = new ArrayList<Booking>();
                dataPast = new ArrayList<Booking>();
                Long now = System.currentTimeMillis();
                for (Booking booking: data) {
                    if (booking.getEndTime().getTime() < now) {
                        dataPast.add(booking);
                    } else {
                        switch (booking.getStatus()) {
                            case CANCELLED: dataCancelled.add(booking); break;
                            case REQUESTED: dataPending.add(booking); break;
                            case APPROVED:
                            default: dataBooked.add(booking); break;
                        }
                    }
                }
                setListData();
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
        switch (view.getId()) {
            case R.id.btn_tab_pending: setScreen(Screen.PENDING); break;
            case R.id.btn_tab_cancelled: setScreen(Screen.CANCELLED); break;
            case R.id.btn_tab_past: setScreen(Screen.PAST); break;
            case R.id.btn_tab_booked:
            default: setScreen(Screen.BOOKED); break;
        }
    }

    protected void setScreen(Screen screen) {
        if (this.screen != screen) {
            this.screen = screen;
            btn_tab_booked.setPaintFlags(btn_tab_booked.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            btn_tab_pending.setPaintFlags(btn_tab_pending.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            btn_tab_cancelled.setPaintFlags(btn_tab_cancelled.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            btn_tab_past.setPaintFlags(btn_tab_past.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            switch (screen) {
                case PENDING: btn_tab_pending.setPaintFlags(btn_tab_pending.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); break;
                case CANCELLED: btn_tab_cancelled.setPaintFlags(btn_tab_cancelled.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); break;
                case PAST: btn_tab_past.setPaintFlags(btn_tab_past.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); break;
                case BOOKED:
                default: btn_tab_booked.setPaintFlags(btn_tab_booked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); break;
            }
            setListData();
        }
    }

    protected void setListData() {
        ArrayList<Booking> data;
        switch (screen) {
            case PENDING: data = dataPending; break;
            case CANCELLED: data = dataCancelled; break;
            case PAST: data = dataPast; break;
            case BOOKED:
            default: data = dataBooked;
        }
        recycler_list.setAdapter(new BookingListAdapter(getApplicationContext(), data, new View.OnClickListener() {
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
