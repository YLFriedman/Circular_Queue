package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private User currentUser;
    private LinearLayout userListContainer;

    /*
     * Fills in layout for UserHome activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userListContainer = findViewById(R.id.userListContainer);
        currentUser = DbUtil.getCurrentUser();

        if (null == currentUser) {
            onSignOutClick(null);
        } else {
            userListContainer.setVisibility(View.GONE);
            if (currentUser.getType() == User.Types.ADMIN) {
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                DbUtil.getUsers(new DbValueEventListener<User>() {
                    @Override
                    public void onSuccess(ArrayList<User> data) {
                        if (null != data && data.size() > 0) {
                            adapter = new UserAdapter(getApplicationContext(), data);
                            recyclerView.setAdapter(adapter);
                            userListContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(DbEventFailureReason reason) {

                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = DbUtil.getCurrentUser();
        if (null == currentUser) {
            onSignOutClick(null);
        } else {
            setWelcomeText();
        }
    }

    private void setWelcomeText() {
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
    }

    /*
     * Navigates to AccountDetails when clicked
     * Displays Account Details
     */
    public void onAccountDetailsClick(View view) {
        if (null == DbUtil.getCurrentUser()) {
            onSignOutClick(null);
        } else {
            startActivity(new Intent(getApplicationContext(), UserAccountViewActivity.class));
        }
    }

    public void onSignOutClick(View view) {
        DbUtil.setCurrentUser(null);
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

}

