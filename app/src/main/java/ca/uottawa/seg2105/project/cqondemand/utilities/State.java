package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * The class <b>State</b> is a singleton used to cache data related to the current state of the application.
 * It is also used to standardize the storage and retrieval of data saved using the SharedPreferences API.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class State {

    /**
     * The single state object.
     */
    private static State state;

    /**
     * The name of the settings file.
     */
    private static final String SETTINGS_NAME = "settings";

    /**
     * The name of the key where the signed-in user key is saved.
     */
    private static final String SIGNED_IN_USER_KEY = "signed_in_user_key";

    /**
     * The SharedPreferences object generated from the context of the app.
     */
    private SharedPreferences prefs;

    /**
     * The User object representing the currently signed-in user.
     */
    private User signedInUser;

    /**
     * A private constructor used to instantiate the State instance.
     * @param context the context of the application that the state object is used within
     */
    private State(@NonNull Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Retrieves the instance of the state object.
     * Can only be used after the getInstance(Context) has been used at least once.
     * @throws IllegalStateException if it is called before getInstance(Context)
     * @return the instance of the state object
     */
    @NonNull
    public static State getInstance() {
        if (null == state) { throw new IllegalStateException("getInstance(Context) must be called at least once."); }
        return state;
    }

    /**
     * Generates or retrieves the instance of the state object.
     * @param context the context of the application that the state object is used within
     * @return the instance of the state object
     */
    @NonNull
    public static State getInstance(@NonNull Context context) {
        if (null == state) { state = new State(context); }
        return state;
    }

    /**
     * Setter for the currently signed-in user.
     * The SharedPreferences are updated when this is used to set the signed-in user.
     * @param signedInUser the user object to assign as the signed-in user
     */
    public void setSignedInUser(@Nullable User signedInUser) {
        if (null == signedInUser) { removePref(SIGNED_IN_USER_KEY);}
        else { setStringPref(SIGNED_IN_USER_KEY, signedInUser.getKey()); }
        this.signedInUser = signedInUser;
    }

    /**
     * Getter for the currently signed-in user.
     * @return the user object that is set as the signed-in user
     */
    @Nullable
    public User getSignedInUser() {
        return signedInUser;
    }

    /**
     * Getter for the key of the currently signed-in user.
     * @return the key of the signed-in user
     */
    @Nullable
    public String getSignedInUserKey() {
        return prefs.getString(SIGNED_IN_USER_KEY, null);
    }

    /**
     * Removes a preference from the SharedPreferences file.
     * @param key the key of the preference to remove
     */
    public void removePref(String key) {
        prefs.edit().remove(key).apply();
    }

    /**
     * Sets the value of a string preference
     * @param key the key of the preference to set
     */
    public void setStringPref(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    /**
     * Sets the value of a boolean preference
     * @param key the key of the preference to set
     */
    public void setBooleanPref(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    /**
     * gets the value of a string preference
     * @@return the value of the preference
     */
    public String getStringPref(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    /**
     * gets the value of a boolean preference
     * @@return the value of the preference
     */
    public boolean getBooleanPref(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

}
