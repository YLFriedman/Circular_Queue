package ca.uottawa.seg2105.project.cqondemand.domain;

import java.io.Serializable;
import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation.PasswordValidationResult;

public class User implements Serializable {


    /**
     * The class User allows for the creation of User objects, and stores the pertinent values for each User.
     * Users can be of type Homeowner, Service Provider or Admin. Getters or Setters for all mutable values
     * are also provided.
     */
    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

    private final Types type;

    /**
     * A simple enum for distinguishing different types of user accounts
     */
    public enum Types {
        ADMIN, HOMEOWNER, SERVICE_PROVIDER;
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
     * @param pass The user's password
     */
    public User(String firstName, String lastName, String username, String email, Types type, String pass) {
        if (null == type) { throw new IllegalArgumentException("The userType parameter cannot be null."); }
        if (!FieldValidation.usernameIsValid(username)) { throw new InvalidDataException("Invalid username. " +
                FieldValidation.ILLEGAL_USERNAME_CHARS_MSG); }
        PasswordValidationResult passwordValRes = FieldValidation.validatePassword(username, pass, pass);
        if (PasswordValidationResult.VALID != passwordValRes) {
            throw new InvalidDataException("Invalid password. " + passwordValRes.toString());
        }
        if (!FieldValidation.nameIsValid(firstName)) { throw new InvalidDataException("Invalid First Name. ");  }
        if (!FieldValidation.nameIsValid(lastName)) { throw new InvalidDataException("Invalid Last Name. ");  }
        if (!FieldValidation.emailIsValid(email)) { throw new InvalidDataException("Invalid Email Address. ");  }

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.type = type;
        this.password = pass;
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
    public Types getType() {
        return type;
    }

    public boolean isAdmin() {
        return type == Types.ADMIN;
    }


    /**
     * A method for converting a string representation of a Type into a Type.
     *
     * @throws IllegalArgumentException if the input String is not a valid Type
     * @param input the String representation of a Type you wish to convert
     * @return The user type that is associated to the given string
     */
    public static User.Types parseType(String input) {
        if (null == input) { throw new IllegalArgumentException("'null' is not a valid user type. "); }
        switch (input) {
            case "Homeowner": return User.Types.HOMEOWNER;
            case "Service Provider": return User.Types.SERVICE_PROVIDER;
            case "Admin": return User.Types.ADMIN;
            default: throw new IllegalArgumentException("'" + input + "' is not a valid user type. ");
        }
    }

    public void create(final AsyncActionEventListener listener) {
        DbUtil.createItem(this, listener);
    }

    public void update(final User newUser, final AsyncActionEventListener listener) {
        AsyncActionEventListener interceptListener = new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                // If we are updating the logged in user, replace the user object
                if (State.getState().getSignedInUser() == User.this) { State.getState().setSignedInUser(newUser); }
                listener.onSuccess();
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                listener.onFailure(reason);
            }
        };
        if (DbUtil.getKey(this).equals(DbUtil.getKey(newUser))) {
            DbUtil.updateItem(newUser, interceptListener);
        } else {
            DbUtil.updateItem(this, newUser, interceptListener);
        }
    }

    public void delete(final AsyncActionEventListener listener) {
        DbUtil.deleteItem(this, listener);
    }

    public boolean equals(User other) {
        return null != other && username.equals(other.username);
    }

    public static void getUser(final String username, final AsyncSingleValueEventListener<User> listener) {
        DbUtil.getItem(DbUtil.DataType.USER, username, listener);
    }

    public static void getUsers(final AsyncValueEventListener<User> listener) {
        DbUtil.getItems(DbUtil.DataType.USER, listener);
    }

    /**
     * Callback method for authenticating a given set of user credentials through the database. Fails
     * if username does not exist or does not match store password value.
     *
     * @param username the username to be authenticated
     * @param password the password to be authenticated
     * @param listener the listener that will be informed if authentication was successful or not
     */
    public static void authenticate(final String username, final String password, final AsyncActionEventListener listener) {
        getUser(username, new AsyncSingleValueEventListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getPassword().equals(password)) {
                    State.getState().setSignedInUser(user);
                    listener.onSuccess();
                } else {
                    listener.onFailure(AsyncEventFailureReason.PASSWORD_MISMATCH);
                }
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                listener.onFailure(reason);
            }
        });
    }

    public void updatePassword(String password, final AsyncActionEventListener listener) {
        PasswordValidationResult passwordValRes = FieldValidation.validatePassword(username, password, password);
        if (PasswordValidationResult.VALID != passwordValRes) {
            throw new InvalidDataException("Invalid password. " + passwordValRes.toString());
        }
        this.password = password;
        update(this, listener);
    }

}
