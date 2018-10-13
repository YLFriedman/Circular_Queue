package ca.uottawa.seg2105.project.cqondemand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private DatabaseReference db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        currentUser = User.getCurrentUser();
        if (null == currentUser) {
            // TODO: Return to main activity
            currentUser = new User("First_test", "Last_test", "test_username", "test@uottawa.ca", User.Types.HOMEOWNER);

        //} else {

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

            // Set values for the initial landing layout
            setUserViewValues();
        }


    }

    private void setUserViewValues() {
        txt_account_type.setText(currentUser.getType().toString());
        txt_username.setText(currentUser.getUserName());
        txt_first_name.setText(currentUser.getFirstName());
        txt_last_name.setText(currentUser.getLastName());
        txt_email.setText(currentUser.getEmail());
    }

    public void onChangePasswordClick(View view){
        setView(Views.CHANGE_PASSWORD);
    }

    public void onDeleteAccountClick(View view){
        //TODO: implement a method which deletes a users account from the db. Presumably the app then navigates to the login screen

    }

    public void onEditUserClick(View view){
        field_username.setText(currentUser.getUserName(), TextView.BufferType.EDITABLE);
        field_first_name.setText(currentUser.getFirstName(), TextView.BufferType.EDITABLE);
        field_last_name.setText(currentUser.getLastName(), TextView.BufferType.EDITABLE);
        field_email.setText(currentUser.getEmail(), TextView.BufferType.EDITABLE);
        setView(Views.USER_EDIT);
    }

    public void onSaveUserClick(View view) {
        String first_name = field_first_name.getText().toString();
        String last_name = field_last_name.getText().toString();
        String username = field_username.getText().toString();
        String email = field_email.getText().toString();
        if (first_name.equals(currentUser.getFirstName()) && last_name.equals(currentUser.getLastName())
                && username.equals(currentUser.getUserName()) && email.equals(currentUser.getEmail())) {
            Toast.makeText(this, "No changes were made to the account details.", Toast.LENGTH_LONG).show();
            setView(Views.USER_VIEW);
        } else {
            boolean updateIsSuccessful = currentUser.update(first_name, last_name, username, email);
            if (updateIsSuccessful || true) {
                Toast.makeText(this, "Account updated successsfully!", Toast.LENGTH_LONG).show();
                setUserViewValues();
                setView(Views.USER_VIEW);
            } else {
                Toast.makeText(this, "Unable to update your account details at this time. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onSavePasswordClick(View view){
        //TODO: implement a method that saves the users choice in a db. Should validate the passwords as well.
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
