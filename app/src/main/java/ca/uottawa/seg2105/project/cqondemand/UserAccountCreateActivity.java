package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UserAccountCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
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

        //Check username validity
        final EditText field_username = findViewById(R.id.field_username);
        final String username = field_username.getText().toString().trim();

        if (username.isEmpty()) {
            field_username.setError("Username is required!");
            field_username.requestFocus();
            return;
        } else if (!User.userNameIsValid(username)) {
            field_username.setError("Username is invalid. " + User.ILLEGAL_USERNAME_CHARS_MSG);
            field_username.requestFocus();
            return;
        }

        //Check First Name validity
        EditText field_first_name = findViewById(R.id.field_first_name);
        String firstName = field_first_name.getText().toString().trim();

        if (firstName.isEmpty()) {
            field_first_name.setError("First name is required!");
            field_first_name.requestFocus();
            return;
        }

        //Check Last Name validity
        EditText field_last_name = findViewById(R.id.field_last_name);
        String lastName = field_last_name.getText().toString().trim();

        if (lastName.isEmpty()) {
            field_last_name.setError("Last name is required!");
            field_last_name.requestFocus();
            return;
        }

        //Check Email Validity
        EditText field_email = findViewById(R.id.field_email);
        String email = field_email.getText().toString();

        if (email.isEmpty()) {
            field_email.setError("Email is required!");
            field_email.requestFocus();
            return;
        } else if (!User.emailIsValid(email)) {
            field_email.setError("This is an invalid E-mail!");
            return;
        }

        //check password matching
        EditText field_password = findViewById(R.id.field_password);
        String password = field_password.getText().toString().trim();

        EditText field_password_confirm = findViewById(R.id.field_password_confirm);
        String passwordConfirm = field_password_confirm.getText().toString().trim();

        switch (User.validatePassword(username, password, passwordConfirm)) {
            case VALID: break;
            case EMPTY:
                field_password.setError("Password is required!");
                field_password.requestFocus();
                return;
            case TOO_SHORT:
                field_password.setError("Minimum length of password is " + User.PASSWORD_MIN_LENGTH + " characters.");
                field_password.requestFocus();
                return;
            case CONFIRM_MISMATCH:
                field_password_confirm.setError("Both passwords must match.");
                field_password_confirm.requestFocus();
                return;
            case ILLEGAL_PASSWORD:
                field_password.setError("The selected password is banned. Please select a new password.");
                field_password.requestFocus();
                return;
            case CONTAINS_USERNAME:
                field_password.setError("The password cannot contain the username.");
                field_password.requestFocus();
                return;
            default:
                field_password.setError("Invalid password.");
                field_password.requestFocus();
                return;
        }

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        final User newUser = new User(firstName, lastName, username, email, User.parseType(radioButton.getText().toString()), password);

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
