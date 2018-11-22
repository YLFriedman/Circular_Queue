package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mindrot.jbcrypt.BCrypt;

import ca.uottawa.seg2105.project.cqondemand.database.DbUser;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class Authentication {

    protected static final int SALT_COMPLEXITY = 5;

    protected static String genSalt() {
        return BCrypt.gensalt(SALT_COMPLEXITY);
    }

    public static String genHash(@NonNull String password) {
        return BCrypt.hashpw(password, genSalt());
    }

    protected static String genHash(@NonNull String password, @NonNull String salt) {
        return BCrypt.hashpw(password, salt);
    }

    public static boolean checkPassword(@NonNull String passwordPlainText, @NonNull String passwordHashed) {
        return BCrypt.checkpw(passwordPlainText, passwordHashed);
    }

    /**
     * Callback method for authenticating a given set of user credentials through the database. Fails
     * if username does not exist or does not match store password value.
     *
     * @param username the username to be authenticated
     * @param password the password to be authenticated
     * @param listener the listener that will be informed if authentication was successful or not
     */
    public static void authenticate(@NonNull String username, @NonNull final String password, @Nullable final AsyncActionEventListener listener) {
        DbUser.getUserByUsername(username, new AsyncSingleValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull User user) {
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
