package ca.uottawa.seg2105.project.cqondemand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    private User currentUser;
    private Button btn_sign_in;
    private Button btn_sign_up;
    private Button btn_create_admin_account;
    private EditText field_username;
    private EditText field_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        currentUser = State.getState().getCurrentUser();
        if (null == currentUser) {
            field_username = findViewById(R.id.field_username);
            field_password = findViewById(R.id.field_password);
            btn_sign_in = findViewById(R.id.btn_sign_in);
            btn_sign_up = findViewById(R.id.btn_sign_up);
            btn_create_admin_account = findViewById(R.id.btn_create_admin_account);
            User.getUser("admin", new AsyncValueEventListener<User>() {
                @Override
                public void onSuccess(ArrayList<User> data) {
                    btn_create_admin_account.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(AsyncEventFailureReason reason) {
                    if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                        btn_create_admin_account.setVisibility(View.VISIBLE);
                    } else {
                        btn_create_admin_account.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = State.getState().getCurrentUser();
        if (null != currentUser) {
            Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (RESULT_CANCELED == resultCode) {
            //field_username.getText().clear();
            field_password.getText().clear();
            if (field_username.getText().toString().isEmpty()) {
                field_username.requestFocus();
            } else {
                field_password.requestFocus();
            }
        } else {
            field_username.setText(intent.getStringExtra("username"));
            field_password.getText().clear();
            field_password.requestFocus();
        }
    }

    /**
     * OnClick listener for the 'Sign in' button. First verifies the users credentials, then navigates
     * to the user's home screen
     *
     * @param view the sign in button which was clicked.
     */
    public void onSignInClick(View view) {
        btn_sign_in.setEnabled(false);
        btn_sign_up.setEnabled(false);
        User.authenticate(field_username.getText().toString(), field_password.getText().toString(), new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                btn_sign_in.setEnabled(true);
                btn_sign_up.setEnabled(true);
                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(loginIntent);
                finish();
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                btn_sign_in.setEnabled(true);
                btn_sign_up.setEnabled(true);
                switch (reason) {
                    case DATABASE_ERROR: Toast.makeText(getApplicationContext(), "Unable to sign in due to a Database Error! Please try again later.", Toast.LENGTH_LONG).show(); break;
                    case INVALID_DATA: Toast.makeText(getApplicationContext(), "Unable to sign in, your account data is corrupt.", Toast.LENGTH_LONG).show(); break;
                    default: Toast.makeText(getApplicationContext(), "Invalid Credentials!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Navigates the user to the create account screen when the corresponding button is clicked
     *
     * @param view the create account button which was clicked
     */
    public void onCreateAccountClick(View view)  {
        Intent intent = new Intent(getApplicationContext(), UserAccountCreateActivity.class);
        intent.putExtra("username", field_username.getText().toString());
        startActivityForResult(intent,0);
    }

    public void onCreateAdminAccountClick(View view) {
        User user = new User("Admin", "User", "admin", "yfrie071@uottawa.ca", User.Types.ADMIN, "admin");
        user.create(new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The admin user has been created successfully. Login with the username and password 'admin'.", Toast.LENGTH_LONG).show();
                btn_create_admin_account.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.ALREADY_EXISTS == reason) {
                    Toast.makeText(getApplicationContext(), "The admin account already exists.", Toast.LENGTH_LONG).show();
                    btn_create_admin_account.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "There was an error creating the admin account. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onCreateTestAccountClick(View view) {
        User user = new User("Test", "User", "test", "test@test.test", User.Types.SERVICE_PROVIDER, "cqpass");
        user.create(new AsyncActionEventListener() {
            @Override
            public void onSuccess() { }
            @Override
            public void onFailure(AsyncEventFailureReason reason) { }
        });
    }

    public void onCreateTestServiceClick(View view){
        ArrayList<User> providers = new ArrayList<>();
        providers.add(new User("joe", "blow", "joeblow", "joeblow@mail.com", User.Types.SERVICE_PROVIDER, "CQPASS"));
        providers.add(new User("buddy", "guy", "buddyguy", "buddyguy@mail.com", User.Types.SERVICE_PROVIDER, "CQPASS"));


        /*Service testService = new Service("security", 140, providers, "locksmith");
        DbUtil.createService(testService, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "It worked, apparently!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(AsyncEventFailureReason reason) {

                Toast.makeText(getApplicationContext(), "It failed, apparently because of " + reason.toString(), Toast.LENGTH_LONG).show();

            }
        });*/
    }

    public void onGetServiceTestClick(View view){
        /*DbUtil.getService("locksmith", new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(ArrayList<Service> data) {
                String category = data.get(0).getCategory();
                Toast.makeText(getApplicationContext(), "Wow it worked, the category is " + category, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                switch(reason){
                    case DOES_NOT_EXIST: Toast.makeText(getApplicationContext(), "Doesn't seem to exist", Toast.LENGTH_LONG).show();
                    break;

                    case DATABASE_ERROR: Toast.makeText(getApplicationContext(), "DB error of some kind", Toast.LENGTH_LONG).show();
                }

            }
        });*/
    }

}
