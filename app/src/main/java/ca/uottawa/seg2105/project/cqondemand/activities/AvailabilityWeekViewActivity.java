package ca.uottawa.seg2105.project.cqondemand.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.R;

public class AvailabilityWeekViewActivity extends SignedInActivity {

    boolean[][] state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_week_view);

        state = new boolean[7][24];

    }

    public void onCellClick(View cell) {

        String cellName = cell.getResources().getResourceEntryName(cell.getId());
        int day = -1;
        switch (cellName.substring(5,8)) {
            case "sun": day = 0; break;
            case "mon": day = 1; break;
            case "tue": day = 2; break;
            case "wed": day = 3; break;
            case "thu": day = 4; break;
            case "fri": day = 5; break;
            case "sat": day = 6;
        }
        int time = Integer.parseInt(cellName.substring(9));

        toggleCell(cell, day, time);

        //Toast.makeText(getApplicationContext(), "Cell: " + cellName + "\nDay: " + day + "\nTime: " + time, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "getResourceEntryName: " + getResourceEntryName + "\ngetResourceName: " + getResourceName, Toast.LENGTH_LONG).show();
    }

    private void toggleCell(View cell, int day, int time) {
        if (state[day][time]) {
            state[day][time] = false;
            cell.setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_default);
        } else {
            state[day][time] = true;
            cell.setBackgroundResource(R.drawable.btn_bg_avail_cell_bkrd_available);
        }
    }


}
