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
import java.util.Map;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class DbService extends DbItem<Service> {

    public String unique_name;
    public String name;
    public String category_id;
    public int rate;

    public DbService() {}

    DbService(Service item) {
        super(item.getKey());
        unique_name = item.getUniqueName();
        name = item.getName();
        rate = item.getRate();
        category_id = item.getCategoryID();
    }

    @NonNull
    public Service toDomainObj() { return new Service(retrieveKey(), name, rate, category_id); }

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

    public static void deleteService(@NonNull Service service, @Nullable AsyncActionEventListener listener) {
        if (null == service.getKey() || service.getKey().isEmpty()) { throw new IllegalArgumentException("A service object with a key is required. Unable to delete from the database without the key."); }
        deleteServiceRelational(service, listener);
    }

    public static void getService(@NonNull String key, @NonNull AsyncSingleValueEventListener<Service> listener) {
        DbUtil.getItem(DbUtil.DataType.SERVICE, key, listener);
    }

    public static void getServiceByName(@NonNull String name, @NonNull final AsyncSingleValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.USER, "unique_name", Service.getUniqueName(name), new AsyncValueEventListener<Service>() {
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

    public static void getServices(@NonNull AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, listener);
    }

    @NonNull
    public static DbListener<?> getServicesLive(@NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, listener);
    }

    public static void getServicesByCategory(@NonNull Category category, @NonNull AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, "category_id", category.getKey(), listener);
    }

    @NonNull
    public static DbListener<?> getServicesByCategoryLive(@NonNull Category category, @NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, "category_id", category.getKey(), listener);
    }

    public static void getProvidersByService(@NonNull Service service, @NonNull final AsyncValueEventListener<Service> listener) {
        if (service.getKey() == null || service.getKey().isEmpty()) { throw new IllegalArgumentException("A service object with a key is required. Unable to query the database without the key."); }
        DbUtilRelational.getItemsRelational(DbUtilRelational.RelationType.SERVICE_USERS, service.getKey(), listener);
    }

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

    private static Map<String,Object> createServiceProviderWithServiceMap(@NonNull String userKey, @Nullable DbUser userDb, @NonNull String serviceKey, @Nullable DbService serviceDb) {
        HashMap<String, Object> pathMap = new HashMap<String, Object>();
        String path = "%s/%s/%s";
        pathMap.put(String.format(path, DbUtilRelational.RelationType.USER_SERVICES, userKey, serviceKey), serviceDb);
        pathMap.put(String.format(path, DbUtilRelational.LookupType.USER_SERVICES, serviceKey, userKey), serviceDb == null ? null : true);
        pathMap.put(String.format(path, DbUtilRelational.RelationType.SERVICE_USERS, serviceKey, userKey), userDb);
        pathMap.put(String.format(path, DbUtilRelational.LookupType.SERVICE_USERS, userKey, serviceKey), userDb == null ? null : true);
        return pathMap;
    }

    public static void relateToProvider(@NonNull Service service, @NonNull User user, @Nullable final AsyncActionEventListener listener) {
        Map<String,Object> pathMap = createServiceProviderWithServiceMap(user.getKey(), new DbUser(user), service.getKey(), new DbService(service));
        DbUtilRelational.multiPathUpdate(pathMap, listener);
    }

    public static void endRelationWithProvider(@NonNull Service service, @NonNull User user, @Nullable final AsyncActionEventListener listener) {
        Map<String,Object> pathMap = createServiceProviderWithServiceMap(user.getKey(), null, service.getKey(), null);
        DbUtilRelational.multiPathUpdate(pathMap, listener);
    }

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
