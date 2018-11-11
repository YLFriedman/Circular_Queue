package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountEditActivity extends SignedInActivity {

    private EditText field_username;
    private EditText field_first_name;
    private EditText field_last_name;
    private EditText field_email;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_edit);
        // Set references to the  UI objects
        field_username = findViewById(R.id.field_username);
        field_first_name = findViewById(R.id.field_first_name);
        field_last_name = findViewById(R.id.field_last_name);
        field_email = findViewById(R.id.field_email);

        currentUser = State.getState().getCurrentUser();
        if (null != currentUser) {
            field_username.setText(currentUser.getUsername(), TextView.BufferType.EDITABLE);
            field_first_name.setText(currentUser.getFirstName(), TextView.BufferType.EDITABLE);
            field_last_name.setText(currentUser.getLastName(), TextView.BufferType.EDITABLE);
            field_email.setText(currentUser.getEmail(), TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (!currentUser.equals(State.getState().getCurrentUser())) {
            finish();
        }
    }

    public void onSaveUserClick(View view) {
        final String firstName = field_first_name.getText().toString().trim();
        final String lastName = field_last_name.getText().toString().trim();
        final String username = field_username.getText().toString().trim();
        final String email = field_email.getText().toString().trim();

        if (firstName.equals(currentUser.getFirstName()) && lastName.equals(currentUser.getLastName())
                && username.equals(currentUser.getUsername()) && email.equals(currentUser.getEmail())) {
            Toast.makeText(getApplicationContext(), "No changes were made to the account details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        final User updatedUser;
        final Button btn_save_user = findViewById(R.id.btn_save_user);
        btn_save_user.setEnabled(false);
        try {
            updatedUser = new User(firstName, lastName, username, email, currentUser.getType(), currentUser.getPassword());
        } catch (InvalidDataException e) {
            Toast.makeText(getApplicationContext(), "Unable to update your account. An invalid input has been detected: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        currentUser.update(updatedUser, new AsyncActionEventListener() {
            public void onSuccess() {
                currentUser = State.getState().getSignedInUser();
                Toast.makeText(getApplicationContext(), "Account updated successfully!", Toast.LENGTH_LONG).show();
                finish();
            }
            public void onFailure(AsyncEventFailureReason reason) {
                switch (reason) {
                    case ALREADY_EXISTS:
                        btn_save_user.setEnabled(true);
                        field_username.setError("Username is already in use!");
                        field_username.requestFocus();
                        field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
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
        });

    }
}
