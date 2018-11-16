package ca.uottawa.seg2105.project.cqondemand.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;

public class AvailabilityWeekViewActivity extends SignedInActivity {

    private static List<Availability> availabilities;

    private enum State { DEFAULT, AVAILABLE, BOOKED, UNAVAILABLE }
    private class Cell {
        String[] dayNames = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
        String[] timeNames = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09" };
        public int day;
        public int time;
        public State state;
        public Cell() { state = State.DEFAULT; }
        public Cell(int day, int time) { this.day = day; this.time = time; state = State.DEFAULT; }
        public Cell(int day, int time, State state) { this.day = day; this.time = time; this.state = state; }
        public String getCellName() { return "cell_" + dayNames[day] + "_" + ((time < 10) ? timeNames[time] : time); }
    }

    View[][] cellViews;
    Cell[][] cells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_week_view);

        // Create the arrays for the cellViews and cells
        cellViews = new View[7][24];
        cells = new Cell[7][24];

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

        if (availabilities != null) {
            setAvailabilities(Availability.toArrays(availabilities));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilities = Availability.toList(getAvailabilities());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.availabilities_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_avail_help: onHelpClick(); return true;
            case R.id.menu_item_avail_clear: onClearClick(); return true;
            case R.id.menu_item_avail_reset: onResetClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAvailabilities(@NonNull boolean[][] availabilities) {
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                setCell(day, time, availabilities[day][time] ? State.AVAILABLE : State.DEFAULT);
            }
        }
    }

    private void onHelpClick() {

    }

    private void onClearClick() {
        setAllCells(State.DEFAULT);
    }

    private void onResetClick() {
        if (availabilities != null) {
            setAvailabilities(Availability.toArrays(availabilities));
        }
    }

    private boolean[][] getAvailabilities() {
        boolean[][] availabilities = new boolean[7][24];
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                availabilities[day][time] = (State.AVAILABLE == cells[day][time].state);
            }
        }
        return availabilities;
    }

    private void setAllCells(@NonNull State state) {
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                setCell(day, time, state);
            }
        }
    }

    private void updateCellView(int day, int time) {
        switch (cells[day][time].state) {
            case AVAILABLE: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_available); break;
            case UNAVAILABLE: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default); break;
            case BOOKED: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_booked); break;
            case DEFAULT:
            default: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default);
        }
    }

    private void setCell(int day, int time, State state) {
        cells[day][time].state = state;
        updateCellView(day, time);
    }

    public void onCellClick(View view) {
        Cell cell;
        if (view.getTag() instanceof Cell) { cell = (Cell) view.getTag(); }
        else { return; }

        toggleAvailability(cell.day, cell.time);

    }

    public void toggleAvailability(int day, int time) {
        Cell cell = cells[day][time];
        if (State.DEFAULT != cell.state) {
            setCell(cell.day, cell.time, State.DEFAULT);
        } else {
            setCell(cell.day, cell.time, State.AVAILABLE);
        }
    }

    private class CellLongClickListener implements View.OnLongClickListener {
        public boolean onLongClick(View view) {
            Cell cell;
            if (view.getTag() instanceof Cell) { cell = (Cell) view.getTag(); }
            else { return false; }

            if (State.DEFAULT != cell.state) {
                setCell(cell.day, cell.time, State.DEFAULT);
            } else {
                setCell(cell.day, cell.time, State.BOOKED);
            }

            return true;
        }
    }
}
