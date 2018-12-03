package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public abstract class SignedInActivity extends AppCompatActivity {

    /**
     * Sets up the activity. This is run during the creation phase of the activity lifecycle.
     * @param savedInstanceState a bundle containing the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        State.getInstance(getApplicationContext());
    }

    /**
     * Checks for a logged in user when the activity is resumed.  If none is found, the app is signed out.
     * This is run during the resume phase of the activity lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (null == State.getInstance().getSignedInUser()) {
            signOut();
        }
    }

    protected void signOut() {
        State.getInstance().setSignedInUser(null);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
