package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserHome extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<User> userList;
    private User currentUser;

    /*
     * Fills in layout for UserHome activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        if(currentUser.getType()== User.Types.ADMIN){
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            userList.add(new User("a", "b", "c", "d", User.Types.HOMEOWNER, "f"));
            userList.add(new User("a", "b", "c", "d", User.Types.HOMEOWNER, "f"));
            userList.add(new User("a", "b", "c", "d", User.Types.HOMEOWNER, "f"));
            userList.add(new User("a", "b", "c", "d", User.Types.HOMEOWNER, "f"));
            userList.add(new User("a", "b", "c", "d", User.Types.HOMEOWNER, "f"));

            adapter = new UserAdapter(this, userList);
            recyclerView.setAdapter(adapter);

        }

        LinearLayout main_view = (LinearLayout) findViewById(R.id.layout_main);
        main_view.setVisibility(View.VISIBLE);
        ScrollView Scroll_view = (ScrollView) findViewById(R.id.scroll_users);
        Scroll_view.setVisibility(View.GONE);


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
                /*
                 * Makes list visible and constructs
                 */
                Scroll_view.setVisibility(View.VISIBLE);


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
        if (null == DatabaseUtil.getCurrentUser()) {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, UserAccount.class);
            startActivity(intent);
        }
    }

}

