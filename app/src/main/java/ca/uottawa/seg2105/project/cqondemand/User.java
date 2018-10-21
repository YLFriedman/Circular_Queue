package ca.uottawa.seg2105.project.cqondemand;

import java.io.Serializable;

public class User implements Serializable {

    private static User currentUser = null;
    public static final String ILLEGAL_USERNAME_CHARS_REGEX = "[^a-zA-Z0-9_-]";
    public static final String ILLEGAL_USERNAME_CHARS_MSG = "Only the following characters are allowed: a-z A-z 0-9 _ -";

    /**
     *The class User allows for the creation of User objects, and stores the pertinent values for each User.
     *Users can be of type Homeowner, Service Provider or Admin. Getters or Setters for all mutable values
     *are also provided.
     *
     */
    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private final Types type;

    /**
     *A simple enum for distinguishing different types of user accounts
     */
    protected enum Types {
        ADMIN, HOMEOWNER, SERVICE_PROVIDER;
        public String toString(){
            switch(this){
                case ADMIN:
                    return "Admin";
                case HOMEOWNER:
                    return "Homeowner";
                case SERVICE_PROVIDER:
                    return "Service Provider";
                default:
                    return this.name();
            }
        }
    }

    /**
     *Constructor for User objects. This constructor supports users of type Homeowner and of type Service Provider
     *
     *@param fName The first name of the user
     *@param lName The last name of the user
     *@param uName The desired Username
     *@param email The user's email
     *@param type The type of the account
     */
    public User(String fName, String lName, String uName, String email, Types type){
        this.firstName = fName;
        this.lastName = lName;
        this.userName = uName;
        this.email = email;
        this.type = type;
    }

    public static boolean userNameIsValid(String username) {
        return !username.matches(ILLEGAL_USERNAME_CHARS_REGEX);
    }

    public static boolean emailIsValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     *Simple getter for the user's first name
     *
     *@return The user's first name
     */
    public String getFirstName(){
        return this.firstName;
    }

    /**
     *Simple getter for the user's last name
     *
     *@return The user's last name
     */
    public String getLastName(){
        return this.lastName;
    }

    /**
     *Simple getter for the user's username
     *
     *@return The user's username
     */
    public String getUserName(){
        return this.userName;
    }

    /**
     *Simple getter for the user's email
     *
     *@return The user's email
     */
    public String getEmail(){
        return this.email;
    }

    /**Simple getter for the user's type
     *
     * @return the user's type
     */
    public Types getType(){ return this.type; }

    /**
     *Simple setter for the user's first name
     *
     *@param input The user's new first name
     */
    public void setFirstName(String input){
        firstName = input;
    }

    /**
     *Simple setter for the user's last name
     *
     *@param input The user's new last name
     */
    public void setLastName(String input){
        lastName = input;
    }

    /**
     *Simple setter for the user's email
     *
     *@param input The user's new email
     */
    public void setEmail(String input){
        email = input;
    }

    /**
     *Simple setter for the user's username
     *
     *@param input The user's new username
     */
    public void setUserName(String input){
        firstName = input;
    }

    public static User.Types parseType(String input){

        switch(input){
            case "Homeowner":
                return User.Types.HOMEOWNER;
            case "Service Provider":
                return User.Types.SERVICE_PROVIDER;
            case "Admin":
                return User.Types.ADMIN;
            default:
                throw new IllegalArgumentException();
        }

    }

    public static User getCurrentUser() { return currentUser; }

    public static void setCurrentUser(User user) { currentUser = user; }

}
