package ca.uottawa.seg2105.project.cqondemand.activities;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbListener;
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceEditActivity extends SignedInActivity {

    protected Spinner spinner_categories;
    protected String categoryName;
    protected Service currentService;
    protected DbListener<?> dbListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_edit);
        spinner_categories = findViewById(R.id.spinner_categories);
        EditText field_service_name = findViewById(R.id.field_service_name);
        EditText field_rate = findViewById(R.id.field_rate);
        currentService = State.getState().getCurrentService();
        State.getState().setCurrentService(null);
        if (null != currentService) {
            field_service_name.setText(currentService.getName());
            field_rate.setText(String.valueOf(currentService.getRate()));
            dbListener = DbCategory.getCategoriesLive(new AsyncValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull ArrayList<Category> data) {
                    loadSpinnerData(data);
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), getString(R.string.category_list_db_error), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the data listener for the services list
        if (null != dbListener) { dbListener.removeListener(); }
    }

    private void loadSpinnerData(ArrayList<Category> data) {
        // Check if there was already a selection made
        Object currentSelectedCategory = spinner_categories.getSelectedItem();
        if (null != currentSelectedCategory && !currentSelectedCategory.toString().equals(getString(R.string.category_list_select))) {
            categoryName = currentSelectedCategory.toString();
        }
        // Convert the categories list to a string list of category names to be passed to the spinner's adapter
        List<String> names = new ArrayList<String>();
        names.add(getString(R.string.category_list_select));
        for (Category category: data) { names.add(category.getName()); }
        // Create the adapter and pass it to the spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_title, names);
        spinner_categories.setAdapter(dataAdapter);
        // Set the spinner to be the previously selected or initial category
        if (null == categoryName) {
            // If no category exists, attempt to get one from the currentService object
            currentService.getCategory(new AsyncSingleValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull Category item) {
                    categoryName = item.getName();
                    spinner_categories.setSelection(dataAdapter.getPosition(categoryName));
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) { }
            });
        } else { spinner_categories.setSelection(dataAdapter.getPosition(categoryName)); }
    }

    public void onSaveService(View view) {
        final EditText field_service_name = findViewById(R.id.field_service_name);
        final String name = field_service_name.getText().toString().trim();

        final EditText field_rate = findViewById(R.id.field_rate);
        final String rate = field_rate.getText().toString().trim();
        int rateNum = 0;

        categoryName = spinner_categories.getSelectedItem().toString();
        EditText field_spinner_categories_error = findViewById(R.id.field_spinner_categories_error);

        // Check valid category selection
        if (categoryName.equals(getString(R.string.category_select))) {
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

        final Service newService;
        newService = new Service(name, rateNum, categoryName);

        final Button btn_save_service = findViewById(R.id.btn_save_service);
        btn_save_service.setEnabled(false);

        DbService.updateService(currentService, newService, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                State.getState().setCurrentService(newService);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.service_update_success), newService.getName()), Toast.LENGTH_LONG).show();
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
