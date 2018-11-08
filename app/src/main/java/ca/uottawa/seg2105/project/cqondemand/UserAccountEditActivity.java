package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserAccountEditActivity extends AppCompatActivity {

    private EditText field_username;
    private EditText field_first_name;
    private EditText field_last_name;
    private EditText field_email;
    private Button btn_save_user;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_edit);
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Set references to the Edit User UI objects
            field_username = findViewById(R.id.field_username);
            field_first_name = findViewById(R.id.field_first_name);
            field_last_name = findViewById(R.id.field_last_name);
            field_email = findViewById(R.id.field_email);
            // Populate the Edit User UI objects
            field_username.setText(currentUser.getUserName(), TextView.BufferType.EDITABLE);
            field_first_name.setText(currentUser.getFirstName(), TextView.BufferType.EDITABLE);
            field_last_name.setText(currentUser.getLastName(), TextView.BufferType.EDITABLE);
            field_email.setText(currentUser.getEmail(), TextView.BufferType.EDITABLE);
            // Set references to the Button User UI objects
            btn_save_user = findViewById(R.id.btn_save_user);
        }
    }

    public void onSaveUserClick(View view) {
        final String first_name = field_first_name.getText().toString();
        final String last_name = field_last_name.getText().toString();
        final String username = field_username.getText().toString();
        final String email = field_email.getText().toString();
        final User updatedUser = new User(first_name, last_name, username, email, currentUser.getType(), currentUser.getPassword());

        if (first_name.equals(currentUser.getFirstName()) && last_name.equals(currentUser.getLastName())
                && username.equals(currentUser.getUserName()) && email.equals(currentUser.getEmail())) {
            Toast.makeText(getApplicationContext(), "No changes were made to the account details.", Toast.LENGTH_SHORT).show();
        } else {
            if (username.isEmpty()) {
                field_username.setError("Username is required!");
                field_username.requestFocus();
                return;
            } else if (!User.userNameIsValid(username)) {
                field_username.setError("Username is invalid. " + User.ILLEGAL_USERNAME_CHARS_MSG);
                field_username.requestFocus();
                return;
            }
            if (first_name.isEmpty()) {
                field_first_name.setError("First name is required!");
                field_first_name.requestFocus();
                return;
            }
            if (last_name.isEmpty()) {
                field_last_name.setError("Last name is required!");
                field_last_name.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                field_email.setError("Email is required!");
                field_email.requestFocus();
                return;
            } else if (!User.emailIsValid(email)) {
                field_email.setError("This is an invalid E-mail!");
                field_email.requestFocus();
                return;
            }
            btn_save_user.setEnabled(false);

            /*updatedUser.update(new AsyncActionEventListener() {
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Account updated successfully!", Toast.LENGTH_LONG).show();
                    currentUser = State.getState().getCurrentUser();
                    btn_save_user.setEnabled(true);
                    finish();
                }
                public void onFailure(AsyncEventFailureReason reason) {
                    switch (reason) {
                        case ALREADY_EXISTS:
                            btn_save_user.setEnabled(true);
                            field_username.setError("Username is already in use!");
                            field_username.requestFocus();
                            break;
                        case DATABASE_ERROR:
                            Toast.makeText(getApplicationContext(), "Unable to update your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            // Some other kind of error
                            btn_save_user.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Unable to update your account at this time. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                    btn_save_user.setEnabled(true);
                }
            });*/
        }
    }
}
