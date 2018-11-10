package ca.uottawa.seg2105.project.cqondemand.utilities;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class State {

    private static State state;

    private User signedInUser;
    private User currentUser;
    private Service currentService;

    private State() {

    }

    // Access the single state object
    public static State getState(){
        if (null == state) { state = new State(); }
        return state;
    }

    // Getters
    public User getSignedInUser() {
        return signedInUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Service getCurrentService() {
        return currentService;
    }

    // Setters
    public void setSignedInUser(User signedInUser) {
        this.signedInUser = signedInUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentService(Service currentService) {
        this.currentService = currentService;
    }

}
