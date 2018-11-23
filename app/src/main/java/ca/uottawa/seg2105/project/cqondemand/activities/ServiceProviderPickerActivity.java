package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

public class ServiceProviderPickerActivity extends SignedInActivity {

    protected boolean itemClickEnabled = true;
    protected RecyclerView recycler_list;
    protected DbListenerHandle<?> dbListenerHandle;
    protected Service currentService;
    protected TextView txt_empty_list_message;

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
                if (data.size() < 1) { txt_empty_list_message.setVisibility(View.VISIBLE); }
                else { txt_empty_list_message.setVisibility(View.GONE); }
                recycler_list.setAdapter(new ServiceProviderPickerListAdapter(getApplicationContext(), data, new View.OnClickListener() {
                    public void onClick(final View view) {
                        if (!itemClickEnabled) { return; }
                        //itemClickEnabled = false;
                        /*Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
                        intent.putExtra("user", (Serializable) view.getTag());
                        startActivity(intent);*/
                    }
                }));
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

    protected void showFilterSettings() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.filter_providers)
                .setMessage(R.string.filter_providers)
                //.setIcon(R.drawable.ic_filter_list_light_24)
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) { itemClickEnabled = true; }
                })
                .setNegativeButton(R.string.cancel, null).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_primary_dark));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialog_red));
    }
}
