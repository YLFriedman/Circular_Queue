package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbBooking;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class WeekViewActivity extends SignedInActivity {

    private enum CellState { UNAVAILABLE, AVAILABLE, BOOKED, REQUESTED }
    private class Cell {
        String[] dayNames = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
        String[] timeNames = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09" };
        int day;
        int time;
        CellState state;
        CellState toggleState;
        Cell() { state = CellState.UNAVAILABLE; }
        Cell(int day, int time) { this.day = day; this.time = time; state = CellState.UNAVAILABLE; }
        Cell(int day, int time, CellState state) { this.day = day; this.time = time; this.state = state; }
        String getCellName() { return "cell_" + dayNames[day] + "_" + ((time < 10) ? timeNames[time] : time); }
    }

    private enum Mode { SELECT_TIMESLOT, AVAILABILITY }
    private Mode mode;
    private boolean itemClickEnabled;
    private View[][] cellViews;
    private Cell[][] cells;
    private ServiceProvider currentProvider;
    private Service currentService;
    private SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMM", Locale.CANADA);
    private SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("d", Locale.CANADA);
    private SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
    private TextView txt_month_name;
    private TextView[] txt_day_nums;
    private Date currentDate;
    private Date startOfPeriod;
    private Date endOfPeriod;
    private Calendar cal;
    private int startAtDayOfWeek;
    private int[] requestedStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        try {
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if ((null == currentProvider) == (null == currentService) && null != currentProvider) {
            mode = Mode.SELECT_TIMESLOT;
            setContentView(R.layout.activity_week_view_calendar);
        } else if (State.getInstance().getSignedInUser() instanceof ServiceProvider) {
            mode = Mode.AVAILABILITY;
            setContentView(R.layout.activity_week_view_availability);
        } else {
            Toast.makeText(getApplicationContext(), R.string.service_provider_required, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Create the arrays for the cellViews and cells
        cellViews = new View[7][24];
        cells = new Cell[7][24];
        startAtDayOfWeek = 0;

        // Initialize the cellViews and cells arrays
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                cells[day][time] = new Cell(day, time);
                cellViews[day][time] = findViewById(getResources().getIdentifier(cells[day][time].getCellName(), "id", getPackageName()));
                cellViews[day][time].setLongClickable(true);
                cellViews[day][time].setOnLongClickListener(new CellLongClickListener());
                cellViews[day][time].setTag(cells[day][time]);
            }
        }

        itemClickEnabled = true;
        cal = Calendar.getInstance(Locale.CANADA);

        if (Mode.SELECT_TIMESLOT == mode) {
            ActionBar actionBar = getSupportActionBar();
            if (null != actionBar) { actionBar.setTitle(R.string.select_timeslot); }
            txt_month_name = findViewById(R.id.txt_month_name);
            txt_day_nums = new TextView[7];
            txt_day_nums[0] = findViewById(R.id.txt_sun_num);
            txt_day_nums[1] = findViewById(R.id.txt_mon_num);
            txt_day_nums[2] = findViewById(R.id.txt_tue_num);
            txt_day_nums[3] = findViewById(R.id.txt_wed_num);
            txt_day_nums[4] = findViewById(R.id.txt_thu_num);
            txt_day_nums[5] = findViewById(R.id.txt_fri_num);
            txt_day_nums[6] = findViewById(R.id.txt_sat_num);
            setDate(new Date(intent.getLongExtra("date", System.currentTimeMillis())));
        } else if (Mode.AVAILABILITY == mode) {
            currentProvider = (ServiceProvider) State.getInstance().getSignedInUser();
            loadProviderAvailabilities();
        }

        ScrollView grid_scroll_view = findViewById(R.id.grid_scroll_view);
        grid_scroll_view.scrollTo(0, dpToPx(252));
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.week_view_options, menu);
        if (Mode.AVAILABILITY == mode) {
            menu.setGroupVisible(R.id.grp_week_view_date_controls, false);
        } else {
            menu.setGroupVisible(R.id.grp_avail_controls, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_avail_help: onHelpClick(); return true;
            case R.id.menu_item_avail_clear: onClearClick(); return true;
            case R.id.menu_item_avail_reset: loadProviderAvailabilities(); return true;
            case R.id.menu_item_select_date: onSelectDateClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAvailabilities(@NonNull boolean[][] availabilities) {
        requestedStart = null;
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                if (day < startAtDayOfWeek) {
                    setCell(day, time, CellState.UNAVAILABLE);
                    cells[day][time].toggleState = CellState.UNAVAILABLE;
                } else {
                    setCell(day, time, availabilities[day][time] ? CellState.AVAILABLE : CellState.UNAVAILABLE);
                    cells[day][time].toggleState = availabilities[day][time] ? CellState.AVAILABLE : CellState.UNAVAILABLE;
                }
            }
        }
    }

    private void resetAllCells() {
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                setCell(day, time, cells[day][time].toggleState);
            }
        }
    }

    private void setAllCells(@NonNull CellState cellState) {
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                setCell(day, time, cellState);
            }
        }
    }

    private void setCell(int day, int time, CellState cellState) {
        if (cells[day][time].state == cellState) { return; }
        cells[day][time].state = cellState;
        updateCellView(day, time);
    }

    private void updateCellView(int day, int time) {
        switch (cells[day][time].state) {
            case AVAILABLE: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_available); break;
            case BOOKED: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_booked); break;
            case REQUESTED: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_requested); break;
            case UNAVAILABLE:
            default: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default);
        }
    }

    private void toggleRequested(int day, int time) {
        if (CellState.REQUESTED == cells[day][time].state) {
            setCell(day, time, cells[day][time].toggleState);
        } else {
            setCell(day, time, CellState.REQUESTED);
        }
    }

    private void toggleAvailability(int day, int time) {
        Cell cell = cells[day][time];
        if (CellState.UNAVAILABLE != cell.state) {
            setCell(cell.day, cell.time, CellState.UNAVAILABLE);
        } else {
            setCell(cell.day, cell.time, CellState.AVAILABLE);
        }
    }

    private void setDate(Date date) {
        startAtDayOfWeek = 0;
        currentDate = date;
        txt_month_name.setText("");
        for (TextView txt: txt_day_nums) { txt.setText(""); }
        String today = TODAY_FORMAT.format(new Date());
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != 1) { cal.add(Calendar.DAY_OF_MONTH, 1 - dayOfWeek); }
        startOfPeriod = cal.getTime();
        txt_month_name.setText(MONTH_FORMAT.format(cal.getTime()).substring(0, 3));
        for (TextView txt: txt_day_nums) {
            if (today.equals(TODAY_FORMAT.format(cal.getTime()))) { startAtDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; }
            txt.setText(DAY_FORMAT.format(cal.getTime()));
            txt.setTag(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        endOfPeriod = cal.getTime();
        loadProviderAvailabilities();
    }

    private void loadProviderAvailabilities() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        if (null == currentProvider.getAvailabilities()) {
            setAvailabilities(Availability.toArrays(new LinkedList<Availability>()));
        } else {
            setAvailabilities(Availability.toArrays(currentProvider.getAvailabilities()));
        }
        loadBookings();
        itemClickEnabled = true;
    }

    private void loadBookings() {
        if (null == startOfPeriod || null == endOfPeriod || Mode.SELECT_TIMESLOT != mode) { return; }
        DbBooking.getBookingsInTimeRange(currentProvider, startOfPeriod, endOfPeriod, new AsyncValueEventListener<Booking>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Booking> data) {
                int day, start, end;
                for (Booking b: data) {
                    if (b.getStatus() != Booking.Status.APPROVED) { continue; }
                    cal.setTime(b.getStartTime());
                    day = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    start = cal.get(Calendar.HOUR_OF_DAY);
                    cal.setTime(b.getEndTime());
                    end = cal.get(Calendar.HOUR_OF_DAY);
                    if (0 == end) { end = 24; }
                    while (start < end) {
                        if (CellState.AVAILABLE == cells[day][start].state) {
                            cells[day][start].toggleState = CellState.BOOKED;
                            setCell(day, start, CellState.BOOKED);
                        }
                        start++;
                    }
                }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {}
        });
    }

    private void setRequested(int day, int time) {
        Cell cell = cells[day][time];
        boolean continuous;
        int t;
        if (CellState.AVAILABLE == cell.state || CellState.BOOKED == cell.state) {
            // If there is currently no requested cell, set the requested cell
            if (null == requestedStart) {
                //setCell(cell.day, cell.time, CellState.REQUESTED);
                toggleRequested(day, time);
                requestedStart = new int[]{ day, time };
            } else { // If there is already a requested cell
                // Assume it is not continuous unless the clicked cell is the same day and there are no gaps
                continuous = false;
                if (requestedStart[0] == day) {
                    t = time;
                    continuous = true;
                    while (t != requestedStart[1]) {
                        if (CellState.UNAVAILABLE == cells[day][t].state) { continuous = false; break; }
                        if (requestedStart[1] > t) { t++; } else {t--; }
                    }
                }
                // If there are no gaps, set the cells from the clicked cell to the closest requested cell to requested
                if (continuous) {
                    t = time;
                    while (t != requestedStart[1]) {
                        if (CellState.REQUESTED == cells[day][t].state) { break; }
                        toggleRequested(day, t);
                        if (requestedStart[1] > t) { t++; } else {t--; }
                    }
                    // If the clicked cell was earlier than the requested cell, change the requested cell to be the start time
                    if (time < requestedStart[1]) { requestedStart[1] = time; }
                } else {
                    // If the selection is not continuous, reset all the cells then set the clicked cell and move the requested cell
                    resetAllCells();
                    toggleRequested(day, time);
                    requestedStart = new int[]{ day, time };
                }
            }
        } else if (CellState.REQUESTED == cell.state) {
            if (0 != time && 23 != time && CellState.REQUESTED == cells[day][time+1].state && CellState.REQUESTED == cells[day][time-1].state) {
                // If we are clicking on a middle requested range, remove all requested cells
                resetAllCells();
                requestedStart = null;
                return;
            } else if (0 == time) {
                if (CellState.REQUESTED == cells[day][time+1].state) { requestedStart[1]++; }
                else { requestedStart = null; }
            } else if (23 == time) {
                if (CellState.REQUESTED != cells[day][time-1].state) { requestedStart = null; }
            } else if (CellState.REQUESTED == cells[day][time+1].state) {
                requestedStart[1]++;
            }
            toggleRequested(day, time);
        }
    }

    public void onCellClick(View view) {
        Cell cell;
        if (view.getTag() instanceof Cell) { cell = (Cell) view.getTag(); }
        else { return; }
        if (Mode.AVAILABILITY == mode) {
            toggleAvailability(cell.day, cell.time);
        } else if (Mode.SELECT_TIMESLOT == mode) {
            setRequested(cell.day, cell.time);
        }
    }

    private class CellLongClickListener implements View.OnLongClickListener {
        public boolean onLongClick(View view) {
            if (Mode.AVAILABILITY == mode) {
                Cell cell;
                if (view.getTag() instanceof Cell) { cell = (Cell) view.getTag(); }
                else { return false; }

                int time = cell.time;
                int day = cell.day;

                if (CellState.UNAVAILABLE == cell.state) {
                    do { setCell(day, time--, CellState.AVAILABLE); }
                    while (time >= 0 && CellState.UNAVAILABLE == cells[day][time].state);
                } else if (CellState.AVAILABLE == cell.state) {
                    do { setCell(day, time--, CellState.UNAVAILABLE); }
                    while (time >= 0 && CellState.AVAILABLE == cells[day][time].state);
                    time = cell.time + 1;
                    while (time <= 23 && CellState.AVAILABLE == cells[day][time].state) {
                        setCell(day, time++, CellState.UNAVAILABLE);
                    }
                }
            }

            return true;
        }
    }

    public void onNextClick(View view) {
        if (!itemClickEnabled) { return; }
        if (null == requestedStart) {
            Toast.makeText(getApplicationContext(), getString(R.string.timeslot_required), Toast.LENGTH_LONG).show();
            return;
        }
        cal.setTime((Date) txt_day_nums[requestedStart[0]].getTag());
        cal.set(Calendar.HOUR_OF_DAY, requestedStart[1]);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(getApplicationContext(), getString(R.string.start_time_in_the_past), Toast.LENGTH_LONG).show();
            return;
        }
        long startTime = cal.getTimeInMillis();
        int day = requestedStart[0];
        int time = requestedStart[1] + 1;
        while (time < 24 && CellState.REQUESTED == cells[day][time].state) { time++; }
        if (24 == time) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, time);
        }
        long endTime = cal.getTimeInMillis();

        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), BookingCreateActivity.class);
        intent.putExtra("provider", currentProvider);
        intent.putExtra("service", currentService);
        intent.putExtra("start_time", startTime);
        intent.putExtra("end_time", endTime);
        startActivity(intent);
    }

    public void onSaveClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        currentProvider.setAvailabilities(Availability.toList(getAvailabilities()));
        DbUser.updateUser(currentProvider, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), getString(R.string.availability_saved_successfully), Toast.LENGTH_LONG).show();
                itemClickEnabled = true;
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), getString(R.string.availability_save_failed), Toast.LENGTH_LONG).show();
                itemClickEnabled = true;
            }
        });
    }

    private void onHelpClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        View helpView;
        if (Mode.AVAILABILITY == mode) {
            helpView = getLayoutInflater().inflate(R.layout.help_availabilities, null);
        } else if (Mode.SELECT_TIMESLOT == mode) {
            helpView = getLayoutInflater().inflate(R.layout.help_select_timeslot, null);
        } else {
            itemClickEnabled = true;
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(helpView)
                .setPositiveButton(R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                })
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
    }

    private void onClearClick() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        setAllCells(CellState.UNAVAILABLE);
        itemClickEnabled = true;
    }

    private void onSelectDateClick() {
        cal.setTime(currentDate);
        DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                setDate(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime());
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.setOkColor(getResources().getColor(R.color.dialog_red));
        dpd.setCancelColor(getResources().getColor(R.color.text_secondary_dark));
        cal.setTime(new Date());
        dpd.setMinDate(cal);
        dpd.show(getSupportFragmentManager(), "date_select");
    }

    private boolean[][] getAvailabilities() {
        boolean[][] availabilities = new boolean[7][24];
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                availabilities[day][time] = (CellState.AVAILABLE == cells[day][time].state);
            }
        }
        return availabilities;
    }

    public void onZoomInClick(View view) {
        setHeights(cellViews[0][0].getLayoutParams().height + dpToPx(8));
    }

    public void onZoomOutClick(View view) {
        setHeights(cellViews[0][0].getLayoutParams().height - dpToPx(8));
    }

    private void setHeights(int height) {
        if (height < dpToPx(20) || height > dpToPx(80)) { return; }
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                cellViews[day][time].getLayoutParams().height = height;
                cellViews[day][time].requestLayout();
            }
        }
        View txt_time;
        LinearLayout col_times = findViewById(R.id.col_times);
        for (int i = 0; i < col_times.getChildCount() - 1; i++) {
            txt_time = col_times.getChildAt(i);
            txt_time.getLayoutParams().height = height;
            txt_time.requestLayout();
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}
