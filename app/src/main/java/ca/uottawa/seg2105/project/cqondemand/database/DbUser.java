package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.uottawa.seg2105.project.cqondemand.domain.User;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
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

    @NonNull
    public User toDomainObj() { return new User(first_name, last_name, username, email, User.parseType(type), password); }

    @NonNull
    public String generateKey() { return DbUtil.getSanitizedKey(username); }

    public static void createUser(@NonNull User user, @Nullable final AsyncActionEventListener listener) {
        DbUtil.createItem(user, listener);
    }

    public static void updateUser(@NonNull final User oldUser, @NonNull final User newUser, @Nullable final AsyncActionEventListener listener) {
        AsyncActionEventListener interceptListener = new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                // If we are updating the logged in user, replace the user object
                if (oldUser.equals(State.getState().getSignedInUser())) { State.getState().setSignedInUser(newUser); }
                if (null != listener) { listener.onSuccess(); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        };
        if (DbUtil.getKey(oldUser).equals(DbUtil.getKey(newUser))) {
            DbUtil.updateItem(newUser, interceptListener);
        } else {
            DbUtil.updateItem(oldUser, newUser, interceptListener);
        }
    }

    public static void deleteUser(@NonNull User user, @Nullable AsyncActionEventListener listener) {
        DbUtil.deleteItem(user, listener);
    }

    public static void getUser(@NonNull String username, @NonNull AsyncSingleValueEventListener<User> listener) {
        DbUtil.getItem(DbUtil.DataType.USER, username, listener);
    }

    public static void getUsers(@NonNull AsyncValueEventListener<User> listener) {
        DbUtil.getItems(DbUtil.DataType.USER, listener);
    }

    @NonNull
    public static DbListener<?> getUsersLive(@NonNull final AsyncValueEventListener<User> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.USER, listener);
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
        getUser(username, new AsyncSingleValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull User user) {
                if (user.getPassword().equals(password)) {
                    State.getState().setSignedInUser(user);
                    if (null != listener) { listener.onSuccess(); }
                } else {
                    if (null != listener) { listener.onFailure(AsyncEventFailureReason.PASSWORD_MISMATCH); }
                }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    public static void updatePassword(@NonNull User user, @NonNull String newPassword, @Nullable final AsyncActionEventListener listener) {
        User newUser = new User(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getType(), newPassword);
        updateUser(user, newUser, listener);
    }

}
