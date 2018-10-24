package ca.uottawa.seg2105.project.cqondemand;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserAccount extends AppCompatActivity {

    private static enum Views { USER_VIEW, USER_EDIT, CHANGE_PASSWORD }

    private TextView txt_account_type;
    private TextView txt_username;
    private TextView txt_first_name;
    private TextView txt_last_name;
    private TextView txt_email;

    private EditText field_username;
    private EditText field_first_name;
    private EditText field_last_name;
    private EditText field_email;

    private EditText field_password_old;
    private EditText field_password;
    private EditText field_password_confirm;

    private Button btn_edit_user;
    private Button btn_change_password;
    private Button btn_delete_user;
    private Button btn_save_user;
    private Button btn_save_password;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        currentUser = DatabaseUtil.getCurrentUser();
        if (null == currentUser) {
            finish();
        } else {
            // Set references to the View User UI objects
            txt_account_type = findViewById(R.id.txt_account_type);
            txt_username = findViewById(R.id.txt_username);
            txt_first_name = findViewById(R.id.txt_first_name);
            txt_last_name = findViewById(R.id.txt_last_name);
            txt_email = findViewById(R.id.txt_email);

            // Set references to the Edit User UI objects
            field_username = findViewById(R.id.field_username);
            field_first_name = findViewById(R.id.field_first_name);
            field_last_name = findViewById(R.id.field_last_name);
            field_email = findViewById(R.id.field_email);
            field_password_old = findViewById(R.id.field_password_old);
            field_password = findViewById(R.id.field_password);
            field_password_confirm = findViewById(R.id.field_password_confirm);

            // Set references to the Cutton User UI objects
            btn_edit_user = findViewById(R.id.btn_edit_user);
            btn_change_password = findViewById(R.id.btn_change_password);
            btn_delete_user = findViewById(R.id.btn_delete_user);
            btn_save_user = findViewById(R.id.btn_save_user);
            btn_save_password = findViewById(R.id.btn_save_password);

            if (currentUser.getType() == User.Types.ADMIN) {
                btn_edit_user.setVisibility(View.GONE);
                btn_change_password.setVisibility(View.GONE);
                //btn_delete_user.setVisibility(View.GONE);
            }
            // Set values for the initial landing layout
            setUserViewValues();
        }

    }

    private void setUserViewValues() {
        if (null == currentUser) {
            txt_account_type.setText("");
            txt_username.setText("");
            txt_first_name.setText("");
            txt_last_name.setText("");
            txt_email.setText("");
        } else {
            txt_account_type.setText(currentUser.getType().toString());
            txt_username.setText(currentUser.getUserName());
            txt_first_name.setText(currentUser.getFirstName());
            txt_last_name.setText(currentUser.getLastName());
            txt_email.setText(currentUser.getEmail());
        }
    }

    public void onChangePasswordClick(View view) {
        field_password_old.getText().clear();
        field_password.getText().clear();
       field_password_confirm.getText().clear();
        setView(Views.CHANGE_PASSWORD);
    }

    public void onDeleteAccountClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete the '" + currentUser.getUserName() + "' account?  \r\nThis CANNOT be undone!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseUtil.deleteUser(currentUser.getUserName(), new UserEventListener(){
                            public void onSuccess() {
                                Toast.makeText(UserAccount.this, "The user account '" + UserAccount.this.currentUser.getUserName() + "' has been successfully deleted.", Toast.LENGTH_LONG).show();
                                DatabaseUtil.setCurrentUser(null);
                                currentUser = null;
                                finish();
                            }
                            public void onFailure(DatabaseUtil.CallbackFailure reason) {
                                Toast.makeText(UserAccount.this, "Unable to delete your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }})
                .setNegativeButton(R.string.cancel, null).show();
    }

    public void onEditUserClick(View view) {
        field_username.setError(null);
        field_first_name.setError(null);
        field_last_name.setError(null);
        field_email.setError(null);
        if (null == currentUser) {
            field_username.setText("", TextView.BufferType.EDITABLE);
            field_first_name.setText("", TextView.BufferType.EDITABLE);
            field_last_name.setText("", TextView.BufferType.EDITABLE);
            field_email.setText("", TextView.BufferType.EDITABLE);
        } else {
            field_username.setText(currentUser.getUserName(), TextView.BufferType.EDITABLE);
            field_first_name.setText(currentUser.getFirstName(), TextView.BufferType.EDITABLE);
            field_last_name.setText(currentUser.getLastName(), TextView.BufferType.EDITABLE);
            field_email.setText(currentUser.getEmail(), TextView.BufferType.EDITABLE);
        }
        setView(Views.USER_EDIT);
    }

    public void onSaveUserClick(View view) {
        final String first_name = field_first_name.getText().toString();
        final String last_name = field_last_name.getText().toString();
        final String username = field_username.getText().toString();
        final String email = field_email.getText().toString();
        final User updatedUser = new User(first_name, last_name, username, email, currentUser.getType(), currentUser.getPassword());

        if (first_name.equals(currentUser.getFirstName()) && last_name.equals(currentUser.getLastName())
                && username.equals(currentUser.getUserName()) && email.equals(currentUser.getEmail())) {
            Toast.makeText(this, "No changes were made to the account details.", Toast.LENGTH_SHORT).show();
            setView(Views.USER_VIEW);
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
            DatabaseUtil.updateUser(currentUser.getUserName(), updatedUser, new UserEventListener() {
                public void onSuccess() {
                    Toast.makeText(UserAccount.this, "Account updated successfully!", Toast.LENGTH_LONG).show();
                    currentUser = DatabaseUtil.getCurrentUser();
                    setUserViewValues();
                    setView(Views.USER_VIEW);
                    btn_save_user.setEnabled(true);
                }
                public void onFailure(DatabaseUtil.CallbackFailure reason) {
                    switch (reason) {
                        case ALREADY_EXISTS:
                            btn_save_user.setEnabled(true);
                            field_username.setError("Username is already in use!");
                            field_username.requestFocus();
                            break;
                        case DATABASE_ERROR:
                            Toast.makeText(UserAccount.this, "Unable to update your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            // Some other kind of error
                            btn_save_user.setEnabled(true);
                            Toast.makeText(UserAccount.this, "Unable to update your account at this time. Please try again later.", Toast.LENGTH_LONG).show();
                    }
                    btn_save_user.setEnabled(true);
                }
            });
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
                    Toast.makeText(UserAccount.this, "Password updated successfully!", Toast.LENGTH_LONG).show();
                    setUserViewValues();
                    setView(Views.USER_VIEW);
                    btn_save_password.setEnabled(true);
                }
                public void onFailure(DatabaseUtil.CallbackFailure reason) {
                    btn_save_password.setEnabled(true);
                    Toast.makeText(UserAccount.this, "Unable to update your password at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        LinearLayout layout_user_view = findViewById(R.id.layout_user_view);
        if (layout_user_view.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            setView(Views.USER_VIEW);
        }
    }

    private void setView(Views view) {
        LinearLayout layout_user_view = findViewById(R.id.layout_user_view);
        LinearLayout layout_user_edit = findViewById(R.id.layout_user_edit);
        LinearLayout layout_change_password = findViewById(R.id.layout_change_password);

        layout_user_view.setVisibility(View.GONE);
        layout_user_edit.setVisibility(View.GONE);
        layout_change_password.setVisibility(View.GONE);

        switch (view) {
            case USER_EDIT: layout_user_edit.setVisibility(View.VISIBLE); return;
            case CHANGE_PASSWORD: layout_change_password.setVisibility(View.VISIBLE); return;
            default: layout_user_view.setVisibility(View.VISIBLE);
        }
    }

}
