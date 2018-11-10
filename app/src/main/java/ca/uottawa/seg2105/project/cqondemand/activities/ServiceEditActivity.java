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
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceEditActivity extends SignedInActivity {

    private Spinner spinner_categories;
    private EditText field_service_name;
    private EditText field_rate;
    private String categoryName;
    private Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_edit);
        spinner_categories = findViewById(R.id.spinner_categories);
        field_service_name = findViewById(R.id.field_service_name);
        field_rate = findViewById(R.id.field_rate);
        currentService = State.getState().getCurrentService();
        if (null != currentService) {
            field_service_name.setText(currentService.getName());
            field_rate.setText(String.valueOf(currentService.getRate()));
            currentService.getCategory(new AsyncSingleValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull Category item) {
                    categoryName = item.getName();
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) { }
            });
        } else {
            finish();
        }
    }

    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (!currentService.equals(State.getState().getCurrentService())) {
            finish();
            return;
        }
        Category.getCategories(new AsyncValueEventListener<Category>() {
            @Override
            public void onSuccess(ArrayList<Category> data) {
                loadSpinnerData(data);
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), "Unable to load the category list at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        categoryName = spinner_categories.getSelectedItem().toString();
    }

    private void loadSpinnerData(ArrayList<Category> data) {
        List<String> names = new ArrayList<String>();
        for (Category category: data){ names.add(category.getName()); }
        names.add(0, "<Select Category>");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_categories.setAdapter(dataAdapter);
        if (categoryName != null) { spinner_categories.setSelection(dataAdapter.getPosition(categoryName)); }
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
        if (categoryName.equals("<Select Category>")) {
            ((TextView)spinner_categories.getSelectedView()).setError("Please select a category!");
            field_spinner_categories_error.setError("Please select a category!");
            field_spinner_categories_error.requestFocus();
            spinner_categories.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        // Check valid service name
        if (!Service.nameIsValid(name)) {
            if (name.isEmpty()) { field_service_name.setError("Service name is required!"); }
            else { field_service_name.setError("Service name is invalid. " + Service.ILLEGAL_SERVICENAME_CHARS_MSG); }
            field_service_name.requestFocus();
            field_service_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        // Check valid service rate
        if (rate.isEmpty()) {
            field_rate.setError("Service rate is required!");
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }
        try {
            rateNum = Integer.parseInt(rate);
        } catch(NumberFormatException e) {
            field_rate.setError("Service rate is too large! Max value is " + String.format(Locale.CANADA, "%,d", Integer.MAX_VALUE));
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }
        if (rateNum < 0) {
            field_rate.setError("Service rate cannot be negative!");
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        final Service newService;
        try {
            newService = new Service(name, rateNum, new Category(categoryName));
        } catch (InvalidDataException e) {
            Toast.makeText(getApplicationContext(), "Unable to create the service. An invalid input has been detected: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        final Button btn_save_service = findViewById(R.id.btn_save_service);
        btn_save_service.setEnabled(false);

        State.getState().getCurrentService().update(newService, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                State.getState().setCurrentService(newService);
                Toast.makeText(getApplicationContext(), "The service '" + name + "' has been successfully updated.", Toast.LENGTH_LONG).show();
                finish();
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), "Unable to update your service at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_service_name.setError("Service name already exist!");
                        field_service_name.requestFocus();
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), "Unable to update your service at this time. Please try again later.", Toast.LENGTH_LONG).show();
                }
                btn_save_service.setEnabled(true);
            }
        });
    }

}
