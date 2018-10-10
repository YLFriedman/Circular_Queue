package ca.uottawa.seg2105.project.cqondemand;

import java.io.Serializable;

public class User implements Serializable {

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
    protected enum Types{
        ADMIN, HOMEOWNER, SERVICEPROVIDER;

        public String toString(){
            switch(this){
                case ADMIN:
                    return "Admin";
                case HOMEOWNER:
                    return "Homeowner";
                case SERVICEPROVIDER:
                    return "Service Provider";
                default:
                    return "Unknown Error";

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
    }

    /**
     *Simple getter for the user's first name
     *
     *@return  The user's first name
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


}
