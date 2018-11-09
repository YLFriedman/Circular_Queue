package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

import static java.lang.Integer.parseInt;

public class ServiceCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_create);

        EditText field_service_name = findViewById(R.id.field_service_name);
        Intent intent = getIntent();
        field_service_name.setText(intent.getStringExtra("name"));

        spinner = findViewById(R.id.spinner_services);
        spinner.setOnItemSelectedListener(this);

        Category.getCategories(new AsyncValueEventListener<Category>() {
            @Override
            public void onSuccess(ArrayList<Category> data) {
                loadSpinnerData(data);
            }

            @Override
            public void onFailure(AsyncEventFailureReason reason) {

            }
        });
    }

    private void loadSpinnerData(ArrayList<Category> data){

        List<String> names = new ArrayList<String>();
        for (Category cat: data){
            names.add(cat.getName());
        }
        names.add(0, "<Select Category>");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onCreateCategory(View view){
        final EditText field_service_name = findViewById(R.id.field_service_name);
        final String name = field_service_name.getText().toString().trim();

        final EditText field_rate = findViewById(R.id.field_rate);
        final String rate = field_rate.getText().toString().trim();
        int rateNum = 0;

        //Check valid service name
        if (name.isEmpty()) {
            field_service_name.setError("Service name is required!");
            field_service_name.requestFocus();
            return;
        } else if (!Service.nameIsValid(name)) {
            field_service_name.setError("Service name is invalid. " + Service.ILLEGAL_SERVICENAME_CHARS_MSG);
            field_service_name.requestFocus();
            return;
        }

        //Check valid service rate
        if (rate.isEmpty()) {
            field_rate.setError("Service rate is required!");
            field_rate.requestFocus();
            return;
        }
        try{
            rateNum = Integer.parseInt(rate);
        } catch(NumberFormatException e) {
            field_rate.setError("Service rate is too large! Max value is " + Integer.MAX_VALUE);
            field_rate.requestFocus();
            return;
        }
        if (rateNum < 0){
            field_rate.setError("Service rate must be positive!");
            field_rate.requestFocus();
            return;
        }

        //Check valid spinner selection
        if (categoryName.equals("<Select Category>")){
            Toast.makeText(getApplicationContext(), "Please select a category!", Toast.LENGTH_SHORT).show();
            ((TextView)spinner.getSelectedView()).setError("Please select a category!");
            return;
        }

        Service newService = new Service(name, rateNum, DbUtil.getKey(new Category(categoryName)));
        final Button btn_create_service = findViewById(R.id.btn_create_service);
        btn_create_service.setEnabled(false);
        newService.create(new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The service '" + name + "' has been successfully created.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), "Unable to create your service at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_service_name.setError("Service name already exist!");
                        field_service_name.requestFocus();
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), "Unable to create your service at this time. Please try again later.", Toast.LENGTH_LONG).show();
                }
                btn_create_service.setEnabled(true);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categoryName = parent.getItemAtPosition(position).toString();

        Toast.makeText(parent.getContext(), categoryName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
