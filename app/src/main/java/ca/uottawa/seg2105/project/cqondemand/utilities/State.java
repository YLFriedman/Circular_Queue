package ca.uottawa.seg2105.project.cqondemand.utilities;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class State {

    private static State state;

    private User signedInUser;
    private Service currentService;

    private State() {

    }

    public static State getState(){
        if (null == state) { state = new State(); }
        return state;
    }

    public User getSignedInUser() {
        return signedInUser;
    }

    public void setSignedInUser(User user) {
        signedInUser = user;
    }

    public Service getCurrentService() {
        return currentService;
    }

    public void setCurrentService(Service currentService) {
        this.currentService = currentService;
    }
}
