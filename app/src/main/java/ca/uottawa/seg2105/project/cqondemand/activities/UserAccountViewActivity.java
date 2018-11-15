package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountViewActivity extends SignedInActivity {

    protected TextView txt_account_type;
    protected TextView txt_username;
    protected TextView txt_full_name;
    protected TextView txt_email;
    protected User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_view);
        // Set references to the UI objects
        txt_account_type = findViewById(R.id.txt_account_type);
        txt_username = findViewById(R.id.txt_username);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_email = findViewById(R.id.txt_email);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (null != State.getState().getCurrentUser()) {
            currentUser = State.getState().getCurrentUser();
            State.getState().setCurrentUser(null);
            setupFields();
        } else if (null != currentUser) {
            setupFields();
        } else {
            currentUser = State.getState().getSignedInUser();
            setupFields();
        }
    }

    private void setupFields() {
        if (null == currentUser) {
            txt_account_type.setText("");
            txt_username.setText("");
            txt_full_name.setText("");
            txt_email.setText("");
        } else {
            txt_account_type.setText(currentUser.getType().toString());
            txt_username.setText(currentUser.getUsername());
            txt_full_name.setText(String.format(getString(R.string.full_name_template), currentUser.getFirstName(), currentUser.getLastName()));
            txt_email.setText(currentUser.getEmail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        getMenuInflater().inflate(R.menu.user_options, menu);
        if (null != user && user.getType() == User.Types.ADMIN) {
            menu.setGroupVisible(R.id.grp_user_edit_controls, false);
        }
        if (!State.getState().getSignedInUser().equals(currentUser)) {
            menu.setGroupVisible(R.id.grp_user_password_controls, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_user_edit: onEditAccountClick(); return true;
            case R.id.menu_item_user_change_password: onChangePasswordClick(); return true;
            case R.id.menu_item_user_delete: onDeleteAccountClick(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEditAccountClick() {
        State.getState().setCurrentUser(currentUser);
        startActivity(new Intent(getApplicationContext(), UserAccountEditActivity.class));
    }

    public void onChangePasswordClick() {
        State.getState().setCurrentUser(currentUser);
        startActivity(new Intent(getApplicationContext(), UserAccountChangePasswordActivity.class));
    }

    public void onDeleteAccountClick() {
        if (null != currentUser) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_account)
                    .setMessage(String.format(getString(R.string.delete_confirm_dialog_template), currentUser.getUsername(), getString(R.string.account).toLowerCase()))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DbUser.deleteUser(currentUser, new AsyncActionEventListener() {
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.account_delete_success), currentUser.getUsername()), Toast.LENGTH_LONG).show();
                                    if (currentUser.equals(State.getState().getSignedInUser())) { State.getState().setSignedInUser(null); }
                                    finish();
                                }
                                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), R.string.account_delete_db_error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }
    }

}
