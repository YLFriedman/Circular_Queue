package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation.PasswordValidationResult;

/**
 * The class User allows for the creation of User objects, and stores the pertinent values for each User.
 * Users can be of type Homeowner, Service Provider or Admin. Getters for all mutable values
 * are also provided.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */

public class User implements Serializable {

    private static final long serialVersionUID = 1;
    /**
     * Stores the database key of the user
     */
    protected String key;
    /**
     * Stores the first name of the user
     */
    protected String firstName;
    /**
     * Stores the last name of the user
     */
    protected String lastName;
    /**
     * Stores the username of the user
     */
    protected String username;
    /**
     * Stores the email of the user
     */
    protected String email;
    /**
     * Stores the user password
     */
    protected String password;
    /**
     * Stores the account type of the user
     */
    protected final Type type;

    /**
     * A simple enum for distinguishing different types of user accounts
     */
    public enum Type {
        ADMIN, HOMEOWNER, SERVICE_PROVIDER;
        @NonNull
        public String toString() {
            switch (this) {
                case ADMIN: return "Admin";
                case HOMEOWNER: return "Homeowner";
                case SERVICE_PROVIDER: return "Service Provider";
                default: return this.name();
            }
        }
        public static Type parse(String status) {
            switch (status.toUpperCase()) {
                case "ADMIN": return Type.ADMIN;
                case "HOMEOWNER": return Type.HOMEOWNER;
                case "SERVICE PROVIDER": return Type.SERVICE_PROVIDER;
                default: throw new IllegalArgumentException("Invalid User Type");
            }
        }
    }

    /**
     * Constructor for User objects. Does not require a key.
     *
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param username The Username (Used as a unique identifier)
     * @param email The user's email address
     * @param type The type of the account
     * @param password The user's password
     */
    public User(@NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Type type, @NonNull String password) {
        if (!FieldValidation.usernameIsValid(username)) { throw new InvalidDataException("Invalid username. " + FieldValidation.USERNAME_CHARS); }
        PasswordValidationResult passwordValRes = FieldValidation.validatePassword(username, password, password);
        if (PasswordValidationResult.VALID != passwordValRes) { throw new InvalidDataException("Invalid password. " + passwordValRes.toString()); }
        if (!FieldValidation.personNameIsValid(firstName)) { throw new InvalidDataException("Invalid First Name. ");  }
        if (!FieldValidation.personNameIsValid(lastName)) { throw new InvalidDataException("Invalid Last Name. ");  }
        if (null != email && !email.equals("{@TEST}") && !FieldValidation.emailIsValid(email)) { throw new InvalidDataException("Invalid Email Address. ");  }
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.type = type;
        this.password = password;
    }

    /**
     * Constructor for User objects. Requires a database Key
     *
     * @param key  the database key
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param username The Username (Used as a unique identifier)
     * @param email The user's email address
     * @param type The type of the account
     * @param password The user's password
     */
    public User(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Type type, @NonNull String password) {
        this(firstName, lastName, username, email, type, password);
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
    }

    /**
     * Method to get a User's full name
     * @return A string containing the user's first and last names
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    /**
     * Getter for the database key associated with a User
     * @return the associated key
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for the user's first name
     * @return The user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the user's last name
     * @return The user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for the user's username
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the user's password
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter for the user's email
     * @return The user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the user's type
     * @return the user's type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the unique username of this User
     * @return the unique username
     */
    public String getUniqueName() {
        return getUniqueName(username);
    }

    /**
     * Generates a unique name based in an input string
     * @param username input string
     * @return a unique version of the input. All letters set to lowercase, spaces and other special characters replaces by underscores
     */
    public static String getUniqueName(@NonNull String username) {
        String uniqueName = username.toLowerCase();
        uniqueName = uniqueName.replaceAll("[^a-z0-9]+", "_");
        return uniqueName;
    }

    /**
     * Method to determine if a particular User is equal to a given object
     * @param otherObj the object to be compared to
     * @return whether or not this user is equal to the given object
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof User)) { return false; }
        if (this == otherObj) { return true; }
        User other = (User) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        if ((null == firstName) != (null == other.firstName) || (null != firstName && !firstName.equals(other.firstName))) { return false; }
        if ((null == lastName)  != (null == other.lastName)  || (null != lastName  && !lastName.equals(other.lastName))) { return false; }
        if ((null == email)     != (null == other.email)     || (null != email     && !email.equals(other.email))) { return false; }
        if ((null == password)  != (null == other.password)  || (null != password  && !password.equals(other.password))) { return false; }
        if ((null == type)      != (null == other.type)      || (null != type      && !type.equals(other.type))) { return false; }
        return null != getUniqueName() && getUniqueName().equals(other.getUniqueName());
    }

    /**
     * Returns a string representation of a particular user
     * @return the string representation
     */
    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, lastName, username);
    }

}
