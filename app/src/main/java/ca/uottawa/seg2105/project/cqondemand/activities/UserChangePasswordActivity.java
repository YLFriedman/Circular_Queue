package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.Authentication;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * The class <b>BookingViewActivity</b> is a UI class that allows a user to change their password.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class UserChangePasswordActivity extends SignedInActivity {

    /**
     * A field used to enter the old password
     */
    private EditText field_password_old;

    /**
     * A field used to enter the new password
     */
    private EditText field_password;

    /**
     * A field used to enter the confirmation password
     */
    private EditText field_password_confirm;

    /**
     * A button used to trigger the save action
     */
    private Button btn_save_password;

    /**
     * The user who's password is being updated
     */
    private User currentUser;

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_password);
        // Set references to the  UI objects
        field_password_old = findViewById(R.id.field_password_old);
        field_password = findViewById(R.id.field_password);
        field_password_confirm = findViewById(R.id.field_password_confirm);
        btn_save_password = findViewById(R.id.btn_save_password);
        // Get the user object from the intent
        Intent intent = getIntent();
        try {
            currentUser = (User) intent.getSerializableExtra("user");
        } catch (ClassCastException e) {
            Toast.makeText(getApplicationContext(), R.string.invalid_intent_object, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (null == currentUser) {
            Toast.makeText(getApplicationContext(),  String.format(getString(R.string.current_item_provided_template), getString(R.string.account)), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * The on-click handler for the save password button
     * @param view the view object that was clicked
     */
    public void onSavePasswordClick(View view) {
        final String oldPassword = field_password_old.getText().toString();
        final String password = field_password.getText().toString();
        final String passwordConfirm = field_password_confirm.getText().toString();

        if (!Authentication.checkPassword(oldPassword, currentUser.getPassword())) {
            field_password_old.setError(getString(R.string.incorrect_password_error));
            field_password_old.requestFocus();
            field_password_old.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
            return;
        }

        Boolean passwordError = true;
        switch (FieldValidation.validatePassword(currentUser.getUsername(), password, passwordConfirm)) {
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

        btn_save_password.setEnabled(false);
        DbUser.updatePassword(currentUser, Authentication.genHash(password), new AsyncSingleValueEventListener<User>() {
            public void onSuccess(@NonNull User updatedUser) {
                Toast.makeText(getApplicationContext(), R.string.password_update_success, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), UserViewActivity.class);
                intent.putExtra("user", updatedUser);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                Toast.makeText(getApplicationContext(), R.string.password_update_db_error, Toast.LENGTH_LONG).show();
                btn_save_password.setEnabled(true);
            }
        });
    }

}
