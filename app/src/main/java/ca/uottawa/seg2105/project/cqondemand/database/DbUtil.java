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
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
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
    enum DataType {
        USER, SERVICE, CATEGORY, USER_SERVICES, SERVICE_USERS;
        @NonNull
        public String toString() {
            switch (this) {
                case USER: return "users";
                case SERVICE: return "services";
                case CATEGORY: return "categories";
                case USER_SERVICES: return "user_services";
                case SERVICE_USERS: return "service_users";
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
    @NonNull
    private static DbItem<?> objectToDbItem(@NonNull Object object) {
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
    @NonNull
    private static DataType getType(@NonNull Object object) {
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
    @NonNull
    public static String getKey(@NonNull Object object) {
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
    @NonNull
    private static Class getDbClassObj(@NonNull DataType type) {
        switch (type) {
            case USER: return DbUser.class;
            case SERVICE: return DbService.class;
            case CATEGORY: return DbCategory.class;
            default: throw new UnsupportedOperationException("The type is unsupported by this method.");
        }
    }

    /**
     * Method for getting a specific DatabaseReference, based on the input type
     *
     * @param type the type of DbItem
     * @return A DatabaseReference pointing to the node which corresponds to the input type
     */
    @NonNull
    private static DatabaseReference getRef(@NonNull DataType type) {
        return FirebaseDatabase.getInstance().getReference().child(type.toString());
    }

    /**
     * Method for returning a database-ready key from a specific String
     * @param uniqueID the String representation of a uniqueID
     * @return A sanitized, database-ready String version of the input key
     */
    @NonNull
    public static String getSanitizedKey(@NonNull String uniqueID) {
        uniqueID = uniqueID.toLowerCase();
        uniqueID = uniqueID.replaceAll("[\\s]", "_");
        uniqueID = uniqueID.replaceAll("[^a-z0-9_]", "_");
        return uniqueID;
    }


    @SuppressWarnings("unchecked")
    static <T> void getItem(@NonNull final DataType type, @NonNull String key, @NonNull final AsyncSingleValueEventListener<T> listener) {
        getRef(type).child(getSanitizedKey(key)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST);
                } else {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) dataSnapshot.getValue(getDbClassObj(type));
                        if (null != dbItem) {
                            T domainObjItem = dbItem.toDomainObj();
                            listener.onSuccess(domainObjItem);
                        } else {
                            listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                        }
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

    static <T> void getItems(@NonNull final DataType type, @NonNull final AsyncValueEventListener<T> listener) {
        getItems(type, listener, true);
    }

    @NonNull
    static <T> DbListener<ValueEventListener> getItemsLive(@NonNull final DataType type, @NonNull final AsyncValueEventListener<T> listener) {
        return getItems(type, listener, false);
    }

    @SuppressWarnings("unchecked")
    private static <T> DbListener<ValueEventListener> getItems(@NonNull final DataType type, @NonNull final AsyncValueEventListener<T> listener, boolean singleEvent) {
        ValueEventListener dataConversionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(getDbClassObj(type));
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
        };
        DatabaseReference ref = getRef(type);
        if (singleEvent) {
            ref.addListenerForSingleValueEvent(dataConversionListener);
            return new DbListener<ValueEventListener>(ref, null);
        } else {
            return new DbListener<ValueEventListener>(ref, ref.addValueEventListener(dataConversionListener));
        }
    }

    static <T> void getItems(@NonNull final DataType type, @NonNull String childKey, @NonNull String childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = getRef(type).orderByChild(childKey).equalTo(childValue);
        getItems(type, query, listener, true);
    }

    static <T> void getItems(@NonNull final DataType type, @NonNull String childKey, int childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = getRef(type).orderByChild(childKey).equalTo(childValue);
        getItems(type, query, listener, true);
    }

    @NonNull
    static <T> DbListener<ValueEventListener> getItemsLive(@NonNull final DataType type, @NonNull String childKey, @NonNull String childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = getRef(type).orderByChild(childKey).equalTo(childValue);
        return getItems(type, query, listener, false);
    }

    @NonNull
    static <T> DbListener<ValueEventListener> getItemsLive(@NonNull final DataType type, @NonNull String childKey, int childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = getRef(type).orderByChild(childKey).equalTo(childValue);
        return getItems(type, query, listener, false);
    }

    @SuppressWarnings("unchecked")
    private static <T> DbListener<ValueEventListener> getItems(@NonNull final DataType type, @NonNull Query query, @NonNull final AsyncValueEventListener<T> listener, boolean singleEvent) {
        ValueEventListener dataConversionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(getDbClassObj(type));
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
        };
        DatabaseReference ref = getRef(type);
        if (singleEvent) {
            query.addListenerForSingleValueEvent(dataConversionListener);
            return new DbListener<ValueEventListener>(ref, null);
        } else {
            return new DbListener<ValueEventListener>(ref, query.addValueEventListener(dataConversionListener));
        }
    }

    public static <T> void getItemsRelational(final DataType relationType, final DataType returnType, String childKey, final AsyncValueEventListener<T> listener) {
        getRef(relationType).child(childKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(getDbClassObj(returnType));
                        if (null != dbItem) {
                            T domainObjItem = dbItem.toDomainObj();
                            returnValue.add(domainObjItem);
                        }
                    } catch (IllegalArgumentException e) {
                    } catch (InvalidDataException e) {
                    } catch (ClassCastException e) {
                    }
                }
                listener.onSuccess(returnValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    static <T> void deleteItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        getRef(type).child(dbItem.generateKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null != listener) {
                    if (databaseError == null) { listener.onSuccess(); }
                    else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                }
            }
        });
    }

    static <T> void createItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        final String key = dbItem.generateKey();
        getItem(type, key, new AsyncSingleValueEventListener<T>() {
            @Override
            public void onSuccess(@NonNull T item) {
                //Failure condition, Item already exists in db
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                //Success condition, item can be safely created;
                if (reason == AsyncEventFailureReason.DOES_NOT_EXIST) {
                    getRef(type).child(key).setValue(dbItem, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (null != listener) {
                                if (null == databaseError) { listener.onSuccess(); }
                                else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                            }
                        }
                    });
                } else {
                    if (null != listener) { listener.onFailure(reason); }
                }
            }
        });
    }

    static <T> void updateItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        final String key = dbItem.generateKey();
        getItem(type, key, new AsyncSingleValueEventListener<T>() {
            @Override
            public void onSuccess(@NonNull T item) {
                // Success condition: Item exists in database and can be updated
                DatabaseReference ref = getRef(type);
                ref.child(key).setValue(dbItem, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (null != listener) {
                            if (null == databaseError) { listener.onSuccess(); }
                            else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Failure condition: cannot update an item that doesn't currently exist in the db
                if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    static <T> void updateItem(@NonNull final T oldItem, @NonNull final T newItem, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(oldItem);
        final DataType newType = getType(newItem);
        if (type != newType) { throw new IllegalArgumentException("The items must be of the same type."); }
        final DbItem<?> dbItem = objectToDbItem(newItem);
        final String key = dbItem.generateKey();
        getItem(type, key, new AsyncSingleValueEventListener<T>() {
            @Override
            public void onSuccess(@NonNull T item) {
                // Error condition: The new item already exists
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    deleteItem(oldItem, new AsyncActionEventListener() {
                        @Override
                        public void onSuccess() {
                            createItem(newItem, listener);
                        }
                        @Override
                        public void onFailure(@NonNull AsyncEventFailureReason reason) {
                            if (null != listener) { listener.onFailure(reason); }
                        }
                    });
                } else if (null != listener) {
                    listener.onFailure(reason);
                }
            }
        });
    }

}
