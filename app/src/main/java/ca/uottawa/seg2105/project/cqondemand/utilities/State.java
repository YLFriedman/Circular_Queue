package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import android.content.Context;
import android.content.SharedPreferences;

public class State {

    private static State state;
    private static final String SETTINGS_NAME = "settings";
    private static final String SIGNED_IN_USER_KEY = "signed_in_user_key";
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private User signedInUser;

    private State(@NonNull Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    @NonNull
    public static State getInstance() {
        if (null == state) { throw new IllegalStateException("getInstance(Context) must be called at least once."); }
        return state;
    }

    @NonNull
    public static State getInstance(@NonNull Context context) {
        if (null == state) { state = new State(context); }
        return state;
    }

    public void setSignedInUser(@Nullable User signedInUser) {
        if (null == signedInUser) {
            prefsEditor.remove(SIGNED_IN_USER_KEY);
        } else {
            prefsEditor.putString(SIGNED_IN_USER_KEY, signedInUser.getKey());
        }
        prefsEditor.apply();
        this.signedInUser = signedInUser;
    }

    @Nullable
    public User getSignedInUser() {
        return signedInUser;
    }

    @Nullable
    public String getSignedInUserKey() {
        return prefs.getString(SIGNED_IN_USER_KEY, null);
    }

}
