package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class State {

    private static State state;

    private User signedInUser;

    private State() {

    }

    // Access the single cellState object
    @NonNull
    public static State getState() {
        if (null == state) { state = new State(); }
        return state;
    }

    // Getters
    @Nullable
    public User getSignedInUser() {
        return signedInUser;
    }

    // Setters
    public void setSignedInUser(@Nullable User signedInUser) {
        this.signedInUser = signedInUser;
    }

}
