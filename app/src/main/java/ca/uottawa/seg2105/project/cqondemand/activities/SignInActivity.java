package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.database.DbReview;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.Authentication;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class SignInActivity extends AppCompatActivity {

    protected boolean itemClickEnabled = true;
    protected Button btn_sign_in;
    protected Button btn_sign_up;
    protected Button btn_create_admin_account;
    protected EditText field_username;
    protected EditText field_password;

    private CheckBox box_remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        State state = State.getInstance(getApplicationContext());
        setContentView(R.layout.activity_sign_in);
        if (null == state.getSignedInUser()) {
            field_username = findViewById(R.id.field_username);
            field_password = findViewById(R.id.field_password);
            btn_sign_in = findViewById(R.id.btn_sign_in);
            btn_sign_up = findViewById(R.id.btn_sign_up);
            btn_create_admin_account = findViewById(R.id.btn_create_admin_account);

            box_remember = findViewById(R.id.box_remember);

            if (state.getBooleanPref("saveLogin", false)) {
                field_username.setText(state.getStringPref("username", ""));
                field_password.setText(state.getStringPref("password", ""));
                box_remember.setChecked(true);
            }

            DbUser.getUserByUsername("admin", new AsyncSingleValueEventListener<User>() {
                @Override
                public void onSuccess(@NonNull User user) {
                    btn_create_admin_account.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
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
        if (null != State.getInstance().getSignedInUser()) {
            Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            itemClickEnabled = true;
        }
    }

    @Override
    public void onBackPressed () {
        moveTaskToBack (false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (RESULT_CANCELED == resultCode) {
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
        if (field_username.getText().toString().trim().isEmpty()) {
            field_username.setError(getString(R.string.empty_username_error));
            field_username.requestFocus();
            field_username.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
        } else if (field_password.getText().toString().isEmpty()) {
            field_password.setError(getString(R.string.empty_password_error));
            field_password.requestFocus();
            field_password.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_custom));
        } else {
            // Clear the remember me saved settings
            final State state = State.getInstance();
            state.setBooleanPref("saveLogin", false);
            state.removePref("username");
            state.removePref("password");

            btn_sign_in.setEnabled(false);
            btn_sign_up.setEnabled(false);
            Authentication.authenticate(field_username.getText().toString().trim(), field_password.getText().toString(), new AsyncActionEventListener() {
                @Override
                public void onSuccess() {
                    // If remember me was checked and the login is successful, save the login details
                    if (box_remember.isChecked()) {
                        state.setBooleanPref("saveLogin", true);
                        state.setStringPref("username", field_username.getText().toString());
                        state.setStringPref("password", field_password.getText().toString());
                    }
                    btn_sign_in.setEnabled(true);
                    btn_sign_up.setEnabled(true);
                    Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                    btn_sign_in.setEnabled(true);
                    btn_sign_up.setEnabled(true);
                    switch (reason) {
                        case DATABASE_ERROR:
                            Toast.makeText(getApplicationContext(), R.string.sign_in_db_error, Toast.LENGTH_LONG).show();
                            break;
                        case INVALID_DATA:
                            Toast.makeText(getApplicationContext(), R.string.sign_in_corrupt_data_error, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.sign_in_invalid_creds, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Navigates the user to the create account screen when the corresponding button is clicked
     *
     * @param view the create account button which was clicked
     */
    public void onCreateAccountClick(View view)  {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        intent.putExtra("username", field_username.getText().toString());
        startActivityForResult(intent,0);
    }

    public void onCreateAdminAccountClick(View view) {
        if (!itemClickEnabled) { return; }
        itemClickEnabled = false;
        User user = new User("Admin", "User", "admin", "yfrie071@uottawa.ca", User.Type.ADMIN, Authentication.genHash("admin"));
        DbUser.createUser(user, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "The admin user has been created successfully. Login with the username and password 'admin'.", Toast.LENGTH_LONG).show();
                btn_create_admin_account.setVisibility(View.GONE);
                itemClickEnabled = true;
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.ALREADY_EXISTS == reason) {
                    Toast.makeText(getApplicationContext(), "The admin account already exists.", Toast.LENGTH_LONG).show();
                    btn_create_admin_account.setVisibility(View.GONE);
                    itemClickEnabled = true;
                } else {
                    Toast.makeText(getApplicationContext(), "There was an error creating the admin account. Please try again later.", Toast.LENGTH_LONG).show();
                    itemClickEnabled = true;
                }
            }
        });
    }

    public void onCreateTestAccountClick(View view) {
        User user = new User("Test", "User", "test", "test@test.test", User.Type.HOMEOWNER, Authentication.genHash("cqpass"));
        DbUser.createUser(user, new AsyncActionEventListener() {
            @Override
            public void onSuccess() { }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) { }
        });
    }

    public void onUpdateServiceTestClick(View view){
        Address address = new Address("45", 2546, "Easy st.", "Ottawa", "Ontario", "Canada", "K1Z5N9");
        java.sql.Timestamp date = new Timestamp(System.currentTimeMillis());

        ServiceProvider provides = new ServiceProvider("-LRTteBm1Bvhh8KMiGd_", "MOMMY", "please", "DONOTspankme", "bad@boy.com",
                "cqpass", "Spaces Allowed", true, "6132453125", address, "I'm gaaaaaaaaaay", 0, 0, 0, null);
        System.out.println("OUTPUT");
        User user = new User("-LRYCsBuPG8E1gmy6otV", "Test", "Homeowner", "Hope", "thisworks@mail.com", User.Type.HOMEOWNER, Authentication.genHash("cqpass"));
        Booking withProvider = new Booking(date, date, null, provides, provides.getKey(), user.getKey(), Booking.Status.REQUESTED, "Extra Nutty", 69, null, true);
        Booking withHomeowner = new Booking("-LSAxH1J826LorP7qVzE", date, date, date, null, user, provides.getKey(), user.getKey(), Booking.Status.REQUESTED, "Extra Nutty", 69, null, false);
        withProvider.setKey("-LSB6L4Zq_hFaIH2it1b");

        Review review = new Review(5, "Great Job!", user, withHomeowner);

        DbReview.getReviews(provides.getKey(), new AsyncValueEventListener<Review>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Review> data) {
                Review review = data.get(0);
                System.out.println(review.getComment());
            }

            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {

            }
        });


    }

}
