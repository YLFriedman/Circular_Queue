package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserAccountChangePassword extends AppCompatActivity {

    private EditText field_password_old;
    private EditText field_password;
    private EditText field_password_confirm;
    private Button btn_save_password;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_change_password);
        currentUser = DatabaseUtil.getCurrentUser();
        if (null == currentUser) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Set references to the Edit User UI objects
            field_password_old = findViewById(R.id.field_password_old);
            field_password = findViewById(R.id.field_password);
            field_password_confirm = findViewById(R.id.field_password_confirm);
            // Set references to the Button User UI objects
            btn_save_password = findViewById(R.id.btn_save_password);
        }
    }

    public void onSavePasswordClick(View view){
        final String oldPassword = field_password_old.getText().toString();
        final String password = field_password.getText().toString();
        final String password_confirm = field_password_confirm.getText().toString();
        if (!oldPassword.equals(currentUser.getPassword())) {
            field_password_old.setError("Incorrect password.");
            field_password_old.requestFocus();
        } else {
            switch (User.validatePassword(currentUser.getUserName(), password, password_confirm)) {
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
                    field_password.setError("Your password cannot contain your username.");
                    field_password.requestFocus();
                    return;
                default:
                    field_password.setError("Invalid password.");
                    field_password.requestFocus();
                    return;
            }
            if (currentUser.getPassword().equals(password)) {
                field_password.setError("The new password must be different from the existing password!");
                field_password.requestFocus();
                return;
            }
            final User updatedUser = new User(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getUserName(), currentUser.getEmail(), currentUser.getType(), password);
            btn_save_password.setEnabled(false);
            DatabaseUtil.updateUserPassword(updatedUser, new UserEventListener() {
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Password updated successfully!", Toast.LENGTH_LONG).show();
                    btn_save_password.setEnabled(true);
                    finish();
                }
                public void onFailure(DatabaseUtil.CallbackFailure reason) {
                    btn_save_password.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Unable to update your password at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
