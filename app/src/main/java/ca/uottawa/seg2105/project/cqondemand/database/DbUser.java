package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.ValueEventListener;

import ca.uottawa.seg2105.project.cqondemand.domain.User;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class DbUser extends DbItem<User> {

    public String first_name;
    public String last_name;
    public String username;
    public String email;
    public String password;
    public String type;

    public DbUser() {}

    public DbUser(User user) {
        first_name = user.getFirstName();
        last_name = user.getLastName();
        username = user.getUsername();
        email = user.getEmail();
        password = user.getPassword();
        type = user.getType().toString();
    }

    public User toDomainObj() { return new User(first_name, last_name, username, email, User.parseType(type), password); }

    public String generateKey() { return DbUtil.getSanitizedKey(username); }

    public static DbListener<?> getUsersLive(final AsyncValueEventListener<User> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.USER, listener);
    }

    public static void createUser(User user, final AsyncActionEventListener listener) {
        DbUtil.createItem(user, listener);
    }

    public static void updateUser(final User oldUser, final User newUser, final AsyncActionEventListener listener) {
        AsyncActionEventListener interceptListener = new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                // If we are updating the logged in user, replace the user object
                if (State.getState().getSignedInUser() == oldUser) { State.getState().setSignedInUser(newUser); }
                listener.onSuccess();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                listener.onFailure(reason);
            }
        };
        if (DbUtil.getKey(oldUser).equals(DbUtil.getKey(newUser))) {
            DbUtil.updateItem(newUser, interceptListener);
        } else {
            DbUtil.updateItem(oldUser, newUser, interceptListener);
        }
    }

    public static void deleteUser(User user, final AsyncActionEventListener listener) {
        DbUtil.deleteItem(user, listener);
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
            public void onSuccess(@NonNull User user) {
                if (user.getPassword().equals(password)) {
                    State.getState().setSignedInUser(user);
                    listener.onSuccess();
                } else {
                    listener.onFailure(AsyncEventFailureReason.PASSWORD_MISMATCH);
                }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                listener.onFailure(reason);
            }
        });
    }

    public static void updatePassword(User oldUser, String password, final AsyncActionEventListener listener) {
        FieldValidation.PasswordValidationResult passwordValRes = FieldValidation.validatePassword(oldUser.getUsername(), password, password);
        if (FieldValidation.PasswordValidationResult.VALID != passwordValRes) {
            throw new InvalidDataException("Invalid password. " + passwordValRes.toString());
        }
        User updatedUser = new User(oldUser.getFirstName(), oldUser.getLastName(), oldUser.getUsername(),
                oldUser.getEmail(), oldUser.getType(), password);
        updateUser(oldUser, updatedUser, listener);
    }

}
