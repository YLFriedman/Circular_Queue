package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserHome extends AppCompatActivity {

    private User currentUser;

    /*
     * Fills in layout for UserHome activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        /*
        *hides details by default
         */
        LinearLayout details_view = (LinearLayout) findViewById(R.id.layout_details);
        details_view.setVisibility(View.INVISIBLE);

        currentUser = DatabaseUtil.getCurrentUser();
        if (null == currentUser) {
            Intent intent = new Intent(this, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            TextView txtWelcome = findViewById(R.id.txt_welcome);
            txtWelcome.setText("Hello " + currentUser.getFirstName() + " " + currentUser.getLastName());
            TextView txtRole = findViewById(R.id.txt_role);
            if (currentUser.getType() == User.Types.ADMIN) {
                txtRole.setText("You are logged in as an " + currentUser.getType().toString());
            } else {
                txtRole.setText("You are logged in as a " + currentUser.getType().toString());
            }
        }

    }

    /*
     * Navigates to AccountDetails when clicked
     * Displays Account Details
     */
    public void onAccountDetailsClick(View view) {
        currentUser = DatabaseUtil.getCurrentUser();
        if (null == currentUser) {
            Intent intent = new Intent(this, UserAccount.class);
            startActivity(intent);
        } else {
            /*
            *Hides original layout, makes account details visible
             */
            LinearLayout main_view = (LinearLayout) findViewById(R.id.layout_main);
            main_view.setVisibility(View.INVISIBLE);
            LinearLayout details_view = (LinearLayout) findViewById(R.id.layout_details);
            details_view.setVisibility(View.VISIBLE);

            TextView txtFirstName = findViewById(R.id.textView_firstName);
            txtFirstName.setText("First Name: " + currentUser.getFirstName());
            TextView txtLastName = findViewById(R.id.textView_lastName);
            txtLastName.setText("Last Name: " + currentUser.getLastName());
            TextView txtUserName = findViewById(R.id.textView_userName);
            txtUserName.setText("User Name: " + currentUser.getUserName());
            TextView txtAccountType = findViewById(R.id.textView_account);
            txtAccountType.setText("Account type: " + currentUser.getType().toString());
            TextView txtEmail = findViewById(R.id.textView_email);
            txtEmail.setText("Current Email: " + currentUser.getEmail());

        }
    }
}

