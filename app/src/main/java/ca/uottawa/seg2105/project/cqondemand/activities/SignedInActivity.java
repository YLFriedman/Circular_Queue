package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public abstract class SignedInActivity extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
        if (null == State.getInstance(getApplicationContext()).getSignedInUser()) {
            signOut();
        }
    }

    protected void signOut() {
        State.getInstance(getApplicationContext()).setSignedInUser(null);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
