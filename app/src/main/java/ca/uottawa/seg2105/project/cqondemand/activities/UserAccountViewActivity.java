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

public class UserAccountViewActivity extends AppCompatActivity {

    private TextView txt_account_type;
    private TextView txt_username;
    private TextView txt_first_name;
    private TextView txt_last_name;
    private TextView txt_email;
    private User currentUser;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_view);
        currentUser = State.getState().getSignedInUser();
        if (null == currentUser) {
            finish();
        } else {
            // Set references to the View User UI objects
            txt_account_type = findViewById(R.id.txt_account_type);
            txt_username = findViewById(R.id.txt_username);
            txt_first_name = findViewById(R.id.txt_first_name);
            txt_last_name = findViewById(R.id.txt_last_name);
            txt_email = findViewById(R.id.txt_email);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        if (null != username) {
            currentUser = null;
            setUserViewValues();
            User.getUser(username, new AsyncSingleValueEventListener<User>() {
                @Override
                public void onSuccess(User user) {
                    currentUser = user;
                    setUserViewValues();
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve the user '" + username + "' from the database.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else {
            currentUser = State.getState().getSignedInUser();
            if (null == currentUser) {
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                setUserViewValues();
            }
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

    public void onEditAccountClick() {
        startActivity(new Intent(getApplicationContext(), UserAccountEditActivity.class));
    }

    public void onChangePasswordClick() {
        startActivity(new Intent(getApplicationContext(), UserAccountChangePasswordActivity.class));
    }

    public void onDeleteAccountClick() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete the '" + currentUser.getUserName() + "' account?  \r\nThis CANNOT be undone!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        currentUser.delete(new AsyncActionEventListener(){
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "The user account '" + currentUser.getUserName() + "' has been successfully deleted.", Toast.LENGTH_LONG).show();
                                if (null != State.getState().getSignedInUser() && State.getState().getSignedInUser().equals(currentUser)) { State.getState().setSignedInUser(null); }
                                currentUser = null;
                                finish();
                            }
                            public void onFailure(AsyncEventFailureReason reason) {
                                Toast.makeText(getApplicationContext(), "Unable to delete your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }})
                .setNegativeButton(R.string.cancel, null).show();
    }
}
