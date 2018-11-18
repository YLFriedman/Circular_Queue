package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class State {

    private static State state;

    private User signedInUser;
    private User currentUser;
    private Service currentService;
    private Category currentCategory;

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

    @Nullable
    public User getCurrentUser() {
        return currentUser;
    }

    @Nullable
    public Service getCurrentService() {
        return currentService;
    }

    @Nullable
    public Category getCurrentCategory() {
        return currentCategory;
    }

    // Setters
    public void setSignedInUser(@Nullable User signedInUser) {
        this.signedInUser = signedInUser;
    }

    public void setCurrentUser(@Nullable User currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentService(@Nullable Service currentService) {
        this.currentService = currentService;
    }

    public void setCurrentCategory(@Nullable Category currentCategory) {
        this.currentCategory = currentCategory;
    }

}
