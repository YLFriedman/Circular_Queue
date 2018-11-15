package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.User;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class DbUser extends DbItem<User> {

    public String unique_name;
    public String first_name;
    public String last_name;
    public String username;
    public String email;
    public String password;
    public String type;

    public DbUser() {}

    DbUser(User item) {
        super(item.getKey());
        unique_name = item.getUniqueName();
        first_name = item.getFirstName();
        last_name = item.getLastName();
        username = item.getUsername();
        email = item.getEmail();
        password = item.getPassword();
        type = item.getType().toString();
    }

    @NonNull
    public User toDomainObj() { return new User(retrieveKey(), first_name, last_name, username, email, User.parseType(type), password); }

    public static void createUser(@NonNull final User user, @Nullable final AsyncActionEventListener listener) {
        getUserByUsername(user.getUsername(), new AsyncSingleValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull User item) {
                // Failure Condition: User already exists
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    // Success Condition: User does not exist
                    DbUtil.createItem(user, listener);
                } else if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    public static void updateUser(@NonNull final User user, @Nullable final AsyncActionEventListener listener) {
        if (null == user.getKey() || user.getKey().isEmpty()) { throw new IllegalArgumentException("A user object with a key is required. Unable to update the database without the key."); }
        DbUtil.updateItem(user, new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                // If we are updating the logged in user, replace the user object
                if (user.equals(State.getState().getSignedInUser())) { State.getState().setSignedInUser(user); }
                if (null != listener) { listener.onSuccess(); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    public static void deleteUser(@NonNull User user, @Nullable AsyncActionEventListener listener) {
        DbUtil.deleteItem(user, listener);
    }

    public static void getUser(@NonNull String key, @NonNull final AsyncSingleValueEventListener<User> listener) {
        DbUtil.getItem(DbUtil.DataType.USER, key, listener);
    }

    public static void getUserByUsername(@NonNull String username, @NonNull final AsyncSingleValueEventListener<User> listener) {
        DbUtil.getItems(DbUtil.DataType.USER, "unique_name", User.getUniqueName(username), new AsyncValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull ArrayList<User> data) {
                if (data.size() == 1) { listener.onSuccess(data.get(0)); }
                else if (data.size() == 0) { listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST); }
                else { listener.onFailure(AsyncEventFailureReason.NOT_UNIQUE); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) { listener.onFailure(reason); }
        });
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
        getUserByUsername(username, new AsyncSingleValueEventListener<User>() {
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

    public static void updatePassword(@NonNull User user, @NonNull String newPassword, @Nullable final AsyncSingleValueEventListener<User> listener) {
        final User newUser = new User(user.getKey(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getType(), newPassword);
        updateUser(newUser, new AsyncActionEventListener() {
            @Override
            public void onSuccess() { if (null != listener) { listener.onSuccess(newUser); } }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) { if (null != listener) { listener.onFailure(reason); } }
        });
    }

}
