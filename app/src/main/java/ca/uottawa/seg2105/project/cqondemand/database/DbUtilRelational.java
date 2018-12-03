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
import java.util.Map;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * The class <b> DbUtilRelational </b> is a utility class for handling all operations on relational nodes
 * in the database. Methods for read/write are implemented in generic form, with enums to specify the relation
 * type
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbUtilRelational extends DbUtil {

    /**
     * Enum to define the different association types in the database
     */
    enum RelationType {
        SERVICE_USERS, USER_SERVICES, USER_BOOKINGS, PROVIDER_REVIEWS;
        @NonNull
        public String toString() {
            switch (this) {
                case SERVICE_USERS: return "service_users";
                case USER_SERVICES: return "user_services";
                case USER_BOOKINGS: return "user_bookings";
                case PROVIDER_REVIEWS: return "provider_reviews";
                default: throw new IllegalArgumentException("Unrecognized type");
            }
        }
        @NonNull
        public Class getDbItemClass() {
            switch (this) {
                case SERVICE_USERS: return DbUser.class;
                case USER_SERVICES: return DbService.class;
                case USER_BOOKINGS: return DbBooking.class;
                case PROVIDER_REVIEWS: return DbReview.class;
                default: throw new IllegalArgumentException("Unrecognized type");
            }
        }
        public DatabaseReference getRef() {
            return DbUtil.getRef(this.toString());
        }
    }

    /**
     * Enum to define the various lookup node types in the database
     */
    enum LookupType {
        SERVICE_USERS, USER_SERVICES, USER_BOOKINGS;
        @NonNull
        public String toString() {
            switch (this) {
                case SERVICE_USERS: return "service_users_lookup";
                case USER_SERVICES: return "user_services_lookup";
                case USER_BOOKINGS: return "user_bookings_lookup";
                default: throw new IllegalArgumentException("Unrecognized type");
            }
        }
        public DatabaseReference getRef() {
            return DbUtil.getRef(this.toString());
        }
    }

    /**
     * Generic method to retrieve all the items stored in a specific relational node
     *
     * @param relationType the type of relation
     * @param path the relational node these items will be found on
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class of objects to retrieve
     */
    @SuppressWarnings("unchecked")
    static <T> void getItemRelational(@NonNull final RelationType relationType, @NonNull String path, @NonNull final AsyncSingleValueEventListener<T> listener) {
        relationType.getRef().child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST);
                } else {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(relationType.getDbItemClass());
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
     * Generic method to retrieve all the items stored in a specific relational node, filtered by a specified
     * DbQuery and child key
     *
     * @param relationType the relation type for this read call
     * @param childKey the association key
     * @param queryDb the query to filter results by
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class of objects to be retrieved
     */
    static <T> void getItemsRelational(@NonNull final RelationType relationType, @NonNull String childKey, @NonNull DbQuery queryDb, @NonNull final AsyncValueEventListener<T> listener) {
        getItemsRelational(relationType, childKey, queryDb, listener, true);
    }

    /**
     * Generic method to retrieve all the items stored in a specific relational node, filtered by a specified
     * DbQuery and child key. Retrieved data is updated whenever changes are made to the database
     *
     * @param relationType the relation type for this read call
     * @param childKey the association key
     * @param queryDb the query to filter results by
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class of objects to be retrieved
     * @return the DbListenerHandle that will handle the ValueEventListener attached to the database
     */
    @NonNull
    static <T> DbListenerHandle<ValueEventListener> getItemsRelationalLive(@NonNull final RelationType relationType, @NonNull String childKey, @NonNull DbQuery queryDb, @NonNull final AsyncValueEventListener<T> listener) {
        return getItemsRelational(relationType, childKey, queryDb, listener, false);
    }

    /**
     * Private generic method to retrieve all the items stored in a specific relational node, filtered by a specified
     * DbQuery and child key. Allows for selection of static or "live" retrieval of data
     *
     * @param relationType the relation type for this read call
     * @param childKey the association key
     * @param queryDb the query to filter results by
     * @param listener the listener that will handle the success/failure of this operation
     * @param <T> the class of objects to be retrieved
     * @param singleEvent boolean, whether this retrieval will be live or not
     * @return the DbListenerHandle that will handle the ValueEventListener attached to the database
     */
    @SuppressWarnings("unchecked")
    @NonNull
    private static <T> DbListenerHandle<ValueEventListener> getItemsRelational(@NonNull final RelationType relationType, @NonNull String childKey, @Nullable DbQuery queryDb, @NonNull final AsyncValueEventListener<T> listener, boolean singleEvent) {
        Query query;
        if (null == queryDb) { query = DbQuery.createKeyQuery().apply(relationType.getRef().child(childKey)); }
        else { query = queryDb.apply(relationType.getRef().child(childKey)); }

        ValueEventListener dataConversionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<T> returnValue = new ArrayList<T>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    try {
                        DbItem<T> dbItem = (DbItem<T>) snapshot.getValue(relationType.getDbItemClass());
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
     * Method for applying a multipath update to the database, given an update map
     *
     * @param pathMap a HashMap containing paths to update and objects to store
     * @param listener the listener that will handle the success/failure of this operation
     */
    static void multiPathUpdate(@NonNull Map<String,Object> pathMap, @Nullable final AsyncActionEventListener listener) {
        FirebaseDatabase.getInstance().getReference().updateChildren(pathMap, new DatabaseReference.CompletionListener() {
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
     * Method to associate a Service Provider and a Service
     *
     * @param service the service to be linked
     * @param provider the service provider to be linked
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void linkServiceAndProvider(@NonNull Service service, @NonNull ServiceProvider provider, @Nullable final AsyncActionEventListener listener) {
        multiPathUpdate(createServiceWithProviderMap(service.getKey(), new DbService(service), provider.getKey(), new DbUser(provider)), listener);
    }

    /**
     * Method to de - associate a Service Provider and a Service
     *
     * @param service the service to be unlinked
     * @param provider the service provider to be unlinked
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void unlinkServiceAndProvider(@NonNull Service service, @NonNull ServiceProvider provider, @Nullable final AsyncActionEventListener listener) {
        multiPathUpdate(createServiceWithProviderMap(service.getKey(), null, provider.getKey(), null), listener);
    }

    /**
     * Method for creating an update map for operations relating a service and a service provider
     * @param serviceKey the key of the associated service
     * @param serviceDb the associated DbService object
     * @param userKey the key of the associated service provider
     * @param userDb the associated DbUser object
     * @return
     */
    private static Map<String,Object> createServiceWithProviderMap(@NonNull String serviceKey, @Nullable DbService serviceDb, @NonNull String userKey, @Nullable DbUser userDb) {
        HashMap<String, Object> pathMap = new HashMap<String, Object>();
        String path = "%s/%s/%s";
        pathMap.put(String.format(path, RelationType.USER_SERVICES, userKey, serviceKey), serviceDb);
        pathMap.put(String.format(path, LookupType.USER_SERVICES, serviceKey, userKey), null == serviceDb ? null : true);
        pathMap.put(String.format(path, RelationType.SERVICE_USERS, serviceKey, userKey), userDb);
        pathMap.put(String.format(path, LookupType.SERVICE_USERS, userKey, serviceKey), null == userDb ? null : true);
        return pathMap;
    }



}
