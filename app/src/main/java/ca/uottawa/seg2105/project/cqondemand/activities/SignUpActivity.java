package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.Authentication;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;

/**
 * The class <b>SignUpActivity</b> is a UI class that allows a user to create their account.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class SignUpActivity extends AppCompatActivity {

    /**
     * An enum defining the screen configurations of this activity
     */
    private enum Screen { FIELDS_1, FIELDS_2 }

    /**
     * The currently active screen
     */
    protected Screen screen;

    /**
     * A field that accepts the username input
     */
    protected EditText field_username;

    /**
     * A field that accepts the first name input
     */
    protected EditText field_first_name;

    /**
     * A field that accepts the last name input
     */
    protected EditText field_last_name;

    /**
     * A field that accepts the email address input
     */
    protected EditText field_email;

    /**
     * A field that accepts the password input
     */
    protected EditText field_password;

    /**
     * A field that accepts the confirmation password input
     */
    protected EditText field_password_confirm;

    /**
     * A spinner that sets the user type selection
     */
    protected Spinner spinner_user_type;

    /**
     * A field that allows the user type error to be displayed
     */
    protected EditText field_user_type_error;

    /**
     * A field that accepts the company name input
     */
    protected EditText field_company_name;

    /**
     * A field that is used to set the licensed status
     */
    protected Switch switch_licensed;

    /**
     * A field that accepts the phone number input
     */
    protected EditText field_phone;

    /**
     * A field that accepts the description input
     */
    protected EditText field_description;

    /**
     * A field that accepts the address unit input
     */
    protected EditText field_unit;

    /**
     * A field that accepts the street number input
     */
    protected EditText field_street_number;

    /**
     * A field that accepts the street name input
     */
    protected EditText field_street_name;

    /**
     * A field that accepts the city input
     */
    protected EditText field_city;

    /**
     * A field that accepts the country input
     */
    protected EditText field_country;

    /**
     * A spinner that allows the province to be selected
     */
    protected Spinner spinner_province;

    /**
     * A field that allows the province error to be displayed
     */
    protected EditText field_province_error;

    /**
     * A field that accepts the postal code input
     */
    protected EditText field_postal;

    /**
     * A view group that contains the views associated to fields_1
     */
    protected LinearLayout fields_1;

    /**
     * A view group that contains the views associated to fields_2
     */
    protected LinearLayout fields_2;

    /**
     * A button that triggers the create account action
     */
    protected Button btn_create_account;

    /**
     * A button that transitions to the next field set
     */
    protected Button btn_next;

    /**
     * Stores the first name value
     */
    protected String firstName;

    /**
     * Stores the lase name value
     */
    protected String lastName;

    /**
     * Stores the username value
     */
    protected String username;

    /**
     * Stores the email address value
     */
    protected String email;

    /**
     * Stores the password value
     */
    protected String password;

    /**
     * Stores the confirmation password value
     */
    protected String passwordConfirm;

    /**
     * Stores the user type value
     */
    protected User.Type userType;

    /**
     * Stores the company name value
     */
    protected String companyName;

    /**
     * Stores the licensed status value
     */
    protected boolean licensed;

    /**
     * Stores the phone number value
     */
    protected String phoneNumber;

    /**
     * Stores the description value
     */
    protected String description;

    /**
     * Stores the address unit value
     */
    protected String unit;

    /**
     * Stores the street number text value
     */
    protected String streetNumber;

    /**
     * Stores the street number integer value
     */
    protected int streetNumberInt;

    /**
     * Stores the street name value
     */
    protected String streetName;

    /**
     * Stores the city value
     */
    protected String city;

    /**
     * Stores the province value
     */
    protected String province;

    /**
     * Stores the country value
     */
    protected String country;

    /**
     * Stores the postal code value
     */
    protected String postalCode;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Screen 1
        spinner_user_type = findViewById(R.id.spinner_user_type);
        field_user_type_error = findViewById(R.id.field_user_type_error);
        field_username = findViewById(R.id.field_username);
        field_first_name = findViewById(R.id.field_first_name);
        field_last_name = findViewById(R.id.field_last_name);
        field_email = findViewById(R.id.field_email);
        field_password = findViewById(R.id.field_password);
        field_password_confirm = findViewById(R.id.field_password_confirm);
        // Screen 2
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
        // Screen Groups and buttons
        fields_1 = findViewById(R.id.fields_1);
        fields_2 = findViewById(R.id.fields_2);
        btn_create_account = findViewById(R.id.btn_create_account);
        btn_next = findViewById(R.id.btn_next);

        // Setup initial state for UI elements
        setScreen(Screen.FIELDS_1, true);
        // Get username from the sign in activity
        Intent intent = getIntent();
        field_username.setText(intent.getStringExtra("username"));
        // Configure the user type selection spinner
        User.Type[] userTypes = new User.Type[]{ null, User.Type.HOMEOWNER, User.Type.SERVICE_PROVIDER };
        spinner_user_type.setAdapter(new SpinnerAdapter<User.Type>(getApplicationContext(), R.layout.spinner_item_title, getString(R.string.select_account_type), userTypes));
        spinner_user_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) == User.Type.SERVICE_PROVIDER) { setScreen(Screen.FIELDS_1, false); }
                else { setScreen(Screen.FIELDS_1, true); }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        // Configure the province selection spinner
        ArrayList<String> provinces = new ArrayList<String>(Arrays.asList(Address.PROVINCES));
        provinces.add(0, null);
        spinner_province.setAdapter(new SpinnerAdapter<String>(getApplicationContext(), R.layout.spinner_item_title, getString(R.string.select_province), provinces));

    }

    /**
     * Overrides the back button for this activity. When not in the fields_1 field set, go back in the list
     * of field sets. When on the fields_1 field set, finish the activity with a canceled result.
     */
    @Override
    public void onBackPressed() {
        if (Screen.FIELDS_2 == screen) {
            setScreen(Screen.FIELDS_1, false);
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    /**
     * Sets the current field set
     * @param screen
     * @param showSubmit
     */
    protected void setScreen(@NonNull Screen screen, boolean showSubmit) {
        this.screen = screen;
        switch (screen) {
            case FIELDS_1:
                fields_1.setVisibility(View.VISIBLE);
                fields_2.setVisibility(View.GONE);
                break;
            case FIELDS_2:
                fields_1.setVisibility(View.GONE);
                fields_2.setVisibility(View.VISIBLE);
                break;
        }
        if (showSubmit) {
            btn_create_account.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
        } else {
            btn_create_account.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Validates the fields in fields_1
     * @return true if the fields in fields_1 have valid data
     */
    protected boolean fields_1AreValid() {

        firstName = field_first_name.getText().toString().trim();
        lastName = field_last_name.getText().toString().trim();
        username = field_username.getText().toString().trim();
        email = field_email.getText().toString().trim();
        password = field_password.getText().toString();
        passwordConfirm = field_password_confirm.getText().toString();
        userType = (User.Type) spinner_user_type.getSelectedItem();

        // Check valid spinner selection
        if (null == userType) {
            ((TextView)spinner_user_type.getSelectedView()).setError(getString(R.string.please_select_type));
            field_user_type_error.setError(getString(R.string.please_select_type));
            field_user_type_error.requestFocus();
            spinner_user_type.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.usernameIsValid(username) || FieldValidation.usernameIsReserved(username)) {
            if (username.isEmpty()) { field_username.setError(getString(R.string.empty_username_error)); }
            else if (FieldValidation.usernameIsReserved(username)) { field_username.setError(getString(R.string.banned_username_msg)); }
            else { field_username.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.USERNAME_CHARS)); }
            field_username.requestFocus();
            field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.personNameIsValid(firstName)) {
            if (username.isEmpty()) { field_first_name.setError(getString(R.string.empty_first_name_error)); }
            else { field_first_name.setError(String.format(getString(R.string.chars_not_allowed_template), FieldValidation.ILLEGAL_PERSON_NAME_CHARS)); }
            field_first_name.requestFocus();
            field_first_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.personNameIsValid(lastName)) {
            if (username.isEmpty()) { field_last_name.setError(getString(R.string.empty_last_name_error)); }
            else { field_last_name.setError(String.format(getString(R.string.chars_not_allowed_template), FieldValidation.ILLEGAL_PERSON_NAME_CHARS)); }
            field_last_name.requestFocus();
            field_last_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.emailIsValid(email)) {
            if (username.isEmpty()) { field_email.setError(getString(R.string.empty_email_error)); }
            else { field_email.setError(getString(R.string.invalid_email_error)); }
            field_email.requestFocus();
            field_email.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        Boolean passwordError = true;
        switch (FieldValidation.validatePassword(username, password, passwordConfirm)) {
            case VALID: passwordError = false; break;
            case EMPTY: field_password.setError(getString(R.string.empty_password_error)); break;
            case TOO_SHORT: field_password.setError(getString(R.string.password_too_short_error)); break;
            case CONFIRM_MISMATCH:
                field_password_confirm.setError(getString(R.string.password_mismatch_error));
                field_password_confirm.requestFocus();
                field_password_confirm.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return false;
            case ILLEGAL_PASSWORD: field_password.setError(getString(R.string.banned_password_error)); break;
            case CONTAINS_USERNAME: field_password.setError(getString(R.string.password_contains_username)); break;
            default: field_password.setError(getString(R.string.password_error_generic));
        }
        if (passwordError) {
            field_password.requestFocus();
            field_password.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        return true;

    }

    /**
     * Validates the fields in fields_2
     * @return true if the fields in fields_2 have valid data
     */
    protected boolean fields_2AreValid() {

        companyName = field_company_name.getText().toString().trim();
        licensed = switch_licensed.isChecked();
        phoneNumber = field_phone.getText().toString().trim();
        description = field_description.getText().toString().trim();
        if (description.isEmpty())  { description = null; }
        unit = field_unit.getText().toString().trim();
        streetNumber = field_street_number.getText().toString().trim();
        streetName = field_street_name.getText().toString().trim();
        city = field_city.getText().toString().trim();
        province = spinner_province.getSelectedItem() == null ? "" : spinner_province.getSelectedItem().toString();
        country = field_country.getText().toString().trim();
        postalCode = field_postal.getText().toString().trim().toUpperCase();

        if (!FieldValidation.companyNameIsValid(companyName)) {
            if (companyName.isEmpty()) { field_company_name.setError(getString(R.string.empty_company_name_error)); }
            else { field_company_name.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.COMPANY_NAME_CHARS)); }
            field_company_name.requestFocus();
            field_company_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.phoneIsValid(phoneNumber)) {
            if (phoneNumber.isEmpty()) { field_phone.setError(getString(R.string.empty_phone_error)); }
            else { field_phone.setError(getString(R.string.invalid_phone_error)); }
            field_phone.requestFocus();
            field_phone.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.unitIsValid(unit)) {
            field_unit.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.ADDRESS_UNIT_CHARS));
            field_unit.requestFocus();
            field_unit.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (streetNumber.isEmpty()) {
            field_street_number.setError(getString(R.string.empty_street_number_error));
            field_street_number.requestFocus();
            field_street_number.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }
        try {
            streetNumberInt = Integer.parseInt(streetNumber);
        } catch (NumberFormatException e) {
            field_street_number.setError(getString(R.string.invalid_street_number_error));
            field_street_number.requestFocus();
            field_street_number.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }
        if (!FieldValidation.streetNumberIsValid(streetNumberInt)) {
            field_street_number.setError(getString(R.string.invalid_street_number_error));
            field_street_number.requestFocus();
            field_street_number.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.streetNameIsValid(streetName)) {
            if (streetName.isEmpty()) { field_street_name.setError(getString(R.string.empty_street_name_error)); }
            else { field_street_name.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.STREET_NAME_CHARS)); }
            field_street_name.requestFocus();
            field_street_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.cityNameIsValid(city)) {
            if (city.isEmpty()) { field_city.setError(getString(R.string.empty_city_name_error)); }
            else { field_city.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.CITY_NAME_CHARS)); }
            field_city.requestFocus();
            field_city.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        // Check valid province spinner selection
        if (province.isEmpty()) {
            ((TextView)spinner_province.getSelectedView()).setError(getString(R.string.please_select_province_territory));
            field_province_error.setError(getString(R.string.please_select_province_territory));
            field_province_error.requestFocus();
            spinner_province.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.countryNameIsValid(country)) {
            if (country.isEmpty()) { field_country.setError(getString(R.string.empty_country_name_error)); }
            else { field_country.setError(String.format(getString(R.string.chars_allowed_template), FieldValidation.COUNTRY_NAME_CHARS)); }
            field_country.requestFocus();
            field_country.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        if (!FieldValidation.postalCodeIsValid(postalCode)) {
            if (postalCode.isEmpty()) { field_postal.setError(getString(R.string.empty_postal_code_error)); }
            else { field_postal.setError(getString(R.string.invalid_postal_code_error)); }
            field_postal.requestFocus();
            field_postal.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return false;
        }

        return true;
    }

    /**
     * The onClick handler for the create user button.
     * @param view the view object that was clicked
     */
    public void onCreateClick(View view) {

        if (!fields_1AreValid() || (User.Type.SERVICE_PROVIDER == userType && !fields_2AreValid())) { return; }

        password = Authentication.genHash(password);

        User newUser = null;
        if (User.Type.SERVICE_PROVIDER == userType) {
            Address address = new Address(unit, streetNumberInt, streetName, city, province, country, postalCode);
            newUser = new ServiceProvider(firstName, lastName, username, email, password, companyName, licensed, phoneNumber, address, description);
        } else {
            newUser = new User(firstName, lastName, username, email, userType, password);
        }

        btn_create_account.setEnabled(false);

        DbUser.createUser(newUser, new AsyncActionEventListener() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.account_create_success), username), Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("username", username);
                setResult(RESULT_OK, intent);
                finish();
            }
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), R.string.account_create_db_error, Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        if (Screen.FIELDS_2 == screen) { setScreen(Screen.FIELDS_1, false); }
                        field_username.setError(getString(R.string.username_already_taken));
                        field_username.requestFocus();
                        field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), R.string.generic_account_create_error, Toast.LENGTH_LONG).show();
                }
                btn_create_account.setEnabled(true);
            }
        });

    }

    /**
     * The onClick handler for the next button.  Transitions from the current field set to the next one.
     * @param view the view object that was clicked
     */
    public void onNextClick(View view) {
        if (fields_1AreValid()) {
            setScreen(Screen.FIELDS_2, true);
        }
    }

}
