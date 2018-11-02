package ca.uottawa.seg2105.project.cqondemand;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserAccountViewActivity extends AppCompatActivity {

    private TextView txt_account_type;
    private TextView txt_username;
    private TextView txt_first_name;
    private TextView txt_last_name;
    private TextView txt_email;
    private Button btn_edit_user;
    private Button btn_change_password;
    private Button btn_delete_user;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_view);
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            finish();
        } else {
            // Set references to the View User UI objects
            txt_account_type = findViewById(R.id.txt_account_type);
            txt_username = findViewById(R.id.txt_username);
            txt_first_name = findViewById(R.id.txt_first_name);
            txt_last_name = findViewById(R.id.txt_last_name);
            txt_email = findViewById(R.id.txt_email);
            // Set references to the Button User UI objects
            btn_edit_user = findViewById(R.id.btn_edit_user);
            btn_change_password = findViewById(R.id.btn_change_password);
            btn_delete_user = findViewById(R.id.btn_delete_user);
            if (currentUser.getType() == User.Types.ADMIN) {
                btn_edit_user.setVisibility(View.GONE);
                btn_change_password.setVisibility(View.GONE);
                //btn_delete_user.setVisibility(View.GONE);
            }
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
            DbUtil.getUser(username, new DbValueEventListener<User>() {
                @Override
                public void onSuccess(ArrayList<User> data) {
                    currentUser = data.get(0);
                    setUserViewValues();
                }
                @Override
                public void onFailure(DbEventFailureReason reason) {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve the user '" + username + "' from the database.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } else {
            currentUser = State.getState().getCurrentUser();
            if (null == currentUser) {
                finish();
            } else {
                setUserViewValues();
            }
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

    public void onEditUserClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserAccountEditActivity.class));
    }

    public void onChangePasswordClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserAccountChangePasswordActivity.class));
    }

    public void onDeleteAccountClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete the '" + currentUser.getUserName() + "' account?  \r\nThis CANNOT be undone!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DbUtil.deleteUser(currentUser.getUserName(), new DbActionEventListener(){
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "The user account '" + currentUser.getUserName() + "' has been successfully deleted.", Toast.LENGTH_LONG).show();
                                if (null != State.getState().getCurrentUser() && State.getState().getCurrentUser().equals(currentUser)) { State.getState().setCurrentUser(null); }
                                currentUser = null;
                                finish();
                            }
                            public void onFailure(DbEventFailureReason reason) {
                                Toast.makeText(getApplicationContext(), "Unable to delete your account at this time due to a database error. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }})
                .setNegativeButton(R.string.cancel, null).show();
    }
}
