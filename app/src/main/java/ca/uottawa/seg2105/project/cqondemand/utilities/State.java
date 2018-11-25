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

    private User signedInUser;

    private State(@NonNull Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
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
        if (null == signedInUser) { removePref(SIGNED_IN_USER_KEY);}
        else { setStringPref(SIGNED_IN_USER_KEY, signedInUser.getKey()); }
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

    public void removePref(String key) {
        prefs.edit().remove(key).apply();
    }

    public void setStringPref(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public void setBooleanPref(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public String getStringPref(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public boolean getBooleanPref(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

}
