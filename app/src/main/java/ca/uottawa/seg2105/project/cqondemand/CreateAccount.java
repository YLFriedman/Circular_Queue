package ca.uottawa.seg2105.project.cqondemand;

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

    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView textView;

    boolean[] checks = new boolean[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        radioGroup = findViewById(R.id.radioGroup);
        textView = findViewById(R.id.text_view_selected);

        //Make text field print selected choice based on radio button
        Button buttonApply = findViewById(R.id.button_apply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                textView.setText(radioButton.getText());
                Toast.makeText(getBaseContext(), "Selected Radio Button: " + radioButton.getText() , Toast.LENGTH_SHORT ).show();
            }
        });
    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
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

        if(username.isEmpty()){
            usernameId.setError("Username is required!");
            usernameId.requestFocus();
            return;
        }

        //Check First Name validity
        EditText firstNameId = findViewById(R.id.field_first_name);
        String firstName = firstNameId.getText().toString().trim();

        if(firstName.isEmpty()){
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
        } else if (!isEmailValid(email)){
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

        //check if account type selected
        TextView typeId = findViewById(R.id.text_view_selected);
        String type = typeId.getText().toString().trim();
        if(type.equals("Please Apply Choice!")){
            typeId.setError("Select an account type!"); //Set error
        }

        Toast.makeText(this, "Passed as " + type, Toast.LENGTH_LONG).show();
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
