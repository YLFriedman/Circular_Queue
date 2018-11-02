package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    private User currentUser;

    /*
     * Fills in layout for UserHome activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            onSignOutClick(null);
        } else {
            //setWelcomeText();
        }
    }

    /*private void setWelcomeText() {
        TextView txtWelcome = findViewById(R.id.txt_welcome);
        String welcome = String.format(getString(R.string.welcome_template), currentUser.getFirstName(), currentUser.getLastName());
        txtWelcome.setText(welcome);
        TextView txtRole = findViewById(R.id.txt_role);
        String loggedInAs = "";
        if (currentUser.getType() == User.Types.ADMIN) {
            loggedInAs = String.format(getString(R.string.logged_in_as_admin_template), currentUser.getType().toString());
        } else {
            loggedInAs = String.format(getString(R.string.logged_in_as_template), currentUser.getType().toString());
        }
        txtRole.setText(loggedInAs);
    }*/

    public void onServicesClick(View view) {
        //startActivity(new Intent(getApplicationContext(), UserAccountListActivity.class));
    }

    public void onUserListClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserAccountListActivity.class));
    }

    /*
     * Navigates to AccountDetails when clicked
     * Displays Account Details
     */
    public void onMyAccountClick(View view) {
        startActivity(new Intent(getApplicationContext(), UserAccountViewActivity.class));
    }

    public void onSignOutClick(View view) {
        State.getState().setCurrentUser(null);
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

}

