package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountChangePasswordActivity extends SignedInActivity {

    private EditText field_password_old;
    private EditText field_password;
    private EditText field_password_confirm;
    private Button btn_save_password;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_change_password);
        // Set references to the  UI objects
        field_password_old = findViewById(R.id.field_password_old);
        field_password = findViewById(R.id.field_password);
        field_password_confirm = findViewById(R.id.field_password_confirm);
        btn_save_password = findViewById(R.id.btn_save_password);

        currentUser = State.getState().getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (!currentUser.equals(State.getState().getCurrentUser())) {
            finish();
        }
    }

    public void onSavePasswordClick(View view){
        final String oldPassword = field_password_old.getText().toString();
        final String password = field_password.getText().toString();
        final String passwordConfirm = field_password_confirm.getText().toString();

        if (!oldPassword.equals(currentUser.getPassword())) {
            field_password_old.setError("Incorrect password.");
            field_password_old.requestFocus();
            field_password_old.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        Boolean passwordError = true;
        switch (FieldValidation.validatePassword(currentUser.getUsername(), password, passwordConfirm)) {
            case VALID: passwordError = false; break;
            case EMPTY: field_password.setError("Password is required!"); break;
            case TOO_SHORT: field_password.setError("Minimum length of password is " + FieldValidation.PASSWORD_MIN_LENGTH + " characters."); break;
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

        btn_save_password.setEnabled(false);
        DbUser.updatePassword(currentUser, password, new AsyncActionEventListener() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Password updated successfully!", Toast.LENGTH_LONG).show();
                btn_save_password.setEnabled(true);
                finish();
            }
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                btn_save_password.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Unable to update your password at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
