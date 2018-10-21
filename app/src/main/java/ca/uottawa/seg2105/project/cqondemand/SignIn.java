package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private Button signInButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.btn_sign_in);
        createAccountButton = findViewById(R.id.btn_sign_up);



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
    public void onSignInClick(View view) {
        signInButton.setEnabled(false);
        createAccountButton.setEnabled(false);
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
                    signInButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
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
                        signInButton.setEnabled(true);
                        createAccountButton.setEnabled(true);


                        startActivity(loginIntent);


                    }
                    else{
                        signInButton.setEnabled(true);
                        createAccountButton.setEnabled(true);
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
