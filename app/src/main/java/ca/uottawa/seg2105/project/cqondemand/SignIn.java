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

    private User currentUser;
    private Button signInButton;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.btn_sign_in);
        createAccountButton = findViewById(R.id.btn_sign_up);

        currentUser = DatabaseUtil.getCurrentUser();
        if (null == currentUser) {
            Intent intent = getIntent();
            String toastText;
            if (null != (toastText = intent.getStringExtra("showToast"))) {
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            }
        } else {
            Intent loginIntent = new Intent(getApplicationContext(), UserHome.class);
            startActivity(loginIntent);
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
        UserEventListener userListener = new UserEventListener() {
            @Override
            public void onSuccess() {
                signInButton.setEnabled(true);
                createAccountButton.setEnabled(true);
                Intent loginIntent = new Intent(getApplicationContext(), UserHome.class);
                startActivity(loginIntent);
            }
            @Override
            public void onFailure(DatabaseUtil.CallbackFailure reason) {
                signInButton.setEnabled(true);
                createAccountButton.setEnabled(true);
                switch (reason) {
                    case DATABASE_ERROR: Toast.makeText(getApplicationContext(), "Database Error!", Toast.LENGTH_LONG).show(); break;
                    default: Toast.makeText(getApplicationContext(), "Invalid Credentials!", Toast.LENGTH_LONG).show();
                }
            }
        };

        DatabaseUtil.authenticate(inputUsername, inputPassword, userListener);

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

}
