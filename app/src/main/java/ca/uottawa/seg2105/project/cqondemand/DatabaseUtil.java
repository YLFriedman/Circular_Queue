package ca.uottawa.seg2105.project.cqondemand;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This class is a utility class for interfacing with the FireBase real-time database. It also allows
 * for the storage and retrieval of a static currentUser variable to represent the currently logged in
 * User. As many methods in this class involve callbacks, and enum CallbackFailure is included as well.
 */

public class DatabaseUtil {

    private static DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("users");

    private static User currentUser = null;

    /**
     * Getter for the currentUser varaiable
     * @return the currently signed in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the currently signed in user. Null inputs are valid.
     * @param user the currently signed in user
     */

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Enum for providing context to callback failures
     */
    public enum CallbackFailure {
        ALREADY_EXISTS, DATABASE_ERROR, DOES_NOT_EXIST, PASSWORD_MISMATCH;
    }

    /**
     * Callback method for authenticating a given set of user credentials through the database. Fails
     * if username does not exist or does not match store password value.
     *
     * @param username the username to be authenticated
     * @param password the password to be authenticated
     * @param listener the listener that will be informed if authentication was successful or not
     */
    public static void authenticate(final String username, final String password, final UserEventListener listener) {

        DatabaseReference userRef = dbUsers.child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(CallbackFailure.DOES_NOT_EXIST);
                } else {
                    String realPassword = (String) dataSnapshot.child("password").getValue();
                    if (password.equals(realPassword)) {
                        String firstName = (String) dataSnapshot.child("first_name").getValue();
                        String lastName = (String) dataSnapshot.child("last_name").getValue();
                        String email = (String) dataSnapshot.child("email").getValue();
                        String typeStr = (String) dataSnapshot.child("type").getValue();
                        User.Types type = User.parseType(typeStr);
                        setCurrentUser(new User(firstName, lastName, username, email, type, password));
                        listener.onSuccess();
                    } else {
                        listener.onFailure(CallbackFailure.PASSWORD_MISMATCH);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(CallbackFailure.DATABASE_ERROR);
            }
        });
    }

    /**
     * Callback method for ensuring that a given account exists. Fails if account does not exist.
     *
     * @param username the username you wish to check
     * @param listener the listener that will respond to the success/failure of the existence check
     */

    public static void userExists(final String username, final UserEventListener listener) {
        DatabaseReference userRef = dbUsers.child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(CallbackFailure.DOES_NOT_EXIST);
                } else {
                    listener.onSuccess();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(CallbackFailure.DATABASE_ERROR);
            }
        });
    }

    /**
     * Callback method to attempt to create a user in the database. Fails if username is already taken
     *
     * @param user the User object you wish to save to the database
     * @param listener the listener that will respond to the creation success or failure
     */

    public static void createUser(final User user,final UserEventListener listener) {
        UserEventListener existListener = new UserEventListener() {
            @Override
            public void onSuccess() {
                listener.onFailure(CallbackFailure.ALREADY_EXISTS);
            }
            @Override
            public void onFailure(CallbackFailure reason) {
                if (reason == CallbackFailure.DOES_NOT_EXIST) {
                    String username = user.getUserName();
                    try {
                        dbUsers.child(username).child("type").setValue(user.getType().toString());
                        dbUsers.child(username).child("first_name").setValue(user.getFirstName());
                        dbUsers.child(username).child("last_name").setValue(user.getLastName());
                        dbUsers.child(username).child("email").setValue(user.getEmail());
                        dbUsers.child(username).child("password").setValue(user.getPassword());
                        listener.onSuccess();
                    } catch (DatabaseException e) {
                        listener.onFailure(CallbackFailure.DATABASE_ERROR);
                    }
                } else {
                    listener.onFailure(CallbackFailure.DATABASE_ERROR);
                }
            }
        };
        userExists(user.getUserName(), existListener);
    }

    /**
     * Callback method for updating a user's information.
     *
     * @param user the user to be updated.
     * @param listener the listener to be notified of the success/failure of the user update.
     */
    public static void updateUser(final User user, final UserEventListener listener) {
        final String username = user.getUserName();
        if (getCurrentUser().getUserName().equals(username)) {
            // If the username is not changing, update the existing node
            try {
                dbUsers.child(username).child("type").setValue(user.getType().toString());
                dbUsers.child(username).child("first_name").setValue(user.getFirstName());
                dbUsers.child(username).child("last_name").setValue(user.getLastName());
                dbUsers.child(username).child("email").setValue(user.getEmail());
                dbUsers.child(username).child("password").setValue(user.getPassword());
                setCurrentUser(user);
                listener.onSuccess();
            } catch (DatabaseException e) {
                listener.onFailure(CallbackFailure.DATABASE_ERROR);
            }
        } else {
            // If the username is changing, use the createUser method, which checks if the new username exists
            createUser(user, new UserEventListener() {
                @Override
                public void onSuccess() {
                    // Remove the old user from the DB
                    dbUsers.child(getCurrentUser().getUserName()).removeValue();
                    // Update the app's current user
                    setCurrentUser(user);
                    listener.onSuccess();
                }
                @Override
                public void onFailure(CallbackFailure reason) {
                    listener.onFailure(reason);
                }
            });
        }
    }

    /**
     * Simple callback method for getting a list of all the system users
     *
     * @param listener the listener to handle the user list.
     */

    public static void getUserList(ValueEventListener listener){
        dbUsers.addListenerForSingleValueEvent(listener);
    }

    /**
     * A callback method specifically for updating a user's password.
     *
     * @param user the user who's password will be changed
     * @param listener the listener to handle the outcome of the password change
     */

    public static void updateUserPassword(final User user, final UserEventListener listener) {
        DatabaseReference.CompletionListener complete = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    currentUser.setPassword(user.getPassword());
                    listener.onSuccess();
                } else {
                    listener.onFailure(CallbackFailure.DATABASE_ERROR);
                }
            }
        };
        try {
            dbUsers.child(user.getUserName()).child("password").setValue(user.getPassword(), complete);
        } catch (DatabaseException e) {
            listener.onFailure(CallbackFailure.DATABASE_ERROR);
        }

    }

    /**
     * Callback method for deleting a user from the database
     *
     * @param username the username attached to the account to be deleted.
     * @param listener the listener that will handle the outcome of the deletion.
     */

    public static void deleteUser(String username, final UserEventListener listener) {
        DatabaseReference.CompletionListener complete = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onSuccess();
                } else {
                    listener.onFailure(CallbackFailure.DATABASE_ERROR);
                }
            }
        };
        dbUsers.child(username).removeValue(complete);
    }

}
