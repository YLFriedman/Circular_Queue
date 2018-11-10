package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class UserAccountCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_create);
        EditText field_username = findViewById(R.id.field_username);
        Intent intent = getIntent();
        field_username.setText(intent.getStringExtra("username"));
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

        String firstName = field_first_name.getText().toString().trim();
        String lastName = field_last_name.getText().toString().trim();
        final String username = field_username.getText().toString().trim();
        String email = field_email.getText().toString().trim();
        String password = field_password.getText().toString();
        String passwordConfirm = field_password_confirm.getText().toString();

        if (!User.usernameIsValid(username)) {
            if (username.isEmpty()) { field_username.setError("Username is required!"); }
            else { field_username.setError("Username is invalid. " + User.ILLEGAL_USERNAME_CHARS_MSG); }
            field_username.requestFocus();
            field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!User.nameIsValid(firstName)) {
            if (username.isEmpty()) { field_first_name.setError("First name is required!"); }
            else { field_first_name.setError("First name is invalid. "); }
            field_first_name.requestFocus();
            field_first_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!User.nameIsValid(lastName)) {
            if (username.isEmpty()) { field_last_name.setError("Last name is required!"); }
            else { field_last_name.setError("Last name is invalid. "); }
            field_last_name.requestFocus();
            field_last_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!User.emailIsValid(email)) {
            if (username.isEmpty()) { field_email.setError("Email is required!"); }
            else { field_email.setError("This is an invalid E-mail!"); }
            field_email.requestFocus();
            field_email.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        Boolean passwordError = true;
        switch (User.validatePassword(username, password, passwordConfirm)) {
            case VALID: passwordError = false; break;
            case EMPTY: field_password.setError("Password is required!"); break;
            case TOO_SHORT: field_password.setError("Minimum length of password is " + User.PASSWORD_MIN_LENGTH + " characters."); break;
            case CONFIRM_MISMATCH:
                field_password_confirm.setError("Both passwords must match.");
                field_password_confirm.requestFocus();
                field_password_confirm.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
                return;
            case ILLEGAL_PASSWORD: field_password.setError("The selected password is banned. Please select a new password."); break;
            case CONTAINS_USERNAME: field_password.setError("The password cannot contain the username."); break;
            default: field_password.setError("Invalid password.");
        }
        if (passwordError) {
            field_password.requestFocus();
            field_password.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());

        final User newUser;
        try {
            newUser = new User(firstName, lastName, username, email, User.parseType(radioButton.getText().toString()), password);
        } catch (InvalidDataException e) {
            Toast.makeText(getApplicationContext(), "Unable to create the account. An invalid input has been detected: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        final Button btn_create_account = findViewById(R.id.btn_create_account);
        btn_create_account.setEnabled(false);

        newUser.create(new AsyncActionEventListener() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The user account '" + username + "' has been successfully created.  Please sign in to continue.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("username", username);
                setResult(RESULT_OK, intent);
                finish();
            }
            public void onFailure(AsyncEventFailureReason reason) {
                switch (reason) {
                    case DATABASE_ERROR:
                        Toast.makeText(getApplicationContext(), "Unable to create your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                        break;
                    case ALREADY_EXISTS:
                        field_username.setError("Username is already in use!");
                        field_username.requestFocus();
                        break;
                    default:
                        // Some other kind of error
                        Toast.makeText(getApplicationContext(), "Unable to create your account at this time. Please try again later.", Toast.LENGTH_LONG).show();
                }
                btn_create_account.setEnabled(true);
            }
        });

    }
}
