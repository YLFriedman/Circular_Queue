package ca.uottawa.seg2105.project.cqondemand;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseUtil {

    private static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference dbUsers = db.child("users");

    private static User currentUser = null;



    public enum CallbackFailure{
        ALREADY_EXISTS, DATABASE_ERROR, DOES_NOT_EXIST, PASSWORD_MISMATCH;
    }

    public static void authenticate(final String username,final String password,final UserEventListener listener){

        DatabaseReference userRef = dbUsers.child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    listener.onFailure(CallbackFailure.DOES_NOT_EXIST);
                }
                else{
                    String realPassword = (String) dataSnapshot.child("password").getValue();
                    if(password.equals(realPassword)){
                        String firstName = (String) dataSnapshot.child("first_name").getValue();
                        String lastName = (String) dataSnapshot.child("last_name").getValue();
                        String email = (String) dataSnapshot.child("email").getValue();
                        String typeStr = (String) dataSnapshot.child("type").getValue();
                        User.Types type = User.parseType(typeStr);

                        User.setCurrentUser(new User(firstName, lastName, username, email, type, password));
                        listener.onSuccess();
                    }
                    else{
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

    public static void exists(final String username, final UserEventListener listener){

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

    public static void createUser(final User user,final String password,final UserEventListener listener) {
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
                        dbUsers.child(username).child("password").setValue(password);
                        listener.onSuccess();
                    } catch (DatabaseException e) {
                        listener.onFailure(CallbackFailure.DATABASE_ERROR);

                    }

                }
                else{
                    listener.onFailure(CallbackFailure.DATABASE_ERROR);
                }
            }
        };
        exists(user.getUserName(), existListener);
    }

    public static void update(User user, UserEventListener listener){
        String username = user.getUserName();

        dbUsers.child(username).child("type").setValue(user.getType().toString());
        dbUsers.child(username).child("first_name").setValue(user.getFirstName());
        dbUsers.child(username).child("last_name").setValue(user.getLastName());
        dbUsers.child(username).child("email").setValue(user.getEmail());
        dbUsers.child(username).child("password").setValue(user.getPassword());

        if(!User.getCurrentUser().getUserName().equals(username)){
            dbUsers.child(User.getCurrentUser().getUserName()).removeValue();

        }
        User.setCurrentUser(user);
        listener.onSuccess();


    }

    public static void delete(String username, UserEventListener listener){
        
    }



}
