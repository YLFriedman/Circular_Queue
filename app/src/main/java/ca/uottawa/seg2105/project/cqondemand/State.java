package ca.uottawa.seg2105.project.cqondemand;

public class State {

    private static State state;

    private User currentUser;

    private State() {

    }

    public static State getState(){
        if (null == state) { state = new State(); }
        return state;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


}
