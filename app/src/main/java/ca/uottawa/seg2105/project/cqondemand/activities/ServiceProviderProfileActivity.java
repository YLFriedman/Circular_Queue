package ca.uottawa.seg2105.project.cqondemand.activities;

import androidx.appcompat.app.AppCompatActivity;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ServiceProviderProfileActivity extends AppCompatActivity {

    protected boolean itemClickEnabled = true;
    protected ServiceProvider currentProvider;
    protected Service currentService;
    protected TextView txt_full_name;
    protected TextView txt_email;
    protected TextView txt_company_name;
    protected TextView txt_phone;
    protected TextView txt_description;
    protected TextView txt_licensed;
    protected TextView txt_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_profile);

        Intent intent = getIntent();
        try {
            currentProvider = (ServiceProvider) intent.getSerializableExtra("provider");
            currentService = (Service) intent.getSerializableExtra("service");
        } catch (ClassCastException e) {
            // TODO: Toast message for invalid type
            finish();
            return;
        }

        if (null == currentProvider) {
            // TODO: Toast message for no provider
            finish();
            return;
        }

        txt_full_name = findViewById(R.id.txt_full_name);
        txt_email = findViewById(R.id.txt_email);
        txt_company_name = findViewById(R.id.txt_company_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_description = findViewById(R.id.txt_description);
        txt_licensed = findViewById(R.id.txt_licensed);
        txt_address = findViewById(R.id.txt_address);

        setupFields();
    }

    private void setupFields() {
        if (null == currentProvider) {
            txt_full_name.setText("");
            txt_email.setText("");
        } else {
            txt_full_name.setText(String.format(getString(R.string.full_name_template), currentProvider.getFirstName(), currentProvider.getLastName()));
            txt_email.setText(currentProvider.getEmail());
            txt_company_name.setText(currentProvider.getCompanyName());
            txt_phone.setText(currentProvider.getPhoneNumber());
            txt_description.setText(currentProvider.getDescription());
            txt_licensed.setText(currentProvider.isLicensed() ? getText(R.string.yes) : getText(R.string.no));
            txt_address.setText(currentProvider.getAddress().toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        itemClickEnabled = true;
    }

}
