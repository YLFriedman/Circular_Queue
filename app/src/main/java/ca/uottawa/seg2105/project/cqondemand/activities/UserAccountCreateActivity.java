package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class UserAccountCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_create);
        EditText field_username = findViewById(R.id.field_username);
        Intent intent = getIntent();
        field_username.setText(intent.getStringExtra("username"));
        Spinner spinner_user_type = findViewById(R.id.spinner_user_type);
        spinner_user_type.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_title, new String[]{ "Homeowner", "Service Provider" }));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onCreateClick(View view){

        final EditText field_username = findViewById(R.id.field_username);
        EditText field_first_name = findViewById(R.id.field_first_name);
        EditText field_last_name = findViewById(R.id.field_last_name);
        EditText field_email = findViewById(R.id.field_email);
        EditText field_password = findViewById(R.id.field_password);
        EditText field_password_confirm = findViewById(R.id.field_password_confirm);
        Spinner spinner_user_type = findViewById(R.id.spinner_user_type);

        String firstName = field_first_name.getText().toString().trim();
        String lastName = field_last_name.getText().toString().trim();
        final String username = field_username.getText().toString().trim();
        String email = field_email.getText().toString().trim();
        String password = field_password.getText().toString();
        String passwordConfirm = field_password_confirm.getText().toString();

        if (!FieldValidation.usernameIsValid(username)) {
            if (username.isEmpty()) { field_username.setError(getString(R.string.empty_username_error)); }
            else { field_username.setError(getString(R.string.invalid_username_msg)); }
            field_username.requestFocus();
            field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.nameIsValid(firstName)) {
            if (username.isEmpty()) { field_first_name.setError(getString(R.string.empty_first_name_error)); }
            else { field_first_name.setError(getString(R.string.invalid_first_name_error)); }
            field_first_name.requestFocus();
            field_first_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.nameIsValid(lastName)) {
            if (username.isEmpty()) { field_last_name.setError(getString(R.string.empty_last_name_error)); }
            else { field_last_name.setError(getString(R.string.invalid_last_name_error)); }
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

        Boolean passwordError = true;
        switch (FieldValidation.validatePassword(username, password, passwordConfirm)) {
            case VALID: passwordError = false; break;
            case EMPTY: field_password.setError(getString(R.string.empty_password_error)); break;
            case TOO_SHORT: field_password.setError(getString(R.string.password_too_short_error)); break;
            case CONFIRM_MISMATCH:
                field_password_confirm.setError(getString(R.string.password_mismatch_error));
                field_password_confirm.requestFocus();
                field_password_confirm.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            case ILLEGAL_PASSWORD: field_password.setError(getString(R.string.banned_password_error)); break;
            case CONTAINS_USERNAME: field_password.setError(getString(R.string.password_contains_username)); break;
            default: field_password.setError(getString(R.string.password_error_generic));
        }
        if (passwordError) {
            field_password.requestFocus();
            field_password.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        String userType = spinner_user_type.getSelectedItem().toString();
        // Check valid spinner selection
        if (userType.isEmpty()) {
            EditText field_spinner_user_type_error = findViewById(R.id.field_spinner_user_type_error);
            ((TextView)spinner_user_type.getSelectedView()).setError("Please select a type!");
            field_spinner_user_type_error.setError("Please select a type!");
            field_spinner_user_type_error.requestFocus();
            spinner_user_type.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }
        User newUser = new User(firstName, lastName, username, email, User.parseType(userType), password);


        final Button btn_create_account = findViewById(R.id.btn_create_account);
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
                        field_username.setError(getString(R.string.username_already_taken));
                        field_username.requestFocus();
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), R.string.generic_account_create_error, Toast.LENGTH_LONG).show();
                }
                btn_create_account.setEnabled(true);
            }
        });

    }
}
