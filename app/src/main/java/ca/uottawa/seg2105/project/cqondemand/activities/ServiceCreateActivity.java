package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;

public class ServiceCreateActivity extends SignedInActivity {

    Spinner spinner_categories;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_create);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category_name");
        spinner_categories = findViewById(R.id.spinner_categories);
    }

    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        DbCategory.getCategories(new AsyncValueEventListener<Category>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Category> data) {
                loadSpinnerData(data);
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_title, names);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_categories.setAdapter(dataAdapter);
        if (categoryName != null) { spinner_categories.setSelection(dataAdapter.getPosition(categoryName)); }
    }

    public void onCreateCategory(View view) {
        final EditText field_service_name = findViewById(R.id.field_service_name);
        final String name = field_service_name.getText().toString().trim();

        final EditText field_rate = findViewById(R.id.field_rate);
        final String rate = field_rate.getText().toString().trim();
        int rateNum = 0;

        categoryName = spinner_categories.getSelectedItem().toString();
        EditText field_spinner_categories_error = findViewById(R.id.field_spinner_categories_error);

        // Check valid spinner selection
        if (categoryName.equals("<Select Category>")) {
            ((TextView)spinner_categories.getSelectedView()).setError("Please select a category!");
            field_spinner_categories_error.setError("Please select a category!");
            field_spinner_categories_error.requestFocus();
            spinner_categories.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        //Check valid service name
        if (name.isEmpty()) {
            field_service_name.setError("Service name is required!");
            field_service_name.requestFocus();
            field_service_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        } else if (!FieldValidation.serviceNameIsValid(name)) {
            field_service_name.setError("Service name is invalid. " + FieldValidation.ILLEGAL_SERVICE_NAME_CHARS_MSG);
            field_service_name.requestFocus();
            field_service_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        //Check valid service rate
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
        if (rateNum < 0){
            field_rate.setError("Service rate cannot be negative!");
            field_rate.requestFocus();
            field_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        Service newService = new Service(name, rateNum, DbUtil.getKey(new Category(categoryName)));
        final Button btn_create_service = findViewById(R.id.btn_create_service);
        btn_create_service.setEnabled(false);
        DbService.createService(newService, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The service '" + name + "' has been successfully created.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ServiceListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("category_name", categoryName);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), "Unable to create your service at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_service_name.setError("Service name already exist!");
                        field_service_name.requestFocus();
                        break;
                    default: // Some other kind of error
                        Toast.makeText(getApplicationContext(), "Unable to create your service at this time. Please try again later.", Toast.LENGTH_LONG).show();
                }
                btn_create_service.setEnabled(true);
            }
        });
    }

}
