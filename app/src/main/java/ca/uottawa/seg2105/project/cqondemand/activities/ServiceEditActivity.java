package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.adapters.SpinnerAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbListenerHandle;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;

/**
 * The class <b>ServiceEditActivity</b> is a UI class that allows the admin user to edit a service.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ServiceEditActivity extends SignedInActivity {

    /**
     * The view that displays the list of categories to choose from
     */
    protected Spinner spinner_categories;

    /**
     * The category that the service is being created under
     */
    protected Category currentCategory;

    /**
     * The service that is being edited
     */
    protected Service currentService;

    /**
     * Stores the handle to the database callback so that it can be cleaned up when the activity ends
     */
    protected DbListenerHandle<?> dbListenerHandle;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_edit);
        spinner_categories = findViewById(R.id.spinner_categories);
        EditText field_service_name = findViewById(R.id.field_service_name);
        EditText field_rate = findViewById(R.id.field_rate);
        // Get the service from the intent
        Intent intent = getIntent();
        try {
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // If we have a service, setup the activity
        if (null != currentService) {
            field_service_name.setText(currentService.getName());
            field_rate.setText(String.valueOf(currentService.getRate()));
            dbListenerHandle = DbCategory.getCategoriesLive(new AsyncValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull ArrayList<Category> data) {
                    data.add(0, null);
                    // Check if there was already a selection made
                    Object currentSelection = spinner_categories.getSelectedItem();
                    if (null != currentSelection && !currentSelection.toString().equals(getString(R.string.category_list_select))) {
                        currentCategory = (Category) currentSelection;
                    }
                    // Create the adapter and pass it to the spinner
                    final SpinnerAdapter<Category> dataAdapter = new SpinnerAdapter<Category>(getApplicationContext(), R.layout.spinner_item_title, getString(R.string.category_select), data);
                    spinner_categories.setAdapter(dataAdapter);
                    // Set the spinner to be the previously selected or initial category
                    if (null != currentCategory) { spinner_categories.setSelection(dataAdapter.getPosition(currentCategory)); }
                    else {
                        // If no category exists, attempt to get one from the currentService object
                        currentService.getCategory(new AsyncSingleValueEventListener<Category>() {
                            @Override
                            public void onSuccess(@NonNull Category item) { spinner_categories.setSelection(dataAdapter.getPosition(item)); }
                            @Override
                            public void onFailure(@NonNull AsyncEventFailureReason reason) { }
                        });
                    }
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), getString(R.string.category_list_db_error), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), R.string.current_service_empty, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Removes the listener for data from the database.
     * This is run during the destroy phase of the activity lifecycle.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener
        if (null != dbListenerHandle) { dbListenerHandle.removeListener(); }
    }

    /**
     * The on-click handler for the save service button
     * @param view the view object that was clicked
     */
    public void onSaveService(View view) {
        final EditText field_service_name = findViewById(R.id.field_service_name);
        final String name = field_service_name.getText().toString().trim();

        final EditText field_rate = findViewById(R.id.field_rate);
        final String rate = field_rate.getText().toString().trim();
        int rateNum = 0;

        final Category category = (Category) spinner_categories.getSelectedItem();
        EditText field_spinner_categories_error = findViewById(R.id.field_spinner_categories_error);

        // Check valid category selection
        if (category.getName().equals(getString(R.string.category_select))) {
            ((TextView)spinner_categories.getSelectedView()).setError(getString(R.string.category_selection_error));
            field_spinner_categories_error.setError(getString(R.string.category_selection_error));
            field_spinner_categories_error.requestFocus();
            spinner_categories.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        // Check valid service name
        if (!FieldValidation.serviceNameIsValid(name)) {
            if (name.isEmpty()) { field_service_name.setError(getString(R.string.empty_service_name_msg)); }
            else { field_service_name.setError(getString(R.string.service_name_invalid)); }
            field_service_name.requestFocus();
            field_service_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        // Check valid service rate
        if (rate.isEmpty()) {
            field_rate.setError(getString(R.string.empty_service_rate_error));
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }
        try {
            rateNum = Integer.parseInt(rate);
        } catch(NumberFormatException e) {
            field_rate.setError(String.format(getString(R.string.rate_too_high_msg), String.format(Locale.CANADA, "%,d", Integer.MAX_VALUE)));
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }
        if (rateNum < 0) {
            field_rate.setError(getString(R.string.negative_service_rate_error));
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (name.equals(currentService.getName()) && currentService.getRate() == rateNum && category.getKey().equals(currentService.getCategoryKey())) {
            Toast.makeText(getApplicationContext(), String.format(getString(R.string.no_changes_made_error_tempalte), getString(R.string.service).toLowerCase()), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final Service newService;
        newService = new Service(currentService.getKey(), name, rateNum, category.getKey());

        final Button btn_save_service = findViewById(R.id.btn_save_service);
        btn_save_service.setEnabled(false);

        DbService.updateService(newService, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_update_success), newService.getName()), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ServiceViewActivity.class);
                intent.putExtra("service", newService);
                intent.putExtra("category", category);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), R.string.service_update_db_error, Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_service_name.setError(getString(R.string.service_name_taken_error));
                        field_service_name.requestFocus();
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), R.string.service_update_error_generic, Toast.LENGTH_LONG).show();
                }
                btn_save_service.setEnabled(true);
            }
        });
    }

}
