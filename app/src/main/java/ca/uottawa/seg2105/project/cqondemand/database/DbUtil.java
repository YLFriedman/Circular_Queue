package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * This class is a utility class for interfacing with the FireBase real-time database. It defines
 * database classes which are adapted from the classes in the domain package.
 *
 */

public class DbUtil {

    /**
     * Enum for differentiating between different object types
     */
    public enum DataType {
        USER, SERVICE, CATEGORY;
        public String toString() {
            switch (this) {
                case USER: return "users";
                case SERVICE: return "services";
                case CATEGORY: return "categories";
                default: return this.name();
            }
        }
    }

    /**
     * Method for creating a DbItem adapted from a domain object. The input object must be of type
     * Service, User or Category. Throws an IllegalArgumentException if passed any other type of object
     *
     * @param object The object to be adapted
     * @return A DbItem adaptation of the input object
     */
    protected static DbItem<?> objectToDbItem(Object object) {
        if (null == object) { throw new IllegalArgumentException("The object cannot be null."); }
        if (object instanceof User) { return new DbUser((User) object); }
        if (object instanceof Service) { return new DbService((Service) object); }
        if (object instanceof Category) { return new DbCategory((Category) object); }
        throw new IllegalArgumentException("Unsupported type.");
    }

    /**
     * Returns the datatype of a given domain object. Accepts objects of type Service, User, and Category.
     * Throws an IllegalArgumentException if passed any other type of object.
     * @param object
     * @return
     */
    protected static DataType getType(Object object) {
        if (null == object) { throw new IllegalArgumentException("The object cannot be null."); }
        if (object instanceof User) { return DataType.USER; }
        if (object instanceof Service) { return DataType.SERVICE; }
        if (object instanceof Category) { return DataType.CATEGORY; }
        throw new IllegalArgumentException("Unsupported type.");
    }

    /**
     * Method for obtaining the database key for a specific object. Accepts objects of type Service, User, and Category.
     * Throws an IllegalArgumentException if passed any other type of object.
     * @param object The object whose key you want
     * @return A string representation of the database key
     */
    public static String getKey(Object object) {
        if (null == object) { throw new IllegalArgumentException("The object cannot be null."); }
        if (object instanceof User) { return new DbUser((User) object).generateKey(); }
        if (object instanceof Service) { return new DbService((Service) object).generateKey(); }
        if (object instanceof Category) { return new DbCategory((Category) object).generateKey(); }
        throw new IllegalArgumentException("Unsupported type.");
    }

    /**
     * Method for returning the class of a specific type of object
     *
     * @param type the type of object whose class you want
     * @return the class of the specified datatype
     */
    protected static Class getDbClassObj(DataType type) {
        if (null == type) { throw new IllegalArgumentException("The type cannot be null."); }
        switch (type) {
            case USER: return DbUser.class;
            case SERVICE: return DbService.class;
            case CATEGORY: return DbCategory.class;
            default: return null;
        }
    }

    /**
     * Method for getting a specific DatabaseReference, based on the input type
     *
     * @param type the type of DbItem
     * @return A DatabaseReference pointing to the node which corresponds to the input type
     */

    protected static DatabaseReference getRef(DataType type) {
        if (null == type) { throw new IllegalArgumentException("The type cannot be null."); }
        return FirebaseDatabase.getInstance().getReference().child(type.toString());
    }

    /**
     * Method for returning a database-ready key from a specific String
     * @param uniqueID the String representation of a uniqueID
     * @return A sanitized, database-ready String version of the input key
     */
    public static String getSanitizedKey(String uniqueID) {
        uniqueID = uniqueID.toLowerCase();
        uniqueID = uniqueID.replaceAll("[\\s]", "_");
        uniqueID = uniqueID.replaceAll("[^a-z0-9_]", "_");
        return uniqueID;
    }


    @SuppressWarnings("unchecked")
    public static <T> void getItem(final DataType type, String key, final AsyncValueEventListener<T> listener) {
        if (null == type) { throw new IllegalArgumentException("The type cannot be null."); }
        if (null == key) { throw new IllegalArgumentException("The key cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        getRef(type).child(getSanitizedKey(key)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST);
                } else {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) dataSnapshot.getValue(getDbClassObj(type));
                        T domainObjItem = dbItem.toItem();
                        ArrayList<T> returnValue = new ArrayList<T>(1);
                        if (null == domainObjItem) {
                            listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                            return;
                        }
                        returnValue.add(domainObjItem);
                        listener.onSuccess(returnValue);
                    } catch (IllegalArgumentException e) {
                        listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                    } catch (InvalidDataException e) {
                        listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                    } catch (ClassCastException e) {
                        listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }


    public static <T> void getItems(final DataType type, final AsyncValueEventListener<T> listener) {
        if (null == type) { throw new IllegalArgumentException("The type cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        getRef(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(getDbClassObj(type));
                        T domainObjItem = dbItem.toItem();
                        if (null != dbItem) { returnValue.add(domainObjItem); }
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

    public static <T> void getItems(final DataType type, String childKey, String childValue, final AsyncValueEventListener<T> listener) {
        Query query = getRef(type).orderByChild(childKey).equalTo(childValue);
        getItems(type, query, listener);
    }

    public static <T> void getItems(final DataType type, String childKey, int childValue, final AsyncValueEventListener<T> listener) {
        Query query = getRef(type).orderByChild(childKey).equalTo(childValue);
        getItems(type, query, listener);
    }

    protected static <T> void getItems(final DataType type, Query query, final AsyncValueEventListener<T> listener) {
        if (null == type) { throw new IllegalArgumentException("The type cannot be null."); }
        if (null == query) { throw new IllegalArgumentException("The query cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(getDbClassObj(type));
                        T domainObjItem = dbItem.toItem();
                        if (null != dbItem) { returnValue.add(domainObjItem); }
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

    public static <T> void deleteItem(T item, final AsyncActionEventListener listener) {
        if (null == item) { throw new IllegalArgumentException("The item cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        final DataType type = getType(item);
        if (null == type) { throw new IllegalArgumentException("The item must be a supported type."); }
        final DbItem<?> dbItem = objectToDbItem(item);
        getRef(type).child(dbItem.generateKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onSuccess();
                } else {
                    listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> void createItem(T item, final AsyncActionEventListener listener) {
        if (null == item) { throw new IllegalArgumentException("The item cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        final DataType type = getType(item);
        if (null == type) { throw new IllegalArgumentException("The item must be a supported type."); }
        final DbItem<?> dbItem = objectToDbItem(item);
        final String key = dbItem.generateKey();
        getItem(type, key, new AsyncValueEventListener<T>() {
            @Override
            public void onSuccess(ArrayList<T> data) {
                //Failure condition, Item already exists in db
                listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS);
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                //Success condition, item can be safely created;
                if (reason == AsyncEventFailureReason.DOES_NOT_EXIST) {
                    getRef(type).child(key).setValue(dbItem, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (null == databaseError) { listener.onSuccess(); }
                            else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                        }
                    });
                } else {
                    listener.onFailure(reason);
                }
            }
        });
    }

    public static <T> void updateItem(T item, final AsyncActionEventListener listener) {
        if (null == item) { throw new IllegalArgumentException("The item cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        final DataType type = getType(item);
        if (null == type) { throw new IllegalArgumentException("The item must be a supported type."); }
        final DbItem<?> dbItem = objectToDbItem(item);
        final String key = dbItem.generateKey();
        getItem(type, key, new AsyncValueEventListener<T>() {
            @Override
            public void onSuccess(ArrayList<T> data) {
                // Success condition: Item exists in database and can be updated
                DatabaseReference ref = getRef(type);
                ref.child(key).setValue(dbItem, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (null == databaseError) { listener.onSuccess(); }
                        else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                    }
                });
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                // Failure condition: cannot update an item that doesn't currently exist in the db
                listener.onFailure(reason);
            }
        });
    }

    public static <T> void updateItem(final T oldItem, final T newItem, final AsyncActionEventListener listener) {
        if (null == oldItem) { throw new IllegalArgumentException("The oldItem cannot be null."); }
        if (null == newItem) { throw new IllegalArgumentException("The newItem cannot be null."); }
        if (null == listener) { throw new IllegalArgumentException("The listener cannot be null."); }
        final DataType type = getType(oldItem);
        final DataType newType = getType(newItem);
        if (null == type || null == newType) { throw new IllegalArgumentException("The item must be a supported type."); }
        if (type != newType) { throw new IllegalArgumentException("The items must be of the same type."); }
        final DbItem<?> dbItem = objectToDbItem(newItem);
        final String key = dbItem.generateKey();
        getItem(type, key, new AsyncValueEventListener<T>() {
            @Override
            public void onSuccess(ArrayList<T> data) {
                // Error condition: The new item already exists
                listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS);
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    deleteItem(oldItem, new AsyncActionEventListener() {
                        @Override
                        public void onSuccess() {
                            createItem(newItem, listener);
                        }
                        @Override
                        public void onFailure(AsyncEventFailureReason reason) {
                            listener.onFailure(reason);
                        }
                    });
                } else {
                    listener.onFailure(reason);
                }
            }
        });
    }

}
