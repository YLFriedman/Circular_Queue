package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class ServiceEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner spinner;
    private String categoryName;
    private String serviceName;
    private String rate;

    private EditText field_service_name;
    private EditText field_rate;
    Service currentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_edit);
        Intent intent = getIntent();

        serviceName = intent.getStringExtra("service_name");
        categoryName = intent.getStringExtra("category_name");
        rate = intent.getStringExtra("rate");

        field_service_name = findViewById(R.id.field_service_name);
        field_rate = findViewById(R.id.field_rate);

        EditText field_service_name = findViewById(R.id.field_service_name);
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

        field_service_name.setText(serviceName);
        field_rate.setText(rate);

        //Create the current service
        currentService = new Service(serviceName, Integer.parseInt(rate), DbUtil.getKey(new Category(categoryName)));
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

        if(categoryName != null){
            spinner.setSelection(dataAdapter.getPosition(categoryName));
        }
    }

    public void onResume() {
        super.onResume();
        if (null == State.getState().getSignedInUser()) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void onSaveService(View view){
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
        final Button btn_save_service = findViewById(R.id.btn_save_service);
        btn_save_service.setEnabled(false);

        currentService.update(newService, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categoryName = parent.getItemAtPosition(position).toString();

        Toast.makeText(parent.getContext(), categoryName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
