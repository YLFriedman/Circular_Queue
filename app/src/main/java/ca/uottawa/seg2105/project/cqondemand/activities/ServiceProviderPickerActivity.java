package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceProviderPickerListAdapter;
import ca.uottawa.seg2105.project.cqondemand.adapters.SpinnerAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceProviderPickerActivity extends SignedInActivity {

    private boolean itemClickEnabled = true;
    private RecyclerView recycler_list;
    private DbListenerHandle<?> dbListenerHandle;
    private Service currentService;
    private TextView txt_empty_list_message;
    private int filterMinRating;
    private int filterDay = -1;
    private int filterStartTime = -1;
    private int filterEndTime = -1;
    private int selectedStartTime = -1;
    private int selectedEndTime = -1;
    private int tempStartTime;
    protected Calendar cal = Calendar.getInstance(Locale.CANADA);
    private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.CANADA);
    private ArrayList<ServiceProvider> allProviders;
    private AlertDialog filterDialog;
    private View filterView;
    private TextView txt_clear_availability;
    private TextView txt_clear_rating;
    private TextView txt_set_time;
    private RatingBar stars_filter_rating;
    private Spinner spinner_day_filter;
    private SpinnerAdapter<Availability.Day> dayFilterAdapter;
    private TextView txt_time_filter;
    private LinearLayout grp_time;
    private Timepoint[] selectableTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_picker);
        recycler_list = findViewById(R.id.recycler_list);
        txt_empty_list_message = findViewById(R.id.txt_empty_list_message);
        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) { return; }

        Intent intent = getIntent();
        try {
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (null == currentService) {
            Toast.makeText(getApplicationContext(), R.string.service_required, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        selectableTimes = new Timepoint[24];
        for (int i = 0; i < 24; i++) {

        }
        //Timepoint[] = new Timepoint()
        //(new Timepoint(4)).

        recycler_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recycler_list.setLayoutManager(layoutManager);
        dbListenerHandle = DbService.getProvidersByServiceLive(currentService, new AsyncValueEventListener<ServiceProvider>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ServiceProvider> data) {
                allProviders = data;
                if (data.size() < 1) { txt_empty_list_message.setVisibility(View.VISIBLE); }
                else { txt_empty_list_message.setVisibility(View.GONE); }
                setListAdapter();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), R.string.provider_list_db_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the users list
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_service_provider, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_filter_providers:
                showFilterSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setListAdapter() {
        if (null == allProviders) { return; }
        ArrayList<ServiceProvider> filteredProviders;
        if (allProviders.size() < 1 || (0 == filterMinRating)) {
            filteredProviders = allProviders;
        } else {
            filteredProviders = new ArrayList<ServiceProvider>(allProviders.size());
            for (ServiceProvider provider: allProviders) {
                if ((provider.getRating() / 100) < filterMinRating) { continue; }
                filteredProviders.add(provider);
            }
        }
        if (allProviders.size() > 0 && filteredProviders.size() < 1) {
            // TODO: Display message to adjust filters
        }
        recycler_list.setAdapter(new ServiceProviderPickerListAdapter(getApplicationContext(), filteredProviders, new View.OnClickListener() {
            public void onClick(final View view) {
                if (!itemClickEnabled) { return; }
                itemClickEnabled = false;
                Intent intent = new Intent(getApplicationContext(), ServiceProviderProfileActivity.class);
                intent.putExtra("provider", (Serializable) view.getTag());
                intent.putExtra("service", currentService);
                startActivity(intent);
            }
        }));
    }

    /*@Override
    public void onFiltersApply(int filterMinRating, Date startTime, Date endTime) {
        this.filterMinRating = filterMinRating;
        this.startTime = startTime;
        this.endTime = endTime;
        setListAdapter();
        //Toast.makeText(getApplicationContext(), "Min Rating: " + filterMinRating, Toast.LENGTH_LONG).show();
    }*/

    /*@Override
    public void onFiltersDialogDismiss() {
        itemClickEnabled = true;
    }*/

    protected void showFilterSettings() {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        if (null == filterView) { setupFilterView(); }
        setFilterValues();
        if (null == filterDialog) {
            filterDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.filters)
                    .setView(filterView)
                    .setIcon(R.drawable.ic_filter_list_med_24)
                    .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (getFilterValues()) { setListAdapter(); }
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            filterDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
            filterDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
        } else {
            filterDialog.show();
        }
    }

    private void setFilterValues() {
        stars_filter_rating.setRating(filterMinRating);
        spinner_day_filter.setSelection(filterDay + 1);
        if (filterDay < 0) {
            grp_time.setVisibility(View.GONE);
            selectedStartTime = -1;
            selectedEndTime = -1;
        } else {
            selectedStartTime = filterStartTime;
            selectedEndTime = filterEndTime;
            txt_time_filter.setText(getTimeString(selectedStartTime, selectedEndTime));
        }
    }

    private boolean getFilterValues() {
        int newMinRating = (int) stars_filter_rating.getRating();
        filterMinRating = newMinRating;
        filterDay = spinner_day_filter.getSelectedItemPosition() - 1;
        filterStartTime = selectedStartTime;
        filterEndTime = selectedEndTime;
        return true;
    }

    private void setupFilterView() {
        filterView = getLayoutInflater().inflate(R.layout.dialog_provider_filters, null);
        txt_clear_availability = filterView.findViewById(R.id.txt_clear_availability);
        txt_clear_rating = filterView.findViewById(R.id.txt_clear_rating);
        txt_set_time = filterView.findViewById(R.id.txt_set_time);
        stars_filter_rating = filterView.findViewById(R.id.stars_filter_rating);
        spinner_day_filter = filterView.findViewById(R.id.spinner_day_filter);
        txt_time_filter = filterView.findViewById(R.id.txt_time_filter);
        grp_time = filterView.findViewById(R.id.grp_time);
        grp_time.setVisibility(View.GONE);

        txt_clear_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { stars_filter_rating.setRating(0); }
        });
        ArrayList<Availability.Day> dayList = new ArrayList<Availability.Day>(Arrays.asList(Availability.Day.values()));
        dayList.add(0, null);
        dayFilterAdapter = new SpinnerAdapter<Availability.Day>(getApplicationContext(), R.layout.spinner_item_title, getString(R.string.any_day), dayList);
        spinner_day_filter.setAdapter(dayFilterAdapter);
        spinner_day_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (0 == position) { return; }
                grp_time.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        txt_clear_availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_day_filter.setSelection(0);
                selectedStartTime = -1;
                selectedEndTime = -1;
                txt_time_filter.setText(getString(R.string.any_time));
                grp_time.setVisibility(View.GONE);
            }
        });

        txt_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startWith = selectedStartTime < 0 ? 0 : selectedStartTime;
                TimePickerDialog tpd = TimePickerDialog.newInstance(getStartTimeListener(), startWith,0,false);
                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                tpd.setOkColor(getResources().getColor(R.color.dialog_red));
                tpd.setCancelColor(getResources().getColor(R.color.text_secondary_dark));
                tpd.enableMinutes(false);
                tpd.setTitle(getString(R.string.start_time));
                tpd.show(getSupportFragmentManager(), "start_time");
            }
        });

    }

    private TimePickerDialog.OnTimeSetListener getStartTimeListener() {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                tempStartTime = hourOfDay;
                TimePickerDialog tpd = TimePickerDialog.newInstance(getEndTimeListener(),hourOfDay + 1,0,false);
                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                tpd.setOkColor(getResources().getColor(R.color.dialog_red));
                tpd.setCancelColor(getResources().getColor(R.color.text_secondary_dark));
                tpd.setMinTime(hourOfDay + 1, 0, 0);
                tpd.enableMinutes(false);
                tpd.setTitle(getString(R.string.end_time));
                tpd.show(getSupportFragmentManager(), "end_time");
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener getEndTimeListener() {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                Toast.makeText(getApplicationContext(), "ST: " + tempStartTime + ", ET: " + hourOfDay, Toast.LENGTH_LONG).show();
                selectedStartTime = tempStartTime;
                selectedEndTime = hourOfDay;
                txt_time_filter.setText(getTimeString(selectedStartTime, selectedEndTime));
            }
        };
    }

    private String getTimeString(int start, int end) {
        String time = "";
        if (start >=0 && end >= 0) {
            cal.setTime(new Date());
            cal.set(0, 0, 0, start, 0, 0);
            time += TIME_FORMAT.format(cal.getTime());
            cal.set(0, 0, 0, end, 0, 0);
            time += " to " + TIME_FORMAT.format(cal.getTime());
        } else {
            time = getString(R.string.any_time);
        }
        return time;
    }

}
