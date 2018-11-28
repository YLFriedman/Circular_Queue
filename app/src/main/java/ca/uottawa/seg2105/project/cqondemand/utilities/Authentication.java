package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mindrot.jbcrypt.BCrypt;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

/**
 * The class <b>Authentication</b> is used to manage password hashing and authentication during the
 * login process. The passwords are hashed using the jBCrypt library.  The passwords are checked
 * using the jBCrypt utility methods.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class Authentication {

    /**
     * Stores the default complexity to be used when hashing a password.
     */
    private static final int SALT_COMPLEXITY = 5;

    /**
     * Generates a salt with the complexity value of SALT_COMPLEXITY
     * @return the generated salt
     */
    private static String genSalt() {
        return BCrypt.gensalt(SALT_COMPLEXITY);
    }

    /**
     * Generates a hashed form of the given password using the default salt generator.
     * @param password the plain text password to be hashed
     * @return the hashed form of the given password
     */
    public static String genHash(@NonNull String password) {
        return BCrypt.hashpw(password, genSalt());
    }

    /**
     * Generates a hashed form of the given password using the provided salt.
     * @param password the plain text password to be hashed
     * @param salt the salt to be used with the password (guards against dictionary attacks)
     * @return the hashed form of the given password
     */
    private static String genHash(@NonNull String password, @NonNull String salt) {
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Tests the equivalence of the plain text password and the hashed password.
     * @param passwordPlainText the plain text password to be tested
     * @param passwordHashed the hashed password to be tested against
     * @return true if the plain text password is equivalent to the hashed password, false otherwise
     */
    public static boolean checkPassword(@NonNull String passwordPlainText, @NonNull String passwordHashed) {
        return BCrypt.checkpw(passwordPlainText, passwordHashed);
    }

    /**
     * Callback method for authenticating a given set of user credentials through the database. Fails
     * if username does not exist or the given password does not match the stored value.
     * @param username the username to be authenticated
     * @param password the password to be authenticated
     * @param listener the listener that will be informed if authentication was successful or not
     */
    public static void authenticate(@NonNull String username, @NonNull final String password, @Nullable final AsyncActionEventListener listener) {
        DbUser.getUserByUsername(username, new AsyncSingleValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull User user) {
                // Test for an IllegalArgumentException which could be thrown by jBCrypt if the password is invalid
                try {
                    if (checkPassword(password, user.getPassword())) {
                        State.getInstance().setSignedInUser(user);
                        if (null != listener) { listener.onSuccess(); }
                    } else {
                        if (null != listener) { listener.onFailure(AsyncEventFailureReason.PASSWORD_MISMATCH); }
                    }
                } catch (IllegalArgumentException e) {
                    if (null != listener) { listener.onFailure(AsyncEventFailureReason.PASSWORD_MISMATCH); }
                }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

}
