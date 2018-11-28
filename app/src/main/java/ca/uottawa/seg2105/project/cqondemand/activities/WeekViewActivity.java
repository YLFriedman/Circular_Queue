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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class WeekViewActivity extends SignedInActivity implements DatePickerDialog.OnDateSetListener {

    protected enum CellState { UNAVAILABLE, AVAILABLE, BOOKED, REQUESTED }
    protected class Cell {
        public String[] dayNames = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
        public String[] timeNames = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09" };
        public int day;
        public int time;
        public CellState cellState;
        public Cell() { cellState = CellState.UNAVAILABLE; }
        public Cell(int day, int time) { this.day = day; this.time = time; cellState = CellState.UNAVAILABLE; }
        public Cell(int day, int time, CellState cellState) { this.day = day; this.time = time; this.cellState = cellState; }
        public String getCellName() { return "cell_" + dayNames[day] + "_" + ((time < 10) ? timeNames[time] : time); }
    }

    protected enum Mode { SELECT_TIMESLOT, AVAILABILITY }
    protected Mode mode;
    protected boolean itemClickEnabled;
    protected View[][] cellViews;
    protected Cell[][] cells;
    protected ServiceProvider currentProvider;
    protected Service currentService;
    protected SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMM", Locale.CANADA);
    protected SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("d", Locale.CANADA);
    protected SimpleDateFormat TODAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
    protected TextView txt_month_name;
    protected TextView[] txt_day_nums;
    protected Date currentDate;
    protected Calendar cal;
    protected int startAtDayOfWeek;
    protected int[] requestedCell;


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

    protected void setDate(Date date) {
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
        txt_month_name.setText(MONTH_FORMAT.format(cal.getTime()).substring(0, 3));
        for (TextView txt: txt_day_nums) {
            if (today.equals(TODAY_FORMAT.format(cal.getTime()))) { startAtDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; }
            txt.setText(DAY_FORMAT.format(cal.getTime()));
            txt.setTag(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        loadProviderAvailabilities();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime());
    }

    public void onSelectDateClick() {
        cal.setTime(currentDate);
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.setOkColor(getResources().getColor(R.color.dialog_red));
        dpd.setCancelColor(getResources().getColor(R.color.text_secondary_dark));
        cal.setTime(new Date());
        dpd.setMinDate(cal);
        dpd.show(getSupportFragmentManager(), "date_select");
    }

    private void setAvailabilities(@NonNull boolean[][] availabilities) {
        requestedCell = null;
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                if (day < startAtDayOfWeek) {
                    setCell(day, time, CellState.UNAVAILABLE);
                } else {
                    setCell(day, time, availabilities[day][time] ? CellState.AVAILABLE : CellState.UNAVAILABLE);
                }
            }
        }
    }

    public void onNextClick(View view) {
        if (!itemClickEnabled) { return; }
        if (null == requestedCell) {
            Toast.makeText(getApplicationContext(), getString(R.string.timeslot_required), Toast.LENGTH_LONG).show();
            return;
        }
        cal.setTime((Date) txt_day_nums[requestedCell[0]].getTag());
        cal.set(Calendar.HOUR_OF_DAY, requestedCell[1]);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(getApplicationContext(), getString(R.string.start_time_in_the_past), Toast.LENGTH_LONG).show();
            return;
        }
        long startTime = cal.getTimeInMillis();
        int day = requestedCell[0];
        int time = requestedCell[1] + 1;
        while (CellState.REQUESTED == cells[day][time].cellState) {
            time++;
        }
        cal.set(Calendar.HOUR_OF_DAY, time);
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

    private void loadProviderAvailabilities() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        if (null == currentProvider.getAvailabilities()) {
            setAvailabilities(Availability.toArrays(new LinkedList<Availability>()));
        } else {
            setAvailabilities(Availability.toArrays(currentProvider.getAvailabilities()));
        }
        itemClickEnabled = true;
    }

    private boolean[][] getAvailabilities() {
        boolean[][] availabilities = new boolean[7][24];
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                availabilities[day][time] = (CellState.AVAILABLE == cells[day][time].cellState);
            }
        }
        return availabilities;
    }

    private void setAllCells(@NonNull CellState cellState) {
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                setCell(day, time, cellState);
            }
        }
    }

    private void updateCellView(int day, int time) {
        switch (cells[day][time].cellState) {
            case AVAILABLE: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_available); break;
            case BOOKED: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_booked); break;
            case REQUESTED: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_requested); break;
            case UNAVAILABLE:
            default: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default);
        }
    }

    private void setCell(int day, int time, CellState cellState) {
        cells[day][time].cellState = cellState;
        updateCellView(day, time);
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

    public void setRequested(int day, int time) {
        //TODO: detect clicks with a break in the availability and move the requested block
        Cell cell = cells[day][time];
        if (CellState.AVAILABLE == cell.cellState) {
            if (null == requestedCell) {
                setCell(cell.day, cell.time, CellState.REQUESTED);
                requestedCell = new int[]{ day, time };
            } else if (requestedCell[0] == day) {
                int cursor = requestedCell[1];
                if (time < requestedCell[1]) {
                    do { requestedCell[1] = cursor; setCell(day, cursor--, CellState.REQUESTED); }
                    while (cursor >= time && (CellState.AVAILABLE == cells[day][cursor].cellState || CellState.REQUESTED == cells[day][cursor].cellState));
                } else {
                    do { setCell(day, cursor++, CellState.REQUESTED); }
                    while (cursor <= time && (CellState.AVAILABLE == cells[day][cursor].cellState || CellState.REQUESTED == cells[day][cursor].cellState));
                }
            } else {
                loadProviderAvailabilities();
                setCell(cell.day, cell.time, CellState.REQUESTED);
                requestedCell = new int[]{ day, time };
            }

        } else if (CellState.REQUESTED == cell.cellState) {
            int cursor = time;
            do { setCell(day, cursor--, CellState.AVAILABLE); }
            while (cursor >= 0 && CellState.REQUESTED == cells[day][cursor].cellState);
            cursor = time + 1;
            while (cursor <= 23 && CellState.REQUESTED == cells[day][cursor].cellState) {
                setCell(day, cursor++, CellState.AVAILABLE);
            }
            requestedCell = null;
        }
    }

    public void toggleAvailability(int day, int time) {
        Cell cell = cells[day][time];
        if (CellState.UNAVAILABLE != cell.cellState) {
            setCell(cell.day, cell.time, CellState.UNAVAILABLE);
        } else {
            setCell(cell.day, cell.time, CellState.AVAILABLE);
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

                if (CellState.UNAVAILABLE == cell.cellState) {
                    do { setCell(day, time--, CellState.AVAILABLE); }
                    while (time >= 0 && CellState.UNAVAILABLE == cells[day][time].cellState);
                } else if (CellState.AVAILABLE == cell.cellState) {
                    do { setCell(day, time--, CellState.UNAVAILABLE); }
                    while (time >= 0 && CellState.AVAILABLE == cells[day][time].cellState);
                    time = cell.time + 1;
                    while (time <= 23 && CellState.AVAILABLE == cells[day][time].cellState) {
                        setCell(day, time++, CellState.UNAVAILABLE);
                    }
                }
            }

            return true;
        }
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
