package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.ServiceProviderPickerListAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceProviderPickerActivity extends SignedInActivity implements DialogProviderFiltersFragment.OnFragmentInteractionListener {

    protected boolean itemClickEnabled = true;
    protected RecyclerView recycler_list;
    protected DbListenerHandle<?> dbListenerHandle;
    protected Service currentService;
    protected TextView txt_empty_list_message;
    protected int minRating;
    protected Date startTime;
    protected Date endTime;
    protected ArrayList<ServiceProvider> allProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_picker);
        recycler_list = findViewById(R.id.recycler_list);
        txt_empty_list_message = findViewById(R.id.txt_empty_list_message);
        User signedInUser = State.getInstance().getSignedInUser();
        if (null == signedInUser) { return; }

        Intent intent = getIntent();
        // Get the current category and current user
        currentService = (Service) intent.getSerializableExtra("service");
        if (null == currentService) {
            Toast.makeText(getApplicationContext(), R.string.service_required, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recycler_list.setHasFixedSize(true);
        recycler_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        if (allProviders.size() < 1 || (0 == minRating && null == startTime && null == endTime)) {
            filteredProviders = allProviders;
        } else {
            filteredProviders = new ArrayList<ServiceProvider>(allProviders.size());
            for (ServiceProvider provider: allProviders) {
                if ((provider.getRating() / 100) < minRating) { continue; }
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

    protected void showFilterSettings() {
        itemClickEnabled = false;
        long start = null != startTime ? startTime.getTime() : -1;
        long end = null != endTime ? endTime.getTime() : -1;
        FragmentManager fm = getSupportFragmentManager();
        DialogProviderFiltersFragment providerFilterDialog = DialogProviderFiltersFragment.newInstance(minRating, start, end);
        providerFilterDialog.show(fm, "fragment_filter_providers");
    }

    @Override
    public void onFiltersApply(int minRating, Date startTime, Date endTime) {
        this.minRating = minRating;
        this.startTime = startTime;
        this.endTime = endTime;
        setListAdapter();
        //Toast.makeText(getApplicationContext(), "Min Rating: " + minRating, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onFiltersDialogDismiss() {
        itemClickEnabled = true;
    }

}
