package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * The class <b> DbUtil</b> is a utility class for interfacing with the FireBase real-time database.
 * It defines all database read/write operation that are used by various DbItem extending classes
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */

public class DbUtil {

    /**
     * Map of database references
     */
    private static final HashMap<String, DatabaseReference> references = new HashMap<String, DatabaseReference>();

    /**
     * Enum for differentiating between different object types
     */
    enum DataType {
        USER, SERVICE, CATEGORY, AVAILABILITY, ADDRESS;
        @NonNull
        public String toString() {
            switch (this) {
                case USER: return "users";
                case SERVICE: return "services";
                case CATEGORY: return "categories";
                case AVAILABILITY: return "availabilities";
                case ADDRESS: return "address"; // Not a top-level node
                default: return this.name();
            }
        }
        @NonNull
        public Class getDbItemClass() {
            switch (this) {
                case USER: return DbUser.class;
                case SERVICE: return DbService.class;
                case CATEGORY: return DbCategory.class;
                case AVAILABILITY: return DbAvailability.class;
                case ADDRESS: return DbAddress.class;
                default: throw new UnsupportedOperationException("The type is unsupported by this method.");
            }
        }
        public DatabaseReference getRef() {
            return DbUtil.getRef(this.toString());
        }
    }

    /**
     * Method to create a DatabaseReference based on the string name of that database location
     * @param refName the name of the location to be accessed
     * @return a DatabaseReference to the specificed location
     */
    static DatabaseReference getRef(String refName) {
        DatabaseReference ref = references.get(refName);
        if (null == ref) {
            ref = FirebaseDatabase.getInstance().getReference().child(refName);
            references.put(refName, ref);
        }
        return ref;
    }

    /**
     * Method to generate a unique key using FireBase's key generation
     *
     * @return the generated key
     */
    static String generateKey() {
        return FirebaseDatabase.getInstance().getReference().push().getKey();
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
        if (object instanceof Availability) { return new DbAvailability((Availability) object); }
        if (object instanceof Address) { return new DbAddress((Address) object); }
        throw new IllegalArgumentException("Unsupported type.");
    }

    /**
     * Returns the datatype of a given domain object. Accepts objects of type Service, User, and Category.
     * Throws an IllegalArgumentException if passed any other type of object.
     * @param object
     * @return
     */
    @NonNull
    protected static DataType getType(@NonNull Object object) {
        if (object instanceof User) { return DataType.USER; }
        if (object instanceof Service) { return DataType.SERVICE; }
        if (object instanceof Category) { return DataType.CATEGORY; }
        if (object instanceof Availability) { return DataType.AVAILABILITY; }
        if (object instanceof Address) { return DataType.ADDRESS; }
        throw new IllegalArgumentException("Unsupported type.");
    }

    /**
     * Generic method for retrieving an item from the database
     * @param type the type of object being retrieved
     * @param key the key of the object to be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class of object which is being retrieved
     */
    @SuppressWarnings("unchecked")
    static <T> void getItem(@NonNull final DataType type, @NonNull String key, @NonNull final AsyncSingleValueEventListener<T> listener) {
        type.getRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST);
                } else {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(type.getDbItemClass());
                        if (null != dbItem && null != snapshot.getKey()) {
                            dbItem.setKey(snapshot.getKey());
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

    /**
     * Generic method to retrieve items of a specific type, based on a DbQuery
     *
     * @param type the item type
     * @param queryDb the query to filter by
     * @param listener  the listener that will handle the success/failure of this operation
     * @param <T> the class of the retrived Items
     */
    static <T> void getItems(@NonNull final DataType type, @NonNull DbQuery queryDb, @NonNull final AsyncValueEventListener<T> listener) {
        getItems(type, queryDb, listener, true);
    }

    /**
     * Generic method to retrieve items of a specific type, based on a DbQuery. Retrieved data is updated
     * in real time as changes are made to the database
     *
     * @param type the item type
     * @param queryDb the query to filter by
     * @param listener  the listener that will handle the success/failure of this operation
     * @param <T> the class of the retrieved Items
     * @return a DbListenerHandle that handles the ValueEventListener attached to the database
     */
    @NonNull
    static <T> DbListenerHandle<ValueEventListener> getItemsLive(@NonNull final DataType type, @NonNull DbQuery queryDb, @NonNull final AsyncValueEventListener<T> listener) {
        return getItems(type, queryDb, listener, false);
    }

    /**
     * Private generic method that retrieves items of a certain type based on a query
     *
     * @param type the type of item to be retrieved
     * @param queryDb the query to filter retrieved items by
     * @param listener the listener that will handle the success/failure of this operation
     * @param singleEvent boolean, whether or not this retrieval operation is 'live' or not
     * @param <T> the class of the retrieved Items
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> DbListenerHandle<ValueEventListener> getItems(@NonNull final DataType type, @Nullable DbQuery queryDb, @NonNull final AsyncValueEventListener<T> listener, boolean singleEvent) {
        Query query;
        if (null == queryDb) { query = DbQuery.createKeyQuery().apply(type.getRef()); }
        else { query = queryDb.apply(type.getRef()); }

        ValueEventListener dataConversionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(type.getDbItemClass());
                        if (null != dbItem && null != snapshot.getKey()) {
                            dbItem.setKey(snapshot.getKey());
                            T domainObjItem = dbItem.toDomainObj();
                            returnValue.add(domainObjItem);
                        }
                    }
                    catch (IllegalArgumentException ignored) { }
                    catch (InvalidDataException ignored) { }
                    catch (ClassCastException ignored) { }
                }
                listener.onSuccess(returnValue);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        };

        if (singleEvent) {
            query.addListenerForSingleValueEvent(dataConversionListener);
            return new DbListenerHandle<ValueEventListener>(query.getRef(), null);
        } else {
            return new DbListenerHandle<ValueEventListener>(query.getRef(), query.addValueEventListener(dataConversionListener));
        }
    }

    /**
     * Generic method to delete an item from the database
     *
     * @param item the item to delete
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class of object to be deleted
     */
    static <T> void deleteItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        if (null == dbItem.getKey() || dbItem.getKey().isEmpty()) { throw new IllegalArgumentException("An item key is required. Unable to delete from the database without the key."); }
        type.getRef().child(dbItem.getKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null != listener) {
                    if (databaseError == null) { listener.onSuccess(); }
                    else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                }
            }
        });
    }

    /**
     * Generic method to add an item to the database
     *
     * @param item the item to be added
     * @param listener the listener that will handle the failure/success of this operation
     * @param <T> the class of item to be created
     */
    static <T> void createItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        final DatabaseReference pushRef = type.getRef().push();
        String key = pushRef.getKey();
        if (null == key) { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
        dbItem.setKey(pushRef.getKey());
        pushRef.setValue(dbItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null != listener) {
                    if (null == databaseError) { listener.onSuccess(); }
                    else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
                }
            }
        });
    }

    /**
     * Generic method to update an Item in the database
     *
     * @param item the item to be updated
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class item to update
     */
    static <T> void updateItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        getItem(type, dbItem.getKey(), new AsyncSingleValueEventListener<T>() {
            @Override
            public void onSuccess(@NonNull T item) {
                // Success condition: Item exists in database and can be updated
                type.getRef().child(dbItem.getKey()).setValue(dbItem, new DatabaseReference.CompletionListener() {
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

}
