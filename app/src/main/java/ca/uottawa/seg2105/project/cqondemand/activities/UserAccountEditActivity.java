package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ca.uottawa.seg2105.project.cqondemand.adapters.SpinnerAdapter;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountEditActivity extends SignedInActivity {

    protected User currentUser;
    protected EditText field_username;
    protected EditText field_first_name;
    protected EditText field_last_name;
    protected EditText field_email;
    protected LinearLayout fields_2;
    protected EditText field_company_name;
    protected Switch switch_licensed;
    protected EditText field_phone;
    protected EditText field_description;
    protected EditText field_unit;
    protected EditText field_street_number;
    protected EditText field_street_name;
    protected EditText field_city;
    protected EditText field_country;
    protected Spinner spinner_province;
    protected EditText field_province_error;
    protected EditText field_postal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_edit);
        // Set references to the  UI objects
        field_username = findViewById(R.id.field_username);
        field_first_name = findViewById(R.id.field_first_name);
        field_last_name = findViewById(R.id.field_last_name);
        field_email = findViewById(R.id.field_email);
        fields_2 = findViewById(R.id.fields_2);

        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("user");

        if (null != currentUser) {
            field_username.setText(currentUser.getUsername(), TextView.BufferType.EDITABLE);
            field_first_name.setText(currentUser.getFirstName(), TextView.BufferType.EDITABLE);
            field_last_name.setText(currentUser.getLastName(), TextView.BufferType.EDITABLE);
            field_email.setText(currentUser.getEmail(), TextView.BufferType.EDITABLE);
            if (currentUser instanceof ServiceProvider) {
                ServiceProvider provider = (ServiceProvider) currentUser;
                Address address = provider.getAddress();
                field_company_name = findViewById(R.id.field_company_name);
                switch_licensed = findViewById(R.id.switch_licensed);
                field_phone = findViewById(R.id.field_phone);
                field_description = findViewById(R.id.field_description);
                field_unit = findViewById(R.id.field_unit);
                field_street_number = findViewById(R.id.field_street_number);
                field_street_name = findViewById(R.id.field_street_name);
                field_city = findViewById(R.id.field_city);
                field_country = findViewById(R.id.field_country);
                spinner_province = findViewById(R.id.spinner_province);
                field_province_error = findViewById(R.id.field_province_error);
                field_postal = findViewById(R.id.field_postal);
                // Configure the province selection spinner
                ArrayList<String> provinces = new ArrayList<String>(Arrays.asList(Address.PROVINCES));
                provinces.add(0, null);
                SpinnerAdapter<String> provincesAdapter = new SpinnerAdapter<String>(getApplicationContext(), R.layout.spinner_item_title, getString(R.string.select_province), provinces);
                spinner_province.setAdapter(provincesAdapter);
                // Set the initial field values
                field_company_name.setText(provider.getCompanyName(), TextView.BufferType.EDITABLE);
                switch_licensed.setChecked(provider.isLicensed());
                field_phone.setText(provider.getPhoneNumber(), TextView.BufferType.EDITABLE);
                field_description.setText(provider.getDescription(), TextView.BufferType.EDITABLE);
                field_unit.setText(address.getUnit(), TextView.BufferType.EDITABLE);
                field_street_number.setText(String.valueOf(address.getStreetNumber()), TextView.BufferType.EDITABLE);
                field_street_name.setText(address.getStreet(), TextView.BufferType.EDITABLE);
                field_city.setText(address.getCity(), TextView.BufferType.EDITABLE);
                spinner_province.setSelection(provincesAdapter.getPosition(address.getProvince()));
                field_country.setText(address.getCountry(), TextView.BufferType.EDITABLE);
                field_postal.setText(address.getPostalCode(), TextView.BufferType.EDITABLE);
            } else {
                // Hide the Service Provider fields if we do not have a Service Provider user
                fields_2.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getApplicationContext(),  String.format(getString(R.string.current_item_provided_template), getString(R.string.account)), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onSaveUserClick(View view) {
        String firstName = field_first_name.getText().toString().trim();
        String lastName = field_last_name.getText().toString().trim();
        String username = field_username.getText().toString().trim();
        String email = field_email.getText().toString().trim();

        if (!FieldValidation.usernameIsValid(username) || FieldValidation.usernameIsReserved(username)) {
            if (username.isEmpty()) { field_username.setError(getString(R.string.empty_username_error)); }
            else if (FieldValidation.usernameIsReserved(username)) { field_username.setError(getString(R.string.banned_username_msg)); }
            else { field_username.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.USERNAME_CHARS)); }
            field_username.requestFocus();
            field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.personNameIsValid(firstName)) {
            if (username.isEmpty()) { field_first_name.setError(getString(R.string.empty_first_name_error)); }
            else { field_first_name.setError(String.format(getString(R.string.chars_not_allowed_template), FieldValidation.ILLEGAL_PERSON_NAME_CHARS)); }
            field_first_name.requestFocus();
            field_first_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.personNameIsValid(lastName)) {
            if (username.isEmpty()) { field_last_name.setError(getString(R.string.empty_last_name_error)); }
            else { field_last_name.setError(String.format(getString(R.string.chars_not_allowed_template), FieldValidation.ILLEGAL_PERSON_NAME_CHARS)); }
            field_last_name.requestFocus();
            field_last_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.emailIsValid(email)) {
            if (username.isEmpty()) { field_email.setError(getString(R.string.empty_email_error)); }
            else { field_email.setError(getString(R.string.invalid_email_error)); }
            field_email.requestFocus();
            field_email.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        // Validate Service Provider fields
        User newUser = null;
        if (User.Type.SERVICE_PROVIDER == currentUser.getType()) {
            ServiceProvider provider = (ServiceProvider) currentUser;
            String companyName = field_company_name.getText().toString().trim();
            boolean licensed = switch_licensed.isChecked();
            String phoneNumber = field_phone.getText().toString().trim();
            String description = field_description.getText().toString().trim();
            String unit = field_unit.getText().toString().trim();
            String streetNumber = field_street_number.getText().toString().trim();
            int streetNumberInt;
            String streetName = field_street_name.getText().toString().trim();
            String city = field_city.getText().toString().trim();
            String province = spinner_province.getSelectedItem().toString();
            String country = field_country.getText().toString().trim();
            String postalCode = field_postal.getText().toString().trim();

            if (!FieldValidation.companyNameIsValid(companyName)) {
                if (companyName.isEmpty()) { field_company_name.setError(getString(R.string.empty_company_name_error)); }
                else { field_company_name.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.COMPANY_NAME_CHARS)); }
                field_company_name.requestFocus();
                field_company_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (!FieldValidation.phoneIsValid(phoneNumber)) {
                if (phoneNumber.isEmpty()) { field_phone.setError(getString(R.string.empty_phone_error)); }
                else { field_phone.setError(getString(R.string.invalid_phone_error)); }
                field_phone.requestFocus();
                field_phone.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (!FieldValidation.unitIsValid(unit)) {
                field_unit.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.ADDRESS_UNIT_CHARS));
                field_unit.requestFocus();
                field_unit.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (streetNumber.isEmpty()) {
                field_street_number.setError(getString(R.string.empty_street_number_error));
                field_street_number.requestFocus();
                field_street_number.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }
            try {
                streetNumberInt = Integer.parseInt(streetNumber);
            } catch (NumberFormatException e) {
                field_street_number.setError(getString(R.string.invalid_street_number_error));
                field_street_number.requestFocus();
                field_street_number.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }
            if (!FieldValidation.streetNumberIsValid(streetNumberInt)) {
                field_street_number.setError(getString(R.string.invalid_street_number_error));
                field_street_number.requestFocus();
                field_street_number.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (!FieldValidation.streetNameIsValid(streetName)) {
                if (streetName.isEmpty()) { field_street_name.setError(getString(R.string.empty_street_name_error)); }
                else { field_street_name.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.STREET_NAME_CHARS)); }
                field_street_name.requestFocus();
                field_street_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (!FieldValidation.cityNameIsValid(city)) {
                if (city.isEmpty()) { field_city.setError(getString(R.string.empty_city_name_error)); }
                else { field_city.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.CITY_NAME_CHARS)); }
                field_city.requestFocus();
                field_city.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            // Check valid province spinner selection
            if (province.isEmpty()) {
                ((TextView)spinner_province.getSelectedView()).setError(getString(R.string.please_select_province_territory));
                field_province_error.setError(getString(R.string.please_select_province_territory));
                field_province_error.requestFocus();
                spinner_province.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (!FieldValidation.countryNameIsValid(country)) {
                if (country.isEmpty()) { field_country.setError(getString(R.string.empty_country_name_error)); }
                else { field_country.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.COUNTRY_NAME_CHARS)); }
                field_country.requestFocus();
                field_country.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            if (!FieldValidation.postalCodeIsValid(postalCode)) {
                if (postalCode.isEmpty()) { field_postal.setError(getString(R.string.empty_postal_code_error)); }
                else { field_postal.setError(getString(R.string.invalid_postal_code_error)); }
                field_postal.requestFocus();
                field_postal.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            }

            Address address = new Address(unit, streetNumberInt, streetName, city, province, country, postalCode);

            if (firstName.equals(currentUser.getFirstName()) && lastName.equals(currentUser.getLastName())
                    && username.equals(currentUser.getUsername()) && email.equals(currentUser.getEmail())
                    && provider.getAddress().equals(address) && companyName.equals(provider.getCompanyName())
                    && licensed == provider.isLicensed() && phoneNumber.equals(provider.getPhoneNumber())
                    && description.equals(provider.getDescription())) {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.no_changes_made_error_tempalte), getString(R.string.service).toLowerCase()), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (description.isEmpty()) { description = null; }
            newUser = new ServiceProvider(currentUser.getKey(), firstName, lastName, username, email, currentUser.getPassword(), companyName, licensed, phoneNumber, address, description);

        } else {

            if (firstName.equals(currentUser.getFirstName()) && lastName.equals(currentUser.getLastName())
                    && username.equals(currentUser.getUsername()) && email.equals(currentUser.getEmail())) {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.no_changes_made_error_tempalte), getString(R.string.service).toLowerCase()), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            newUser = new User(currentUser.getKey(), firstName, lastName, username, email, currentUser.getType(), currentUser.getPassword());

        }

        final Button btn_save_user = findViewById(R.id.btn_save_user);
        btn_save_user.setEnabled(false);
        final User updatedUser = newUser;

        DbUser.updateUser(updatedUser, new AsyncActionEventListener() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), R.string.account_update_success, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), UserAccountViewActivity.class);
                intent.putExtra("user", (User) updatedUser);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case NOT_UNIQUE:
                    case ALREADY_EXISTS:
                        btn_save_user.setEnabled(true);
                        field_username.setError(getString(R.string.username_already_taken));
                        field_username.requestFocus();
                        field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                        break;
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), R.string.account_update_db_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        // Some other kind of error
                        btn_save_user.setEnabled(true);
                        Toast.makeText(getApplicationContext(), R.string.account_update_generic_error, Toast.LENGTH_LONG).show();
                }
                btn_save_user.setEnabled(true);
            }
        });

    }

}
