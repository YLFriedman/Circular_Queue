package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    /**
     * OnClick listener for the 'Sign in' button. First verifies the users credentials, then navigates
     * to the user's home screen
     *
     * @param view the sign in button which was clicked.
     */
    public void onSignInClick(View view){
        //TODO: get user info from database
        if(validateLogin()){
            User user = new User("firstName", "lastName", "username",
                    "email@example.com", User.Types.HOMEOWNER);
            Intent loginIntent = new Intent(this, UserHome.class);
            loginIntent.putExtra("user", user);

            startActivity(loginIntent);
        }
    }

    /**
     * Navigates the user to the create account screen when the corresponding button is clicked
     *
     * @param view the create account button which was clicked
     */
    public void onCreateAccountClick(View view){
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);

    }

    /**
     * Method for validating a user's credentials
     *
     * @return true if the user's credentials are valid.
     */
    public boolean validateLogin(){
        //TODO: Implement a real login validation method, using databases
        return true;
    }

}
