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
import android.widget.ToggleButton;

public class CreateAccount extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView textView;

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
                textView.setText("Your choice: " + radioButton.getText());
                Toast.makeText(getBaseContext(), "Selected Radio Button: " + radioButton.getText() , Toast.LENGTH_SHORT ).show();
            }
        });
    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
    }

    public void onTypeTglClick(View view){
        /**
        int id = view.getId();
        ToggleButton homeowner = findViewById(R.id.tgl_homeowner);
        ToggleButton service_provider = findViewById(R.id.tgl_service_provider);
        //Newbuttondsfdsfdsfds

        if (id == R.id.tgl_homeowner) {
            service_provider.setChecked(!homeowner.isChecked());
        } else if (id == R.id.tgl_service_provider) {
            homeowner.setChecked(!service_provider.isChecked());
        }**/
    }
    //TODO: Allow for the creation of an admin account, if one does not already exist. Will have to add new UI elements.

    public void onCreateClick(View view){
        //Check Email Validity
        EditText emailid = findViewById(R.id.field_email);
        String getEmailId = emailid.getText().toString();

        if (!isEmailValid(getEmailId)){
            Toast.makeText(this, "Your E-mail is not Valid!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



}
