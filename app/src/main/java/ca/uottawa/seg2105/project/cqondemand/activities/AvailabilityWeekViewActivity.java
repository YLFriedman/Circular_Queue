package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbAvailability;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class AvailabilityWeekViewActivity extends SignedInActivity {

    //private static List<Availability> availabilities;

    private enum CellState { AVAILABLE, BOOKED, UNAVAILABLE }
    private class Cell {
        String[] dayNames = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
        String[] timeNames = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09" };
        public int day;
        public int time;
        public CellState cellState;
        public Cell() { cellState = CellState.UNAVAILABLE; }
        public Cell(int day, int time) { this.day = day; this.time = time; cellState = CellState.UNAVAILABLE; }
        public Cell(int day, int time, CellState cellState) { this.day = day; this.time = time; this.cellState = cellState; }
        public String getCellName() { return "cell_" + dayNames[day] + "_" + ((time < 10) ? timeNames[time] : time); }
    }

    View[][] cellViews;
    Cell[][] cells;
    protected User currentUser;

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

        currentUser = State.getState().getSignedInUser();

        if (currentUser instanceof ServiceProvider) {
            onResetClick();
        } else {
            // TODO: Toast message on fail
            finish();
        }

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
                setCell(day, time, availabilities[day][time] ? CellState.AVAILABLE : CellState.UNAVAILABLE);
            }
        }
    }

    public void onSaveClick(View view) {
        DbAvailability.setAvailabilities((ServiceProvider) currentUser, Availability.toList(getAvailabilities()), new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), getString(R.string.availability_saved_successfully), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), getString(R.string.availability_save_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onHelpClick() {

        new AlertDialog.Builder(this)
                //.setTitle(R.string.delete_category)
                //.setIcon(android.R.drawable.ic_menu_help)
                .setView(getLayoutInflater().inflate(R.layout.help_availabilities, null))
                .setNegativeButton(R.string.ok, null).show();



    }

    private void onClearClick() {
        setAllCells(CellState.UNAVAILABLE);
    }

    private void onResetClick() {
        DbAvailability.getAvailabilities((ServiceProvider) currentUser, new AsyncValueEventListener<Availability>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Availability> data) {
                setAvailabilities(Availability.toArrays(data));
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), getString(R.string.availability_load_failed), Toast.LENGTH_LONG).show();
            }
        });
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

        toggleAvailability(cell.day, cell.time);

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
            Cell cell;
            if (view.getTag() instanceof Cell) { cell = (Cell) view.getTag(); }
            else { return false; }

            if (CellState.UNAVAILABLE == cell.cellState) {
                int time = cell.time;
                int day = cell.day;
                do {
                    setCell(cell.day, time, CellState.AVAILABLE);
                    time--;
                } while (time >= 0 && CellState.UNAVAILABLE == cells[day][time].cellState);
            }

            return true;
        }
    }
}
