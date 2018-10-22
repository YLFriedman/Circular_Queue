package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
        LinearLayout main_view = (LinearLayout) findViewById(R.id.layout_main);
        main_view.setVisibility(View.VISIBLE);
        ScrollView Scroll_view = (ScrollView) findViewById(R.id.scroll_users);
        Scroll_view.setVisibility(View.GONE);
        TextView scroll_title= (TextView) findViewById(R.id.txt_Scroll);
        scroll_title.setVisibility(View.GONE);

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
                *Makes list visible and constructs
                 */
                scroll_title.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(this, UserAccount.class);
        startActivity(intent);
    }

}

