package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserHome extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

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

    public void onAccountDetailsClick(View view){
        Intent intent = new Intent(this, UserAccount.class);
        startActivity(intent);
    }

}
