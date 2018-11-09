package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.Provider;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;

public class ServiceCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_create);

        EditText field_service_name = findViewById(R.id.field_service_name);
        Intent intent = getIntent();
        field_service_name.setText(intent.getStringExtra("name"));

        Spinner spinner = findViewById(R.id.spinner_services);

        //For Testing purpose, data stored in layout/values/strings as a string array of size 5 named R.array.categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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

        //Check valid service name
        if (name.isEmpty()) {
            field_service_name.setError("Service name is required!");
            field_service_name.requestFocus();
            return;
        } else if (!Service.nameIsValid(name)) {
            field_service_name.setError("Username is invalid. " + Service.ILLEGAL_SERVICENAME_CHARS_REGEX);
            field_service_name.requestFocus();
            return;
        }

        //Check valid service rate


        //Service newService = new Service(name, rate);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
