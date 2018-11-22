package ca.uottawa.seg2105.project.cqondemand.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    protected ProgressBar progress;
    protected TextView txt_sub_title;
    protected CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progress = (ProgressBar) findViewById(R.id.progress_loading);
        txt_sub_title = findViewById(R.id.txt_sub_title);

        // Get the state
        final State state = State.getInstance(getApplicationContext());

        if (null != state.getSignedInUser()) {
            launchHomeScreen();
        } else if (null == state.getSignedInUserKey()) {
            progress.setMax(100);
            progress.setProgress(1);
            timer = new CountDownTimer(2500, 25) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progress.incrementProgressBy(1);
                }
                @Override
                public void onFinish() {
                    progress.setProgress(100);
                    launchSignInScreen();
                }
            };
            timer.start();
        } else {
            progress.setIndeterminate(true);
            txt_sub_title.setText(R.string.signing_in);
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

    protected void launchSignInScreen() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }

    protected void launchHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
