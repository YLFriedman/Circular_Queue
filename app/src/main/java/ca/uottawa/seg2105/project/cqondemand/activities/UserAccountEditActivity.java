package ca.uottawa.seg2105.project.cqondemand.activities;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountEditActivity extends SignedInActivity {

    protected EditText field_username;
    protected EditText field_first_name;
    protected EditText field_last_name;
    protected EditText field_email;
    protected User currentUser;

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
        State.getState().setCurrentUser(null);
        if (null != currentUser) {
            field_username.setText(currentUser.getUsername(), TextView.BufferType.EDITABLE);
            field_first_name.setText(currentUser.getFirstName(), TextView.BufferType.EDITABLE);
            field_last_name.setText(currentUser.getLastName(), TextView.BufferType.EDITABLE);
            field_email.setText(currentUser.getEmail(), TextView.BufferType.EDITABLE);
        } else {
            Toast.makeText(getApplicationContext(),  String.format(getString(R.string.current_item_provided_template), getString(R.string.account)), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onSaveUserClick(View view) {
        final String firstName = field_first_name.getText().toString().trim();
        final String lastName = field_last_name.getText().toString().trim();
        final String username = field_username.getText().toString().trim();
        final String email = field_email.getText().toString().trim();

        if (!FieldValidation.usernameIsValid(username)) {
            if (username.isEmpty()) { field_username.setError(getString(R.string.empty_username_error)); }
            else { field_username.setError(getString(R.string.invalid_username_msg)); }
            field_username.requestFocus();
            field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.personNameIsValid(firstName)) {
            if (username.isEmpty()) { field_first_name.setError(getString(R.string.empty_first_name_error)); }
            else { field_first_name.setError(getString(R.string.invalid_first_name_error)); }
            field_first_name.requestFocus();
            field_first_name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        if (!FieldValidation.personNameIsValid(lastName)) {
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

        if (firstName.equals(currentUser.getFirstName()) && lastName.equals(currentUser.getLastName())
                && username.equals(currentUser.getUsername()) && email.equals(currentUser.getEmail())) {
            Toast.makeText(getApplicationContext(), String.format(getString(R.string.no_changes_made_error_tempalte), getString(R.string.service).toLowerCase()), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final User updatedUser;
        final Button btn_save_user = findViewById(R.id.btn_save_user);
        btn_save_user.setEnabled(false);
        updatedUser = new User(currentUser.getKey(), firstName, lastName, username, email, currentUser.getType(), currentUser.getPassword());

        DbUser.updateUser(updatedUser, new AsyncActionEventListener() {
            public void onSuccess() {
                State.getState().setCurrentUser(updatedUser);
                Toast.makeText(getApplicationContext(), R.string.account_update_success, Toast.LENGTH_LONG).show();
                finish();
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
