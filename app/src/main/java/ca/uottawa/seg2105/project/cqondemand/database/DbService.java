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

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

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
                if (service.getKey().equals(item.getKey())) { DbUtil.updateItem(service, listener); }
                else if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Success Condition: Name is not in use
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) { DbUtil.updateItem(service, listener); }
                else if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    public static void deleteService(@NonNull Service service, @Nullable AsyncActionEventListener listener) {
        if (null == service.getKey() || service.getKey().isEmpty()) { throw new IllegalArgumentException("A service object with a key is required. Unable to delete from the database without the key."); }
        DbUtil.deleteItem(service, listener);
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


    public static void getServicesByProvider(String providerID, @NonNull final AsyncValueEventListener<Service> listener) {
        DbUtil.getItemsRelational(DbUtil.DataType.USER_SERVICES, DbUtil.DataType.SERVICE, providerID, listener);
    }

    public static void updateServiceRelational(final Service newService, final String serviceKey, final AsyncActionEventListener listener){
        AsyncSingleValueEventListener<HashMap<String, Object>> mapListener = new AsyncSingleValueEventListener<HashMap<String, Object>>() {
            @Override
            public void onSuccess(HashMap<String, Object> data) {
                DbService updatedService = new DbService(newService);
                data.put(String.format("services/%s", serviceKey), updatedService);
                FirebaseDatabase.getInstance().getReference().updateChildren(data);
                listener.onSuccess();
            }
            @Override
            public void onFailure(AsyncEventFailureReason reason) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        };
        createUpdateMap(newService, serviceKey, mapListener);
    }

    private static void createUpdateMap(final Service newService, final String serviceKey, final AsyncSingleValueEventListener<HashMap<String, Object>> listener) {
        final HashMap<String, Object> pathMap = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_service_lookup").child(serviceKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    DbService updatedService = new DbService(newService);
                    String userKey = child.getKey();
                    String path = String.format("user_services/%s/%s", userKey, serviceKey);
                    pathMap.put(path, updatedService);
                }
                listener.onSuccess(pathMap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    public static void relateToProvider(Service service, User user, final AsyncActionEventListener listener){
        HashMap<String, Object> map = new HashMap<>();
        DbService serviceDB = new DbService(service);
        DbUser userDB = new DbUser(user);
        String userToServicesPath = String.format("user_services/%s/%s", userDB.retrieveKey(), serviceDB.retrieveKey());
        String serviceToUsersPath = String.format("service_users/%s/%s", serviceDB.retrieveKey(), userDB.retrieveKey());
        String userServiceLookupPath = String.format("user_service_lookup/%s/%s", serviceDB.retrieveKey(), userDB.retrieveKey());
        String serviceUserLookupPath = String.format("service_users_lookup/%s/%s", userDB.retrieveKey(), serviceDB.retrieveKey());
        map.put(userToServicesPath, serviceDB);
        map.put(serviceToUsersPath, userDB);
        map.put(userServiceLookupPath, true);
        map.put(serviceUserLookupPath, true);
        FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) { listener.onSuccess(); }
                else { listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR); }
            }
        });
    }

    public static void deleteServiceRelational(final String serviceKey, final AsyncActionEventListener listener){
        AsyncSingleValueEventListener<HashMap<String, Object>> mapListener = new AsyncSingleValueEventListener<HashMap<String, Object>>() {
            @Override
            public void onSuccess(@NonNull HashMap<String, Object> item) {
                FirebaseDatabase.getInstance().getReference().updateChildren(item);
                listener.onSuccess();
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                listener.onFailure(reason);
            }
        };
        createDeletionMap(serviceKey, mapListener);
    }

    private static void createDeletionMap(final String serviceKey, final AsyncSingleValueEventListener<HashMap<String, Object>> listener){
        final HashMap<String, Object> pathMap = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_service_lookup").child(serviceKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String providerKey = child.getKey();
                    String userServicesPath = String.format("user_services/%s/%s", providerKey, serviceKey);
                    String lookupPath = String.format("service_users_lookup/%s/%s", providerKey, serviceKey);
                    pathMap.put(userServicesPath, null);
                    pathMap.put(lookupPath, null);
                }
                String serviceUsersPath = String.format("service_users/%s", serviceKey);
                String lookupPathPrimary = String.format("user_service_lookup/%s", serviceKey);
                String serviceMainPath = String.format("services/%s", serviceKey);
                pathMap.put(serviceUsersPath, null);
                pathMap.put(lookupPathPrimary, null);
                pathMap.put(serviceMainPath, null);
                listener.onSuccess(pathMap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(AsyncEventFailureReason.DATABASE_ERROR);
            }
        });
    }

    @NonNull
    public static DbListener<?> getServicesByCategoryLive(@NonNull Category category, @NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, "category_id", category.getKey(), listener);
    }

}
