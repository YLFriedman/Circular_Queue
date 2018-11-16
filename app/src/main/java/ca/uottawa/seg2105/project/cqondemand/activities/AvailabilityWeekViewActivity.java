package ca.uottawa.seg2105.project.cqondemand.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;

public class AvailabilityWeekViewActivity extends SignedInActivity {

    private enum State { DEFAULT, AVAILABLE, BOOKED, UNAVAILABLE }
    private class Cell {
        String[] dayNames = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
        String[] timeNames = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09" };
        public int day;
        public int time;
        public State state;
        public Cell() { state = State.DEFAULT; }
        public Cell(int day, int time) { this.day = day; this.time = time; this.state = state; state = State.DEFAULT; }
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

        // Fill the cellViews and cells arrays
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                cells[day][time] = new Cell(day, time);
                cellViews[day][time] = findViewById(getResources().getIdentifier(cells[day][time].getCellName(), "id", getPackageName()));
                cellViews[day][time].setLongClickable(true);
                cellViews[day][time].setOnLongClickListener(new CellLongClickListener());
                cellViews[day][time].setTag(cells[day][time]);
            }
        }

        //Testing
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                if ((day % 2 == 0 && time % 2 == 0) || (day % 2 != 0 && time % 2 != 0)) {
                    cells[day][time].state = State.AVAILABLE;
                    updateCellView(day, time);
                }
            }
        }
        updateAllCellViews();
    }

    private void updateAllCellViews() {
        for (int day = 0; day < 7; day++) {
            for (int time = 0; time < 24; time++) {
                updateCellView(day, time);
            }
        }
    }

    private void updateCellView(int day, int time) {
        switch (cells[day][time].state) {
            case AVAILABLE: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_available); break;
            case UNAVAILABLE: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default); break;
            case BOOKED: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_booked); break;
            case DEFAULT: cellViews[day][time].setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default);
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

        String toast;
        toast = "Cell: " + cell.getCellName();
        //toast += " (" + (cellViews[cell.day][cell.time] == view ? "Y" : "N") + ")";

        if (State.DEFAULT != cell.state) {
            setCell(cell.day, cell.time, State.DEFAULT);
        } else {
            setCell(cell.day, cell.time, State.AVAILABLE);
        }


        //Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
    }

    public void onCellLongPress(View view) {


    }

    private class CellLongClickListener implements View.OnLongClickListener {

        public boolean onLongClick(View view) {
            Cell cell;
            if (view.getTag() instanceof Cell) { cell = (Cell) view.getTag(); }
            else { return false; }
            setCell(cell.day, cell.time, State.BOOKED);

            return true;
        }
    }
}
