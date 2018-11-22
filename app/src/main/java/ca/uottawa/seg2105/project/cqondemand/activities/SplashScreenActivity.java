package ca.uottawa.seg2105.project.cqondemand.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.R;
import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Get the state
        final State state = State.getInstance(this);

        if (null != state.getSignedInUser()) {
            launchHomeScreen();
        } else if (null == state.getSignedInUserKey()) {
            launchSignInScreen();
        } else {
            DbUser.getUser(state.getSignedInUserKey(), new AsyncSingleValueEventListener<User>() {
                @Override
                public void onSuccess(@NonNull User item) {
                    state.setSignedInUser(item);
                    launchHomeScreen();
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) {
                    launchSignInScreen();
                }
            });
        }

    }

    private void launchSignInScreen() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
