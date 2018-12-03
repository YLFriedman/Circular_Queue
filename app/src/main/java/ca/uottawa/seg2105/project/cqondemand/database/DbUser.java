package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

/**
 * The class <b> DbUser </b> is meant to take a ServiceProvider or User object, and put it in a form more
 * easily stored in the database. Methods from moving between DbUser and User/ServiceProvider are provider, as
 * well as database read/write methods
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbUser extends DbItem<User> {

    /**
     * The unique name associated with a DbUser
     */
    public String unique_name;

    /**
     * The first name associated with a DbUser
     */
    public String first_name;

    /**
     * The last name associated with a DbUser
     */
    public String last_name;

    /**
     * The user name associated with a DbUser
     */
    public String username;

    /**
     * The email associated with a DbUser
     */
    public String email;

    /**
     * The encrypted password associated with a DbUser
     */
    public String password;

    /**
     * The type associated with a DbUser (Admin, Homeowner, or ServiceProvider)
     */
    public String type;

    /**
     * The address associated with a DbUser (service provider only)
     */
    public DbAddress address;

    /**
     * The licensed status associated with a DbUser (service provider only)
     */
    public Boolean licensed;

    /**
     * The phone number associated with a DbUser (service provider only)
     */
    public String phone_number;

    /**
     * The company name associated with a DbUser (service provider only)
     */
    public String company_name;

    /**
     * The description associated with a DbUser (service provider only)
     */
    public String description;

    /**
     * The overall rating associated with a DbUser (service provider only)
     */
    public Integer rating;

    /**
     * The running total of ratings associated with a DbUser (service provider only)
     */
    public Long running_rating_total;

    /**
     * The number of ratings associated with a DbUser (service provider only)
     */
    public Integer num_ratings;

    /**
     * The availability list  associated with a DbUser (service provider only)
     */
    public List<DbAvailability> availabilities;

    /**
     * Empty constructor for FireBase to create a DbUser object
     */
    public DbUser() {}

    /**
     * Constructor that creates a DbUser object based off of an input User object. If the input object
     * is also of type ServiceProvider, the relevant fields are added.
     *
     * @param item the User the new DbUser object will be based off of
     */
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
            licensed = provider.isLicensed();
            phone_number = provider.getPhoneNumber();
            company_name = provider.getCompanyName();
            description = provider.getDescription();
            rating = provider.getRating();
            running_rating_total = provider.getRunningRatingTotal();
            num_ratings = provider.getNumRatings();
            if (null == provider.getAvailabilities()) { availabilities = null; }
            else {
                availabilities = new LinkedList<DbAvailability>();
                for (Availability availability: provider.getAvailabilities()) { availabilities.add(new DbAvailability(availability)); }
            }
        }
    }

    /**
     * Method to create a User object based off of this DbUser object
     *
     * @return a User generated from this DbUser
     */
    @NonNull
    public User toDomainObj() {
        if (User.Type.parse(type) == User.Type.SERVICE_PROVIDER) {
            if (null == rating || null == running_rating_total || null == num_ratings) {
                throw new InvalidDataException("One of the number values is null in the database. Unable to create Service Provider object.");
            }
            if (null == address) { throw new IllegalArgumentException("The address cannot be null"); }
            List<Availability> availabilities = null;
            if (null != this.availabilities) {
                availabilities = new LinkedList<Availability>();
                for (DbAvailability availability: this.availabilities) { availabilities.add(availability.toDomainObj()); }
            }
            return new ServiceProvider(getKey(), first_name, last_name, username, email, password, company_name, licensed,
                    phone_number, address.toDomainObj(), description, rating, running_rating_total, num_ratings, availabilities);
        }
        return new User(getKey(), first_name, last_name, username, email, User.Type.parse(type), password);
    }

    /**
     * Adds a user to the database
     *
     * @param user the user to be added in the database
     * @param listener the listener that will handle the success/failure of this operation
     */
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

    /**
     * Updates a user's database information, does not include any other updates
     *
     * @param user the updated user
     * @param listener the listener that will handle the success/failure of this operations
     */
    public static void updateUser(@NonNull final User user, @Nullable final AsyncActionEventListener listener) {
        updateUser(user, null, listener);
    }

    /**
     * Updates a user's database information, and possibly updates other locations/objects
     *
     * @param user the updated user
     * @param otherUpdates a map of other updates to perform
     * @param listener the listener that will handle the success/failure of this operations
     */
    public static void updateUser(@NonNull final User user, @Nullable Map<String, Object> otherUpdates, @Nullable final AsyncActionEventListener listener) {
        if (null == user.getKey() || user.getKey().isEmpty()) { throw new IllegalArgumentException("A user object with a key is required. Unable to update the database without the key."); }
        final AsyncActionEventListener loggedInUserUpdateListener = new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                // If we are updating the logged in user, replace the user object
                if (user.equals(State.getInstance().getSignedInUser())) { State.getInstance().setSignedInUser(user); }
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
                if (user.getKey().equals(item.getKey())) { updateUserRelational(user, otherUpdates, loggedInUserUpdateListener); }
                else { loggedInUserUpdateListener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Success Condition: Username is not in use
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) { updateUserRelational(user, otherUpdates, loggedInUserUpdateListener); }
                else { loggedInUserUpdateListener.onFailure(reason); }
            }
        });
    }

    /**
     * Deletes a user from the database, updating all related locations to reflect this deletion
     *
     * @param user the user to be deleted
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void deleteUser(final @NonNull User user, final @Nullable AsyncActionEventListener listener) {
        if (null == user.getKey() || user.getKey().isEmpty()) { throw new IllegalArgumentException("A user object with a key is required. Unable to delete from the database without the key."); }
        final AsyncActionEventListener loggedInUserUpdateListener = new AsyncActionEventListener() {
            @Override
            public void onSuccess() {
                // If we are deleting the logged in user, remove the signed in user
                if (user.equals(State.getInstance().getSignedInUser())) { State.getInstance().setSignedInUser(null); }
                if (null != listener) { listener.onSuccess(); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (null != listener) { listener.onFailure(reason); }
            }
        };
        deleteUserRelational(user, loggedInUserUpdateListener);
    }

    /**
     * Retrieves a specific user from the database, based on user key
     * @param key the user key associated with the user to be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void getUser(@NonNull String key, @NonNull final AsyncSingleValueEventListener<User> listener) {
        DbUtil.getItem(DbUtil.DataType.USER, key, listener);
    }

    /**
     * Retrieves a specific user from the database, based on username
     * @param username the username associated with the user to be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void getUserByUsername(@NonNull String username, @NonNull final AsyncSingleValueEventListener<User> listener) {
        DbQuery query = DbQuery.createChildValueQuery("unique_name").setEqualsFilter(User.getUniqueName(username));
        DbUtil.getItems(DbUtil.DataType.USER, query, new AsyncValueEventListener<User>() {
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

    /**
     * Method to retrieve the full user list from the database, updating in real time as changes are made
     * to the database
     *
     * @param listener the listener that will handle the success/failure of this operation
     * @return a DbListenerHandle that will handle the ValueEventListener attached to the database
     */
    @NonNull
    public static DbListenerHandle<?> getUsersLive(@NonNull final AsyncValueEventListener<User> listener) {
        DbQuery query = DbQuery.createChildValueQuery("unique_name");
        return DbUtil.getItemsLive(DbUtil.DataType.USER, query, listener);
    }

    /**
     * Updates a users password, updating all relevant locations
     * @param user the user whose password will be updated
     * @param newPassword the new password
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void updatePassword(@NonNull User user, @NonNull String newPassword, @Nullable final AsyncSingleValueEventListener<User> listener) {
        final User newUser = new User(user.getKey(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getType(), newPassword);
        updateUser(newUser, new AsyncActionEventListener() {
            @Override
            public void onSuccess() { if (null != listener) { listener.onSuccess(newUser); } }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) { if (null != listener) { listener.onFailure(reason); } }
        });
    }

    /**
     * Private method to update a user in all the relational locations that user is stored.
     *
     * @param user the user to be updated
     * @param otherUpdates the update map that has been constructed thus far
     * @param listener the listener that will handle the success/failure of this operation
     */
    private static void updateUserRelational(@NonNull final User user, @Nullable Map<String, Object> otherUpdates, @Nullable final AsyncActionEventListener listener) {
        final String userKey = user.getKey();
        final DbUser updatedUser = new DbUser(user);
        final Map<String, Object> pathMap;
        if (null == otherUpdates) { pathMap = new HashMap<String, Object>(); }
        else { pathMap = otherUpdates; }
        // Add the primary path to the map
        pathMap.put(String.format("%s/%s", DbUtil.DataType.USER, userKey), updatedUser);
        buildBookingsUpdateMap(updatedUser, pathMap, new AsyncSingleValueEventListener<Map<String, Object>>() {
            @Override
            public void onSuccess(@NonNull Map<String, Object> bookingMap) {
                if (user instanceof  ServiceProvider) {
                    buildServiceUsersMap(updatedUser, bookingMap, new AsyncSingleValueEventListener<Map<String, Object>>() {
                        @Override
                        public void onSuccess(@NonNull Map<String, Object> bookingAndServiceMap) {
                            DbUtilRelational.multiPathUpdate(bookingAndServiceMap, listener);
                        }
                        @Override
                        public void onFailure(@NonNull AsyncEventFailureReason reason) {
                            listener.onFailure(reason);
                        }
                    });
                } else {
                    DbUtilRelational.multiPathUpdate(bookingMap, listener);
                }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                listener.onFailure(reason);
            }
        });
    }

    /**
     * Builds an update map for all the locations in the "service_users" node where a particular user is stored
     *
     * @param updatedUser the user to be updated
     * @param pathMap the update map constructed thus far
     * @param listener the listener that will handle the success/failure of this operation
     */
    private static void buildServiceUsersMap(DbUser updatedUser, Map<String, Object> pathMap, AsyncSingleValueEventListener<Map<String,Object>> listener){
        String userKey = updatedUser.getKey();
        DbUtilRelational.LookupType.SERVICE_USERS.getRef().child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String serviceKey = child.getKey();
                    String path = String.format("%s/%s/%s", DbUtilRelational.RelationType.SERVICE_USERS, serviceKey, userKey);
                    pathMap.put(path, updatedUser);
                }
                listener.onSuccess(pathMap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
            }
        });
    }

    /**
     * Builds an update map for all the locations in the "user_bookings" node that a particular user is stored
     *
     * @param updatedUser the user to be updated
     * @param map the update map constructed thus far
     * @param listener the listener that will handle the success/failure of this operation
     */
    private static void buildBookingsUpdateMap(DbUser updatedUser, Map<String, Object> map, AsyncSingleValueEventListener<Map<String, Object>> listener) {
        String userKey = updatedUser.getKey();
        String objectType;
        if (User.Type.parse(updatedUser.type) == User.Type.HOMEOWNER) { objectType = "homeowner"; }
        else { objectType = "service_provider"; }
        String lookupPath = String.format("%s/%s", DbUtilRelational.LookupType.USER_BOOKINGS, userKey);
        DbUtilRelational.getRef(lookupPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot assoc: dataSnapshot.getChildren()) {
                    for (DataSnapshot booking: assoc.getChildren()) {
                        String updatePath = String.format("%s/%s/%s/%s", DbUtilRelational.RelationType.USER_BOOKINGS, assoc.getKey(), booking.getKey(), objectType);
                        map.put(updatePath, updatedUser);
                    }
                }
                listener.onSuccess(map);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    /**
     * Deletes all the occurences of a user in relational nodes
     *
     * @param user the user to be deleted
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void deleteUserRelational(@NonNull User user, @Nullable final AsyncActionEventListener listener) {
        final String userKey = user.getKey();
        final Map<String, Object> pathMap = new HashMap<>();
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


    /**
     * Retrieves all the services associated with a specific user. Data is updated in real time every time
     * it is updated in the database
     * @param provider the provider whose services will be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     * @return a DbListenerHandle that will handle the ValueEventListener attached to the database
     */
    @NonNull
    public static DbListenerHandle<?> getServicesProvidedLive(@NonNull ServiceProvider provider, @NonNull AsyncValueEventListener<Service> listener) {
        if (provider.getKey() == null || provider.getKey().isEmpty()) {
            throw new IllegalArgumentException("A service provider object with a key is required. Unable to query the database without the key.");
        }
        DbQuery query = DbQuery.createChildValueQuery("name");
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.USER_SERVICES, provider.getKey(), query, listener);
    }

}
