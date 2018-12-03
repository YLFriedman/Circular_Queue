package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

/**
 * The class <b> DbService </b> takes a Service object and puts it into a form more easily stored in the
 * database. Methods for moving between Service and DbService are provided, as well as database read/write
 * operations
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbService extends DbItem<Service> {

    /**
     * The unique name associated with a DbService
     */
    public String unique_name;

    /**
     * The display name associated with a DbService
     */
    public String name;

    /**
     * The category key associated with a DbService
     */
    public String category_key;

    /**
     * The rate per hour associated with a DbService
     */
    public Integer rate;

    /**
     * Empty constructor for FireBase to create DbService objects
     */
    public DbService() {}

    /**
     * Constructor that creates a new DbService based on an input Service object
     * @param item the Service which the constructed DbService will be based off of
     */
    public DbService(Service item) {
        super(item.getKey());
        unique_name = item.getUniqueName();
        name = item.getName();
        rate = item.getRate();
        category_key = item.getCategoryKey();
    }

    /**
     * Method to create a Service object based off of this DbService
     * @return a Service object based off of this DbService
     */
    @NonNull
    public Service toDomainObj() { return new Service(getKey(), name, rate, category_key); }

    /**
     * Method that adds a service to the database.
     * @param service the service to be added to the database
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void createService(@NonNull final Service service, @Nullable final AsyncActionEventListener listener) {
        getServiceByName(service.getName(), new AsyncSingleValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull Service item) {
                // Failure Condition: Service already exists
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    // Success Condition: Service does not exist
                    DbUtil.createItem(service, listener);
                } else if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    /**
     * Method to update a particular service. Updates base node and all relational nodes
     *
     * @param service the service to be updated
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void updateService(@NonNull final Service service, @Nullable final AsyncActionEventListener listener) {
        if (null == service.getKey() || service.getKey().isEmpty()) { throw new IllegalArgumentException("A service object with a key is required. Unable to update the database without the key."); }
        // Check if the name is already in use
        getServiceByName(service.getName(), new AsyncSingleValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull Service item) {
                // Success Condition: The only service with this name is the service being updated
                if (service.getKey().equals(item.getKey())) { multiPathUpdate(service, listener); }
                else if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Success Condition: Name is not in use
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) { multiPathUpdate(service, listener); }
                else if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    /**
     * Method to delete a service from the database. Deletes all occurrences and association of that service
     * @param service the service to be deleted
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void deleteService(@NonNull Service service, @Nullable AsyncActionEventListener listener) {
        if (null == service.getKey() || service.getKey().isEmpty()) { throw new IllegalArgumentException("A service object with a key is required. Unable to delete from the database without the key."); }
        deleteServiceRelational(service, listener);
    }


    /**
     * Method to retrieve a service from the database, based off of the service name
     *
     * @param name the name of the service to be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void getServiceByName(@NonNull String name, @NonNull final AsyncSingleValueEventListener<Service> listener) {
        DbQuery query = DbQuery.createChildValueQuery("unique_name").setEqualsFilter(Service.getUniqueName(name));
        DbUtil.getItems(DbUtil.DataType.USER, query, new AsyncValueEventListener<Service>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Service> data) {
                if (data.size() == 1) { listener.onSuccess(data.get(0)); }
                else if (data.size() == 0) { listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST); }
                else { listener.onFailure(AsyncEventFailureReason.NOT_UNIQUE); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) { listener.onFailure(reason); }
        });
    }

    /**
     * Method to retrieve all the Services from the database
     *
     * @param listener the listener that will handle the success/failure of this operation
     * @return
     */
    @NonNull
    public static DbListenerHandle<?> getServicesLive(@NonNull final AsyncValueEventListener<Service> listener) {
        DbQuery query = DbQuery.createChildValueQuery("unique_name");
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, query, listener);
    }

    /**
     * Method to retrieve only the services that fall under a particular category
     *
     * @param category the category of services to be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void getServicesByCategory(@NonNull Category category, @NonNull AsyncValueEventListener<Service> listener) {
        DbQuery query = DbQuery.createChildValueQuery("category_key").setEqualsFilter(category.getKey());
        DbUtil.getItems(DbUtil.DataType.SERVICE, query, listener);
    }

    /**
     * Method to retrieve only the services that fall under a particular category, data is updated in real
     * time as changes are made
     *
     * @param category the category of services to be retrieved
     * @param listener the listener that will handle the success/failure of this operation
     * @return a DbListenerHandle that handles the ValueEventListener attached to the database
     */
    @NonNull
    public static DbListenerHandle<?> getServicesByCategoryLive(@NonNull Category category, @NonNull final AsyncValueEventListener<Service> listener) {
        DbQuery query = DbQuery.createChildValueQuery("category_key").setEqualsFilter(category.getKey());
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, query, listener);
    }

    /**
     * Method for retrieving all the service providers associated with a particular service. Data is updated
     * in real time as changes are made.
     *
     * @param service the service whose providers you wish to retrieve
     * @param listener  the listener that will handle the success/failure of this operation
     * @return a DbListenerHandle that handles the ValueEventListener attached to the database
     */
    public static DbListenerHandle<ValueEventListener> getProvidersByServiceLive(@NonNull Service service, @NonNull final AsyncValueEventListener<ServiceProvider> listener) {
        if (service.getKey() == null || service.getKey().isEmpty()) { throw new IllegalArgumentException("A service object with a key is required. Unable to query the database without the key."); }
        DbQuery query = DbQuery.createChildValueQuery("rating");
        return DbUtilRelational.getItemsRelationalLive(DbUtilRelational.RelationType.SERVICE_USERS, service.getKey(), query, listener);
    }

    /**
     * Method for updating all the database locations in which a particular Service is stored
     *
     * @param service the service to be updated
     * @param listener the listener that will handle the success/failure of this operation
     */
    private static void multiPathUpdate(@NonNull final Service service, @Nullable final AsyncActionEventListener listener) {
        final HashMap<String, Object> pathMap = new HashMap<>();
        final String serviceKey = service.getKey();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_service_lookup").child(serviceKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DbService updatedService = new DbService(service);
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String userKey = child.getKey();
                    String path = String.format("%s/%s/%s", DbUtilRelational.RelationType.USER_SERVICES, userKey, serviceKey);
                    pathMap.put(path, updatedService);
                }
                pathMap.put(String.format("%s/%s", DbUtil.DataType.SERVICE, serviceKey), updatedService);
                DbUtilRelational.multiPathUpdate(pathMap, listener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
            }
        });
    }

    /**
     * Method to delete a Service from the database, updating all nodes that contain that service
     *
     * @param service the service to be deleted
     * @param listener the listener that will handle the success/failure of this operation
     */
    private static void deleteServiceRelational(Service service, final AsyncActionEventListener listener) {
        final String serviceKey = service.getKey();
        final HashMap<String, Object> pathMap = new HashMap<>();
        String path = "%s/%s";
        pathMap.put(String.format(path, DbUtilRelational.RelationType.SERVICE_USERS, serviceKey), null);
        pathMap.put(String.format(path, DbUtilRelational.LookupType.USER_SERVICES, serviceKey), null);
        pathMap.put(String.format(path, DbUtil.DataType.SERVICE, serviceKey), null);
        DatabaseReference ref = DbUtilRelational.LookupType.USER_SERVICES.getRef().child(serviceKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String path = "%s/%s/%s";
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String providerKey = child.getKey();
                    pathMap.put(String.format(path, DbUtilRelational.RelationType.USER_SERVICES, providerKey, serviceKey), null);
                    pathMap.put(String.format(path, DbUtilRelational.LookupType.SERVICE_USERS, providerKey, serviceKey), null);
                }
                DbUtilRelational.multiPathUpdate(pathMap, listener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

}
