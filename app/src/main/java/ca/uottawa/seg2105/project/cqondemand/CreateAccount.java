package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccount extends AppCompatActivity {

    boolean[] checks = new boolean[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);



    }

    public static boolean userNameIsValid(String username) {
        return username.matches("[^a-zA-Z0-9-_]");
    }

    private boolean emailIsValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //TODO: Allow for the creation of an admin account, if one does not already exist. Will have to add new UI elements.

    public void onCreateClick(View view){

        //init boolean array to all true;
        for (int i = 0; i < checks.length; i++){
            checks[i] = true;
        }

        //Check username validity
        EditText usernameId = findViewById(R.id.field_username);
        String username = usernameId.getText().toString().trim();

        if (username.isEmpty()) {
            usernameId.setError("Username is required!");
            usernameId.requestFocus();
            return;
        } else if (!userNameIsValid(username)) {
            usernameId.setError("Username is invalid. " + User.ALLOWED_USERNAME_CHARS_MSG);
            usernameId.requestFocus();
            return;
        }

        //Check First Name validity
        EditText firstNameId = findViewById(R.id.field_first_name);
        String firstName = firstNameId.getText().toString().trim();

        if (firstName.isEmpty()){
            firstNameId.setError("First name is required!");
            firstNameId.requestFocus();
            return;
        }

        //Check Last Name validity
        EditText lastNameId = findViewById(R.id.field_last_name);
        String lastName = lastNameId.getText().toString().trim();

        if(lastName.isEmpty()){
            lastNameId.setError("Last name is required!");
            lastNameId.requestFocus();
            return;
        }

        //Check Email Validity
        EditText emailId = findViewById(R.id.field_email);
        String email = emailId.getText().toString();

        if(email.isEmpty()){
            emailId.setError("Email is required!");
            emailId.requestFocus();
            return;
        } else if (!emailIsValid(email)){
            emailId.setError("This is an invalid E-mail!");
            return;
        }

        //check password matching
        EditText passwordId = findViewById(R.id.field_password);
        String password = passwordId.getText().toString().trim();

        EditText passwordConfirmId = findViewById(R.id.field_password_confirm);
        String passwordConfirm = passwordConfirmId.getText().toString().trim();

        if (password.isEmpty()) {
            passwordId.setError("Password is required!");
            passwordId.requestFocus();
            return;
        } else if (passwordConfirm.isEmpty()) {
            passwordConfirmId.setError("Password Confirm is required!");
            passwordConfirmId.requestFocus();
            return;
        } else if (password.length()<6) {
            passwordId.setError("Minimum length of password should be 6");
            passwordId.requestFocus();
            return;
        } else if (!password.equals(passwordConfirm)) {
            passwordConfirmId.setError("Passwords does not match!");
            passwordId.requestFocus();
            return;
        }

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String type = radioButton.getText().toString();
        User.Types accountType;
        switch (type) {
            case "Service Provider": accountType = User.Types.SERVICE_PROVIDER; break;
            case "Admin": accountType = User.Types.ADMIN; break;
            default: accountType = User.Types.HOMEOWNER; break;
        }


        User newUser = new User(firstName, lastName, username, email, accountType);
        if (newUser.create(password)) {
            User.setCurrentUser(newUser);
            Intent intent = new Intent(this, SignIn.class);
            intent.putExtra("showToast", "The user account '" + username + "' has been successfully created.  Please sign in to continue.");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unable to create your account at this time. Please try again later.", Toast.LENGTH_LONG).show();
        }

    }
}
