package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class DbUtilRelational extends DbUtil {

    enum RelationType {
        SERVICE_USERS, USER_SERVICES;

        public String toString(){
            switch(this){
                case SERVICE_USERS: return "service_users";
                case USER_SERVICES: return "user_services";
                default: throw new IllegalArgumentException("Unrecognized type");
            }
        }

        public Class getDbItemClass(){
            switch(this){
                case SERVICE_USERS: return DbUser.class;
                case USER_SERVICES: return DbService.class;
                default: throw new IllegalArgumentException("Unrecognized type");
            }
        }

    }

    public static DatabaseReference getRef(RelationType relationRelationType){
        switch(relationRelationType){
            case SERVICE_USERS: return FirebaseDatabase.getInstance().getReference().child("service_users");
            case USER_SERVICES: return FirebaseDatabase.getInstance().getReference().child("user_services");
            default: throw new IllegalArgumentException("Unsupported Type");
        }
    }


    public static <T> void getItemsRelational(final RelationType relationRelationType, String childKey, final AsyncValueEventListener<T> listener) {
        getRef(relationRelationType).child(childKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<T> returnValue = new ArrayList<T>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {

                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(relationRelationType.getDbItemClass());
                        if (null != dbItem) {
                            T domainObjItem = dbItem.toDomainObj();
                            returnValue.add(domainObjItem);
                        }
                    }
                    catch (IllegalArgumentException e) { }
                    catch (InvalidDataException e) { }
                    catch (ClassCastException e) { }
                }
                listener.onSuccess(returnValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    public static void multiPathUpdate(HashMap<String, Object> pathMap, final AsyncActionEventListener listener ){
        FirebaseDatabase.getInstance().getReference().updateChildren(pathMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    listener.onSuccess();
                }
                else{
                    listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
                }
            }
        });
    }
}
