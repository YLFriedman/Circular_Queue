package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class UserAccountViewActivity extends SignedInActivity {

    private TextView txt_account_type;
    private TextView txt_username;
    private TextView txt_full_name;
    private TextView txt_email;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_view);
        // Set references to the UI objects
        txt_account_type = findViewById(R.id.txt_account_type);
        txt_username = findViewById(R.id.txt_username);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_email = findViewById(R.id.txt_email);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFinishing()) { return; }
        if (null != username) { // If a username was passed through the intent
            // Clear the text fields
            State.getState().setCurrentUser(null);
            setupFields();
            // Try to get the user object
            User.getUser(username, new AsyncSingleValueEventListener<User>() {
                @Override
                public void onSuccess(User user) {
                    State.getState().setCurrentUser(user);
                    setupFields();
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve the user '" + username + "' from the database.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else { // If no username was passed through the intent, then load the logged-in user
            State.getState().setCurrentUser(State.getState().getSignedInUser());
            setupFields();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = State.getState().getSignedInUser();
        getMenuInflater().inflate(R.menu.user_options, menu);
        if (null != user && user.getType() == User.Types.ADMIN) {
            menu.setGroupVisible(R.id.grp_user_edit_controls, false);
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

    private void setupFields() {
        User currentUser = State.getState().getCurrentUser();
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

    public void onEditAccountClick() {
        startActivity(new Intent(getApplicationContext(), UserAccountEditActivity.class));
    }

    public void onChangePasswordClick() {
        startActivity(new Intent(getApplicationContext(), UserAccountChangePasswordActivity.class));
    }

    public void onDeleteAccountClick() {
        final User currentUser = State.getState().getCurrentUser();
        if (null != currentUser) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete the '" + currentUser.getUsername() + "' account?  \r\nThis CANNOT be undone!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            currentUser.delete(new AsyncActionEventListener() {
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), "The user account '" + currentUser.getUsername() + "' has been successfully deleted.", Toast.LENGTH_LONG).show();
                                    if (null != State.getState().getSignedInUser() && State.getState().getSignedInUser().equals(currentUser)) {
                                        State.getState().setSignedInUser(null);
                                    }
                                    State.getState().setCurrentUser(null);
                                    finish();
                                }
                                public void onFailure(AsyncEventFailureReason reason) {
                                    Toast.makeText(getApplicationContext(), "Unable to delete your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }
    }
}
