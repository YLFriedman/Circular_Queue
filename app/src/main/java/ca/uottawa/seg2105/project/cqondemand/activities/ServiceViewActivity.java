package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceViewActivity extends SignedInActivity {

    TextView txt_name;
    TextView txt_rate;
    TextView txt_category;
    String serviceName;
    String categoryName;
    Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
        txt_name = findViewById(R.id.txt_name);
        txt_rate = findViewById(R.id.txt_rate);
        txt_category = findViewById(R.id.txt_category);
        txt_name.setText("");
        txt_rate.setText("");
        txt_category.setText("");
        Intent intent = getIntent();
        serviceName = intent.getStringExtra("service_name");
    }

    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (null != serviceName) {
            // Clear the text fields
            setupFields();
            // Try to get the service object
            Service.getService(serviceName, new AsyncSingleValueEventListener<Service>() {
                @Override
                public void onSuccess(@NonNull Service item) {
                    serviceName = null;
                    currentService = item;
                    setupFields();
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), "There was an error getting the service details from the database. Please try again later.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else if (null != State.getState().getCurrentService()) {
            currentService = State.getState().getCurrentService();
            State.getState().setCurrentService(null);
            setupFields();
        } else if (null != currentService) {
            setupFields();
        }  else {
            Toast.makeText(getApplicationContext(), "No service provided.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupFields() {
        txt_name.setText("");
        txt_rate.setText("");
        txt_category.setText("");
        if (null != currentService) {
            txt_name.setText(currentService.getName());
            if (0 == currentService.getRate()) {
                txt_rate.setText(getString(R.string.zero_value_service));
            } else {
                txt_rate.setText(String.format(Locale.CANADA, getString(R.string.service_rate_template), currentService.getRate()));
            }
            currentService.getCategory(new AsyncSingleValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull Category item) {
                    categoryName = item.getName();
                    txt_category.setText(String.format(Locale.CANADA, getString(R.string.category_template), item.getName()));
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) { }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        if (null != user && user.isAdmin()) {
            getMenuInflater().inflate(R.menu.service_options, menu);
            menu.setGroupVisible(R.id.grp_category_controls, false);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_service_create: onCreateServiceClick(); return true;
            case R.id.menu_item_service_edit: onEditServiceClick(); return true;
            case R.id.menu_item_service_delete: onDeleteServiceClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateServiceClick() {
        Intent intent = new Intent(getApplicationContext(), ServiceCreateActivity.class);
        if (null != categoryName) { intent.putExtra("category_name", categoryName); }
        startActivity(intent);
    }

    public void onEditServiceClick() {
        State.getState().setCurrentService(currentService);
        Intent intent = new Intent(getApplicationContext(), ServiceEditActivity.class);
        startActivity(intent);
    }

    public void onDeleteServiceClick() {
        if (null != currentService) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete the '" + currentService.getName() + "' service?  \r\nThis CANNOT be undone!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            currentService.delete(new AsyncActionEventListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), "The the '" + currentService.getName() + "' service has been successfully deleted.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                @Override
                                public void onFailure(AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), "Unable to delete the '" + currentService.getName() + "' service at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }
    }

}
