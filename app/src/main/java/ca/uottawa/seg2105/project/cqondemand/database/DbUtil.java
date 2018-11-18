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
 * This class is a utility class for interfacing with the FireBase real-time database. It defines
 * database classes which are adapted from the classes in the domain package.
 *
 */

public class DbUtil {

    protected static final HashMap<String, DatabaseReference> references = new HashMap<String, DatabaseReference>();

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
            DatabaseReference ref = references.get(this.toString());
            if (null == ref) {
                ref = FirebaseDatabase.getInstance().getReference().child(this.toString());
                references.put(this.toString(), ref);
            }
            return ref;
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
    protected static DbItem<?> objectToDbItem(@NonNull Object object) {
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
                            dbItem.storeKey(snapshot.getKey());
                            T domainObjItem = dbItem.toDomainObj();
                            listener.onSuccess(domainObjItem);
                        } else {
                            listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                        }
                    } catch (IllegalArgumentException ignored) {
                        listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                    } catch (InvalidDataException ignored) {
                        listener.onFailure(AsyncEventFailureReason.INVALID_DATA);
                    } catch (ClassCastException ignored) {
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
    protected static <T> DbListener<ValueEventListener> getItems(@NonNull final DataType type, @NonNull final AsyncValueEventListener<T> listener, boolean singleEvent) {
        ValueEventListener dataConversionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(type.getDbItemClass());
                        if (null != dbItem && null != snapshot.getKey()) {
                            dbItem.storeKey(snapshot.getKey());
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
        DatabaseReference ref = type.getRef();
        if (singleEvent) {
            ref.addListenerForSingleValueEvent(dataConversionListener);
            return new DbListener<ValueEventListener>(ref, null);
        } else {
            return new DbListener<ValueEventListener>(ref, ref.addValueEventListener(dataConversionListener));
        }
    }

    static <T> void getItems(@NonNull final DataType type, @NonNull String childKey, @NonNull String childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = type.getRef().orderByChild(childKey).equalTo(childValue);
        getItems(type, query, listener, true);
    }

    static <T> void getItems(@NonNull final DataType type, @NonNull String childKey, int childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = type.getRef().orderByChild(childKey).equalTo(childValue);
        getItems(type, query, listener, true);
    }

    @NonNull
    static <T> DbListener<ValueEventListener> getItemsLive(@NonNull final DataType type, @NonNull String childKey, @NonNull String childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = type.getRef().orderByChild(childKey).equalTo(childValue);
        return getItems(type, query, listener, false);
    }

    @NonNull
    static <T> DbListener<ValueEventListener> getItemsLive(@NonNull final DataType type, @NonNull String childKey, int childValue, @NonNull final AsyncValueEventListener<T> listener) {
        Query query = type.getRef().orderByChild(childKey).equalTo(childValue);
        return getItems(type, query, listener, false);
    }

    @SuppressWarnings("unchecked")
    protected static <T> DbListener<ValueEventListener> getItems(@NonNull final DataType type, @NonNull Query query, @NonNull final AsyncValueEventListener<T> listener, boolean singleEvent) {
        ValueEventListener dataConversionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                ArrayList<T> returnValue = new ArrayList<T>(size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(type.getDbItemClass());
                        if (null != dbItem && null != snapshot.getKey()) {
                            dbItem.storeKey(snapshot.getKey());
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
            return new DbListener<ValueEventListener>(query.getRef(), null);
        } else {
            return new DbListener<ValueEventListener>(query.getRef(), query.addValueEventListener(dataConversionListener));
        }
    }

    static <T> void deleteItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        if (null == dbItem.retrieveKey() || dbItem.retrieveKey().isEmpty()) { throw new IllegalArgumentException("An item key is required. Unable to delete from the database without the key."); }
        type.getRef().child(dbItem.retrieveKey()).removeValue(new DatabaseReference.CompletionListener() {
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
        final DatabaseReference pushRef = type.getRef().push();
        String key = pushRef.getKey();
        if (null == key) { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
        dbItem.storeKey(pushRef.getKey());
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

    static <T> void updateItem(@NonNull T item, @Nullable final AsyncActionEventListener listener) {
        final DataType type = getType(item);
        final DbItem<?> dbItem = objectToDbItem(item);
        getItem(type, dbItem.retrieveKey(), new AsyncSingleValueEventListener<T>() {
            @Override
            public void onSuccess(@NonNull T item) {
                // Success condition: Item exists in database and can be updated
                type.getRef().child(dbItem.retrieveKey()).setValue(dbItem, new DatabaseReference.CompletionListener() {
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
