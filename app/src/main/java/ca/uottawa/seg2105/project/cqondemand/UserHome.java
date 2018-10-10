package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Intent loginIntent = getIntent();
        Bundle bundle = loginIntent.getExtras();
        User user = (User) bundle.getSerializable("user");

        TextView txtWelcome = findViewById(R.id.txt_welcome);
        txtWelcome.setText("Hello " + user.getFirstName() + user.getLastName());
        TextView txtRole = findViewById(R.id.txt_role);
        txtRole.setText("You are logged in as " + user.getType().toString());


    }

    public void onAccountDetailsClick(View view){
        //TODO: navigate to the account details screen when clicked. Bring user information along to populate fields
    }
}
