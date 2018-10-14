package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Intent intent = getIntent();
        String toastText;
        if (null != (toastText = intent.getStringExtra("showToast"))) {
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * OnClick listener for the 'Sign in' button. First verifies the users credentials, then navigates
     * to the user's home screen
     *
     * @param view the sign in button which was clicked.
     */
    public void onSignInClick(View view){
        //TODO: get user info from database
        EditText field_username = findViewById(R.id.field_username);
        EditText field_password = findViewById(R.id.field_password);
        final String inputUsername = field_username.getText().toString();
        final String inputPassword = field_password.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(inputUsername);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(), "That user does not exist!", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    String realPassword = (String) dataSnapshot.child("password").getValue();
                    if(inputPassword.equals(realPassword)){
                        String firstName = (String) dataSnapshot.child("first_name").getValue();
                        String lastName = (String) dataSnapshot.child("last_name").getValue();
                        String email = (String) dataSnapshot.child("email").getValue();
                        String typeStr = (String) dataSnapshot.child("type").getValue();
                        User.Types type = parseType(typeStr);

                        User.setCurrentUser(new User(firstName, lastName, inputUsername, email, type));
                        Intent loginIntent = new Intent(getApplicationContext(), UserHome.class);


                        startActivity(loginIntent);


                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Incorrect username/password combination!", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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

    public User.Types parseType(String input){

        switch(input){
            case "Homeowner":
                return User.Types.HOMEOWNER;
            case "Service Provider":
                return User.Types.SERVICE_PROVIDER;
            case "Admin":
                return User.Types.ADMIN;
            default:
                throw new IllegalArgumentException();
        }

    }


}
