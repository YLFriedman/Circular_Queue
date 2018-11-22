package ca.uottawa.seg2105.project.cqondemand.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
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
    protected LinearLayout progressLL;
    protected TextView txt_sub_title;
    protected CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progress = (ProgressBar) findViewById(R.id.progress_loading);
        txt_sub_title = findViewById(R.id.txt_sub_title);


        // Get the state
        final State state = State.getInstance(this);

        if (null != state.getSignedInUser()) {
            timer = new CountDownTimer(2000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progress.incrementProgressBy(5);
                }
                @Override
                public void onFinish() {
                    launchHomeScreen();
                }
            };
            timer.start();
        } else if (null == state.getSignedInUserKey()) {
            timer = new CountDownTimer(2000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progress.incrementProgressBy(5);
                }
                @Override
                public void onFinish() {
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

    private void launchSignInScreen() {
        //if (true) { return; }
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchHomeScreen() {
        //if (true) { return; }
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
