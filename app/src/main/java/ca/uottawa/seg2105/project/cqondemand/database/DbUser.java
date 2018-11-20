package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
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
    public DbAddress address;
    public boolean licensed;
    public String phone_number;
    public String company_name;
    public String description;

    public DbUser() {}

    public DbUser(User item) {
        super(item.getKey());
        unique_name = item.getUniqueName();
        first_name = item.getFirstName();
        last_name = item.getLastName();
        username = item.getUsername();
        email = item.getEmail();
        password = item.getPassword();
        type = item.getType().toString();
        if (item instanceof ServiceProvider) {
            ServiceProvider provider = (ServiceProvider) item;
            address = new DbAddress(provider.getAddress());
            company_name = provider.getCompanyName();
            phone_number = provider.getPhoneNumber();
            licensed = provider.isLicensed();
            description = provider.getDescription();
        }
    }

    @NonNull
    public User toDomainObj() {
        if (User.parseType(type) == User.Type.SERVICE_PROVIDER) {
            if (null == address) { throw new IllegalArgumentException("The address cannot be null"); }
            return new ServiceProvider(retrieveKey(), first_name, last_name, username, email, password, company_name, licensed, phone_number, address.toDomainObj(), description);
        }
        return new User(retrieveKey(), first_name, last_name, username, email, User.parseType(type), password);
    }

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
        final AsyncActionEventListener loggedInUserUpdateListener = new AsyncActionEventListener() {
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
        };
        // Check if the username is already in use
        getUserByUsername(user.getUsername(), new AsyncSingleValueEventListener<User>() {
            @Override
            public void onSuccess(@NonNull User item) {
                // Success Condition: The only user with this username is the user being updated
                if (user.getKey().equals(item.getKey())) { updateUserRelational(user, loggedInUserUpdateListener); }
                else { loggedInUserUpdateListener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Success Condition: Username is not in use
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) { updateUserRelational(user, loggedInUserUpdateListener); }
                else { loggedInUserUpdateListener.onFailure(reason); }
            }
        });
    }

    public static void deleteUser(@NonNull User user, @Nullable AsyncActionEventListener listener) {
        if (null == user.getKey() || user.getKey().isEmpty()) { throw new IllegalArgumentException("A user object with a key is required. Unable to delete from the database without the key."); }
        deleteUserRelational(user, listener);
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
    public static DbListenerHandle<?> getUsersLive(@NonNull final AsyncValueEventListener<User> listener) {
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

    public static void updateUserRelational(@NonNull final User user, @Nullable final AsyncActionEventListener listener) {
        final String userKey = user.getKey();
        final DbUser updatedUser = new DbUser(user);
        final HashMap<String, Object> pathMap = new HashMap<String, Object>();
        // Add the primary path to the map
        pathMap.put(String.format("%s/%s", DbUtil.DataType.USER, userKey), updatedUser);
        if (user instanceof  ServiceProvider) {
            DbUtilRelational.LookupType.SERVICE_USERS.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        String serviceKey = child.getKey();
                        String path = String.format("%s/%s/%s", DbUtilRelational.RelationType.SERVICE_USERS, serviceKey, userKey);
                        pathMap.put(path, updatedUser);
                    }
                    DbUtilRelational.multiPathUpdate(pathMap, listener);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (null != listener) { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                }
            });
        } else {
            DbUtilRelational.multiPathUpdate(pathMap, listener);
        }
    }

    public static void deleteUserRelational(@NonNull User user, @Nullable final AsyncActionEventListener listener) {
        final String userKey = user.getKey();
        final HashMap<String, Object> pathMap = new HashMap<>();
        final String path2 = "%s/%s";
        final String path3 = "%s/%s/%s";
        // Add the primary path to the map
        pathMap.put(String.format(path2, DbUtil.DataType.USER, userKey), null);
        // Get the paths for the users linked to services
        DbUtilRelational.LookupType.SERVICE_USERS.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    // Add the paths for each service that the user is nested under
                    String serviceKey = child.getKey();
                    pathMap.put(String.format(path3, DbUtilRelational.RelationType.SERVICE_USERS, serviceKey, userKey), null);
                    pathMap.put(String.format(path3, DbUtilRelational.LookupType.USER_SERVICES, serviceKey, userKey), null);
                }
                // Add the path for the list of services assigned to the user
                pathMap.put(String.format(path2, DbUtilRelational.RelationType.USER_SERVICES, userKey), null);
                pathMap.put(String.format(path2, DbUtilRelational.LookupType.SERVICE_USERS, userKey), null);
                DbUtilRelational.multiPathUpdate(pathMap, listener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
            }
        });
    }

    public static void getServicesProvided(@NonNull ServiceProvider provider, @NonNull AsyncValueEventListener<Service> listener) {
        if (provider.getKey() == null || provider.getKey().isEmpty()) {
            throw new IllegalArgumentException("A service provider object with a key is required. Unable to query the database without the key.");
        }
        DbUtilRelational.getItemsRelational(DbUtilRelational.RelationType.USER_SERVICES, provider.getKey(), listener);
    }

    @NonNull
    public static DbListenerHandle<?> getServicesProvidedLive(@NonNull ServiceProvider provider, @NonNull AsyncValueEventListener<Service> listener) {
        if (provider.getKey() == null || provider.getKey().isEmpty()) {
            throw new IllegalArgumentException("A service provider object with a key is required. Unable to query the database without the key.");
        }
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.USER_SERVICES, provider.getKey(), listener);
    }

}
