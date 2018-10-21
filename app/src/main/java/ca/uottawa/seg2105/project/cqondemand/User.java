package ca.uottawa.seg2105.project.cqondemand;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class User implements Serializable {

    private static User currentUser = null;
    public static final String ALLOWED_USERNAME_CHARS_MSG = "Only the following characters are allowed: a-z A-z 0-9 _ -";

    private DatabaseReference db;
    /**
     *The class User allows for the creation of User objects, and stores the pertinent values for each User.
     *Users can be of type Homeowner, Service Provider or Admin. Getters or Setters for all mutable values
     *are also provided.
     *
     */
    private String fName, lName, uName, email;

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
        this.fName = fName;
        this.lName = lName;
        this.uName = uName;
        this.email = email;
        this.type = type;
        db = FirebaseDatabase.getInstance().getReference("users");
    }

    public boolean create(String password) {
        try {
            db.child(uName).child("type").setValue(type.toString());
            db.child(uName).child("first_name").setValue(fName);
            db.child(uName).child("last_name").setValue(lName);
            db.child(uName).child("email").setValue(email);
            db.child(uName).child("password").setValue(password);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean update(String fName, String lName, String uName, String email) {
        try {
            db.child(uName).child("first_name").setValue(fName);
            db.child(uName).child("last_name").setValue(lName);
            db.child(email).child("email").setValue(email);
            //db.child(uName).child("password").setValue(password);

            String oldUsername = uName;
            this.fName = fName;
            this.lName = lName;
            this.uName = uName;
            this.email = email;

        } catch (Exception e) {
            return false;
        }
        return true;

        // TODO: Update database
    }

    public boolean delete() {
        return false;
    }

    /**
     *Simple getter for the user's first name
     *
     *@return The user's first name
     */
    public String getFirstName(){
        return this.fName;
    }

    /**
     *Simple getter for the user's last name
     *
     *@return The user's last name
     */
    public String getLastName(){
        return this.lName;
    }
    /**
     *Simple getter for the user's username
     *
     *@return The user's username
     */
    public String getUserName(){
        return this.uName;
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
        fName = input;
    }

    /**
     *Simple setter for the user's last name
     *
     *@param input The user's new last name
     */
    public void setLastName(String input){
        lName = input;
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
        fName = input;
    }

    public static User getCurrentUser() { return currentUser; }

    public static void setCurrentUser(User user) { currentUser = user; }

}
