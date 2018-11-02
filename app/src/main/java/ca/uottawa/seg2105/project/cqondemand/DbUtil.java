package ca.uottawa.seg2105.project.cqondemand;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * This class is a utility class for interfacing with the FireBase real-time database. It also allows
 * for the storage and retrieval of a static currentUser variable to represent the currently logged in
 * User. As many methods in this class involve callbacks, and enum CallbackFailure is included as well.
 */

public class DbUtil {

    private static DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("users");
    private static DatabaseReference dbServices = FirebaseDatabase.getInstance().getReference().child("services");

    private static User currentUser = null;

    /**
     * Callback method for authenticating a given set of user credentials through the database. Fails
     * if username does not exist or does not match store password value.
     *
     * @param username the username to be authenticated
     * @param password the password to be authenticated
     * @param listener the listener that will be informed if authentication was successful or not
     */
    public static void authenticate(final String username, final String password, final DbActionEventListener listener) {

        DatabaseReference userRef = dbUsers.child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(DbEventFailureReason.DOES_NOT_EXIST);
                } else {
                    String realPassword = (String) dataSnapshot.child("password").getValue();
                    if (password.equals(realPassword)) {
                        String firstName = (String) dataSnapshot.child("first_name").getValue();
                        String lastName = (String) dataSnapshot.child("last_name").getValue();
                        String email = (String) dataSnapshot.child("email").getValue();
                        String typeStr = (String) dataSnapshot.child("type").getValue();
                        User.Types type = User.parseType(typeStr);
                        try {
                            State.getState().setCurrentUser(new User(firstName, lastName, username, email, type, password));
                            listener.onSuccess();
                        } catch (IllegalArgumentException e) {
                            listener.onFailure(DbEventFailureReason.BAD_USER);
                        }

                    } else {
                        listener.onFailure(DbEventFailureReason.PASSWORD_MISMATCH);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(DbEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    /**
     * Callback method for ensuring that a given account exists. Fails if account does not exist.
     *
     * @throws IllegalArgumentException if the username or listener are null
     * @param username the username you wish to check
     * @param listener the listener that will respond to the success/failure of the existence check
     */
    public static void getUser(final String username, final DbValueEventListener listener) {
        if (null == username) { throw new IllegalArgumentException("The username cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        DatabaseReference userRef = dbUsers.child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(DbEventFailureReason.DOES_NOT_EXIST);
                } else {
                    String firstName = (String) dataSnapshot.child("first_name").getValue();
                    String lastName = (String) dataSnapshot.child("last_name").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String password = (String) dataSnapshot.child("password").getValue();
                    try {
                        User.Types type = User.parseType((String) dataSnapshot.child("type").getValue());
                        ArrayList<User> userList = new ArrayList<User>(1);
                        userList.add(new User(firstName, lastName, username, email, type, password));
                        listener.onSuccess(userList);
                    } catch (IllegalArgumentException e) {
                        listener.onFailure(DbEventFailureReason.BAD_USER);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(DbEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    /**
     * Simple callback method for getting a list of all the system users
     *
     * @param listener the listener to handle the user list.
     */
    public static void getUsers(final DbValueEventListener<User> listener){
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<User> userList = new ArrayList<User>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String firstName = (String) userSnapshot.child("first_name").getValue();
                    String lastName = (String) userSnapshot.child("last_name").getValue();
                    String email = (String) userSnapshot.child("email").getValue();
                    String password = (String) userSnapshot.child("password").getValue();
                    String username = userSnapshot.getKey();
                    try {
                        User.Types type = User.parseType((String) userSnapshot.child("type").getValue());
                        userList.add(new User(firstName, lastName, username, email, type, password));
                    } catch (IllegalArgumentException e) { }
                }
                listener.onSuccess(userList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(DbEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    /**
     * Callback method to attempt to create a user in the database. Fails if username is already taken
     *
     * @throws IllegalArgumentException if the user is null
     * @param user the User object you wish to save to the database
     * @param listener the listener that will respond to the creation success or failure
     */
    public static void createUser(final User user, @Nullable final DbActionEventListener listener) {
        if (null == user) { throw new IllegalArgumentException("The user cannot be null."); }
        getUser(user.getUserName(), new DbValueEventListener<User>() {
            @Override
            public void onSuccess(ArrayList<User> data) {
                // Failure condition: User already exists
                if (null != listener) { listener.onFailure(DbEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(DbEventFailureReason reason) {
                if (reason == DbEventFailureReason.DOES_NOT_EXIST) {
                    // Success condition: User does not exist, can be created
                    String username = user.getUserName();
                    try {
                        dbUsers.child(username).child("type").setValue(user.getType().toString());
                        dbUsers.child(username).child("first_name").setValue(user.getFirstName());
                        dbUsers.child(username).child("last_name").setValue(user.getLastName());
                        dbUsers.child(username).child("email").setValue(user.getEmail());
                        dbUsers.child(username).child("password").setValue(user.getPassword());
                        if (null != listener) { listener.onSuccess(); }
                    } catch (DatabaseException e) {
                        if (null != listener) { listener.onFailure(DbEventFailureReason.DATABASE_ERROR); }
                    }
                } else {
                    if (null != listener) { listener.onFailure(DbEventFailureReason.DATABASE_ERROR); }
                }
            }
        });
    }

    /**
     * Callback method for updating a user's information.
     *
     * @throws IllegalArgumentException if the username or user are null
     * @param username the user to be updated.
     * @param user the new details to be applied to the user.
     * @param listener the listener to be notified of the success/failure of the user update.
     */
    public static void updateUser(final String username, final User user, @Nullable final DbActionEventListener listener) {
        if (null == username) { throw new IllegalArgumentException("The username cannot be null."); }
        if (null == user) { throw new IllegalArgumentException("The user cannot be null."); }
        if (user.getUserName().equals(username)) {
            // If the username is not changing, update the existing node
            try {
                dbUsers.child(username).child("type").setValue(user.getType().toString());
                dbUsers.child(username).child("first_name").setValue(user.getFirstName());
                dbUsers.child(username).child("last_name").setValue(user.getLastName());
                dbUsers.child(username).child("email").setValue(user.getEmail());
                dbUsers.child(username).child("password").setValue(user.getPassword());
                State.getState().setCurrentUser(user);
                if (null != listener) { listener.onSuccess(); }
            } catch (DatabaseException e) {
                if (null != listener) { listener.onFailure(DbEventFailureReason.DATABASE_ERROR); }
            }
        } else {
            // If the username is changing, use the createUser method, which checks if the new username exists
            createUser(user, new DbActionEventListener() {
                @Override
                public void onSuccess() {
                    // Remove the old user from the DB
                    dbUsers.child(State.getState().getCurrentUser().getUserName()).removeValue();
                    // Update the app's current user
                    State.getState().setCurrentUser(user);
                    if (null != listener) { listener.onSuccess(); }
                }
                @Override
                public void onFailure(DbEventFailureReason reason) {
                    if (null != listener) { listener.onFailure(reason); }
                }
            });
        }
    }

    /**
     * A callback method specifically for updating a user's password.
     *
     * @param user the user who's password will be changed
     * @param listener the listener to handle the outcome of the password change
     */
    public static void updateUserPassword(final User user, final DbActionEventListener listener) {
        DatabaseReference.CompletionListener complete = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    currentUser.setPassword(user.getPassword());
                    listener.onSuccess();
                } else {
                    listener.onFailure(DbEventFailureReason.DATABASE_ERROR);
                }
            }
        };
        try {
            dbUsers.child(user.getUserName()).child("password").setValue(user.getPassword(), complete);
        } catch (DatabaseException e) {
            listener.onFailure(DbEventFailureReason.DATABASE_ERROR);
        }

    }

    /**
     * Callback method for deleting a user from the database
     *
     * @param username the username attached to the account to be deleted.
     * @param listener the listener that will handle the outcome of the deletion.
     */
    public static void deleteUser(String username, final DbActionEventListener listener) {
        DatabaseReference.CompletionListener complete = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onSuccess();
                } else {
                    listener.onFailure(DbEventFailureReason.DATABASE_ERROR);
                }
            }
        };
        dbUsers.child(username).removeValue(complete);
    }

    public static void createService(final Service service, final DbActionEventListener listener){
        if(null == service) throw new IllegalArgumentException("The service cannot be null");
        if(null == listener) throw new IllegalArgumentException("Listener must not be null");
        getService(service.getName(), new DbValueEventListener<Service>() {
            @Override
            public void onSuccess(ArrayList<Service> data) {
                //Failure condition, service already exists
                listener.onFailure(DbEventFailureReason.ALREADY_EXISTS);

            }

            @Override
            public void onFailure(DbEventFailureReason reason) {
                //Success Condition, service can be created
                String serviceId = service.getName().toLowerCase();
                try{
                    dbServices.child(serviceId).setValue(service);
                    listener.onSuccess();
                }
                catch(DatabaseException e){
                    listener.onFailure(DbEventFailureReason.DATABASE_ERROR);

                }

            }
        });

    }

    public static void getService(String name, final DbValueEventListener<Service> listener){
        if(null == name) throw new IllegalArgumentException("The name cannot be null");
        if(null == listener) throw new IllegalArgumentException("Listener must not be null");
        dbServices.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    listener.onFailure(DbEventFailureReason.DOES_NOT_EXIST);
                }
                else{
                    Service retrievedService = dataSnapshot.getValue(Service.class);
                    ArrayList<Service> returnValue = new ArrayList<>();
                    returnValue.add(retrievedService);
                    listener.onSuccess(returnValue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                listener.onFailure(DbEventFailureReason.DATABASE_ERROR);

            }
        });

    }


}
