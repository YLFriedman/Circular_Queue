package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation.PasswordValidationResult;

public class User implements Serializable {

    private static final long serialVersionUID = 1;

    protected String key;
    /**
     * The class User allows for the creation of User objects, and stores the pertinent values for each User.
     * Users can be of type Homeowner, Service Provider or Admin. Getters or Setters for all mutable values
     * are also provided.
     */
    protected String firstName;

    protected String lastName;

    protected String username;

    protected String email;

    protected String password;

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
    }

    /**
     * Constructor for User objects. This constructor supports users of type Homeowner and of type Service Provider
     *
     * @throws IllegalArgumentException if any of the parameters are null
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

    public User(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Type type, @NonNull String password) {
        this(firstName, lastName, username, email, type, password);
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
    }

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

    public String getUniqueName() {
        return getUniqueName(username);
    }

    public static String getUniqueName(@NonNull String username) {
        String uniqueName = username.toLowerCase();
        uniqueName = uniqueName.replaceAll("[^a-z0-9]+", "_");
        return uniqueName;
    }

    public boolean isAdmin() {
        return type == Type.ADMIN;
    }

    /**
     * A method for converting a string representation of a Type into a Type.
     *
     * @throws IllegalArgumentException if the input String is not a valid Type
     * @param input the String representation of a Type you wish to convert
     * @return The user type that is associated to the given string
     */
    public static Type parseType(String input) {
        if (null == input) { throw new IllegalArgumentException("'null' is not a valid user type. "); }
        switch (input) {
            case "Homeowner": return Type.HOMEOWNER;
            case "Service Provider": return Type.SERVICE_PROVIDER;
            case "Admin": return Type.ADMIN;
            default: throw new IllegalArgumentException("'" + input + "' is not a valid user type. ");
        }
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof User)) { return false; }
        if (this == otherObj) { return true; }
        User other = (User) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        return null != getUniqueName() && getUniqueName().equals(other.getUniqueName());
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, lastName, username);
    }

}
