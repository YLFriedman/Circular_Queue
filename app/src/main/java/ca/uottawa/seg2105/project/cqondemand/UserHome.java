package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserHome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> userList;
    private User currentUser;
    private LinearLayout userListContainer;

    /*
     * Fills in layout for UserHome activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userListContainer = findViewById(R.id.userListContainer);
        currentUser = DatabaseUtil.getCurrentUser();
        userList = new ArrayList<User>();

        if (null == currentUser) {
            signOut();
        } else {
            userListContainer.setVisibility(View.GONE);
            if (currentUser.getType() == User.Types.ADMIN) {
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                DatabaseUtil.getUserList(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String firstName = (String) postSnapshot.child("first_name").getValue();
                            String lastName = (String) postSnapshot.child("last_name").getValue();
                            String email = (String) postSnapshot.child("email").getValue();
                            String password = (String) postSnapshot.child("password").getValue();
                            String username =  postSnapshot.getKey();
                            String typeStr = (String) postSnapshot.child("type").getValue();
                            User.Types type = User.parseType(typeStr);

                            User current = new User(firstName, lastName, username, email, type, password);
                            userList.add(current);

                            adapter = new UserAdapter(getApplicationContext(), userList);
                            recyclerView.setAdapter(adapter);
                        }
                        userListContainer.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
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

    @Override
    public void onResume() {
        super.onResume();
        currentUser = DatabaseUtil.getCurrentUser();
        if (null == currentUser) {
            signOut();
        } else {
            setWelcomeText();
        }
    }

    /*
     * Navigates to AccountDetails when clicked
     * Displays Account Details
     */
    public void onAccountDetailsClick(View view) {
        if (null == DatabaseUtil.getCurrentUser()) {
            signOut();
        } else {
            startActivity(new Intent(getApplicationContext(), UserAccountView.class));
        }
    }

    public void onSignOutClick(View view) {
        signOut();
    }

    public void signOut() {
        DatabaseUtil.setCurrentUser(null);
        startActivity(new Intent(getApplicationContext(), SignIn.class));
        finish();
    }

}

