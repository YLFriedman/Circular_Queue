package ca.uottawa.seg2105.project.cqondemand.domain;

import java.io.Serializable;
import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class User implements Serializable {

    public static final String ILLEGAL_USERNAME_CHARS_REGEX = ".*[^a-zA-Z0-9_].*";
    public static final String ILLEGAL_USERNAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z 0-9 _";
    // Illegal Name Characters: 0-9 < > ] [ } { \ / ! @ # $ % ^ & * _ + = ) (
    public static final String ILLEGAL_NAME_CHARS_REGEX = ".*[0-9<>\\]\\[}{\\\\/!@#$%^&*_+=)(:;].*";
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final String[] ILLEGAL_PASSWORDS = { "password" };
    public enum PasswordValidationResult { VALID, EMPTY, TOO_SHORT, CONFIRM_MISMATCH, ILLEGAL_PASSWORD, CONTAINS_USERNAME }

    /**
     * The class User allows for the creation of User objects, and stores the pertinent values for each User.
     * Users can be of type Homeowner, Service Provider or Admin. Getters or Setters for all mutable values
     * are also provided.
     */
    private String firstName;

    private String lastName;

    private String userName;

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
     * @param fName The first name of the user
     * @param lName The last name of the user
     * @param uName The Username (Used as a unique identifier)
     * @param emailAddr The user's email address
     * @param userType The type of the account
     * @param pass The user's password
     */
    public User(String fName, String lName, String uName, String emailAddr, Types userType, String pass) {

        if (null == userType) { throw new IllegalArgumentException("The userType parameter cannot be null."); }
        if (!userNameIsValid(uName)) { throw new InvalidDataException("Invalid username. " + ILLEGAL_USERNAME_CHARS_MSG); }
        PasswordValidationResult passwordValRes = validatePassword(uName, pass, pass);
        if (PasswordValidationResult.VALID != passwordValRes) {
            throw new InvalidDataException("Invalid password. " + passwordValRes.toString());
        }
        if (!userNameIsValid(fName)) { throw new InvalidDataException("Invalid First Name. ");  }
        if (!userNameIsValid(lName)) { throw new InvalidDataException("Invalid Last Name. ");  }

        firstName = fName;
        lastName = lName;
        userName = uName;
        email = emailAddr;
        type = userType;
        password = pass;

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
    public String getUserName() {
        return userName;
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
        return this.type;
    }

    /**
     * Method for checking validity of a username (i.e that it is not empty or null, and that it
     * does not contain illegal characters)
     *
     * @param username the username you want to check
     * @return true if username is valid, false otherwise
     */
    public static boolean userNameIsValid(String username) {
        if (null == username || username.isEmpty()) { return false; }
        return !username.matches(ILLEGAL_USERNAME_CHARS_REGEX);
    }

    /**
     * Method for checking validity of a first or last name (i.e that it is not empty or null, and that it
     * does not contain illegal characters)
     *
     * @param name the username you want to check
     * @return true if name is valid, false otherwise
     */
    public static boolean nameIsValid(String name) {
        if (null == name || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_NAME_CHARS_REGEX);
    }

    /**
     * Method for checking if a given email is valid
     *
     * @param email the email address you want to check
     * @return true if email is valid, false otherwise
     */
    public static boolean emailIsValid(CharSequence email) {
        if (null == email || email.toString().isEmpty()) { return false; }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Method for checking if a given password is valid. Checks for empty or null inputs, as well as
     * illegal characters. Also ensures that the password does not contain the User's username and
     * that both password fields match.
     *
     * @param username the username associated with the password you are checking
     * @param password the input of the primary password field
     * @param confirmPassword the input of the secondary password field
     * @return true if the password is valid, false otherwise
     */
    public static PasswordValidationResult validatePassword(String username, String password, String confirmPassword) {
        if (null == password || password.isEmpty()) { return PasswordValidationResult.EMPTY; }
        if (null != username && username.equals("admin")) { return PasswordValidationResult.VALID; }
        if (password.length() < PASSWORD_MIN_LENGTH) { return PasswordValidationResult.TOO_SHORT; }
        if (null != username && password.toLowerCase().contains(username.toLowerCase())) { return PasswordValidationResult.CONTAINS_USERNAME; }
        for (String illegalPW: ILLEGAL_PASSWORDS) {
            if (password.toLowerCase().equals(illegalPW)) { return PasswordValidationResult.ILLEGAL_PASSWORD; }
        }
        if (!password.equals(confirmPassword)) { return PasswordValidationResult.CONFIRM_MISMATCH; }
        return PasswordValidationResult.VALID;
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
        return null != other && userName.equals(other.userName);
    }

    public static void getUser(final String username, final AsyncValueEventListener<User> listener) {
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
        getUser(username, new AsyncValueEventListener<User>() {
            @Override
            public void onSuccess(ArrayList<User> data) {
                User user = data.get(0);
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
        PasswordValidationResult passwordValRes = validatePassword(userName, password, password);
        if (PasswordValidationResult.VALID != passwordValRes) {
            throw new InvalidDataException("Invalid password. " + passwordValRes.toString());
        }
        this.password = password;
        update(this, listener);
    }

}
