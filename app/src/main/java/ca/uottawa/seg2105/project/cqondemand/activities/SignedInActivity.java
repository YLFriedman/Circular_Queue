package ca.uottawa.seg2105.project.cqondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public abstract class SignedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        State.getInstance(getApplicationContext());
    }

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
