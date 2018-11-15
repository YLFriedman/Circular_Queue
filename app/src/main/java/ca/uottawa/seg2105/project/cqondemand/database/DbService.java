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

    public String name;
    public String category_id;
    public int rate;

    public DbService() {}

    public DbService(Service service) {
        name = service.getName();
        rate = service.getRate();
        category_id = service.getCategoryID();
    }

    @NonNull
    public Service toDomainObj() { return new Service(name, rate, category_id); }

    @NonNull
    public String generateKey() { return DbUtil.getSanitizedKey(name); }

    public static void createService(@NonNull Service service, @Nullable AsyncActionEventListener listener) {
        DbUtil.createItem(service, listener);
    }

    public static void updateService(@NonNull Service oldService, @NonNull Service newService, @Nullable AsyncActionEventListener listener) {
        if (DbUtil.getKey(oldService).equals(DbUtil.getKey(newService))) {
            DbUtil.updateItem(newService, listener);
        } else {
            DbUtil.updateItem(oldService, newService, listener);
        }
    }

    public static void deleteService(@NonNull Service service, @Nullable AsyncActionEventListener listener) {
        DbUtil.deleteItem(service, listener);
    }

    public static void getService(@NonNull String name, @NonNull AsyncSingleValueEventListener<Service> listener) {
        DbUtil.getItem(DbUtil.DataType.SERVICE, name, listener);
    }

    public static void getServices(@NonNull AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, listener);
    }

    @NonNull
    public static DbListener<?> getServicesLive(@NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, listener);
    }

    public static void getServices(@NonNull String categoryName, @NonNull AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, "category_id", DbUtil.getKey(new Category(categoryName)), listener);
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

    private static void createUpdateMap(final Service newService, final String serviceKey,
                                        final AsyncSingleValueEventListener<HashMap<String, Object>> listener) {

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
        String userToServicesPath = String.format("user_services/%s/%s", userDB.generateKey(), serviceDB.generateKey());
        String serviceToUsersPath = String.format("service_users/%s/%s", serviceDB.generateKey(), userDB.generateKey());
        String userServiceLookupPath = String.format("user_service_lookup/%s/%s", serviceDB.generateKey(), userDB.generateKey());
        String serviceUserLookupPath = String.format("service_users_lookup/%s/%s", userDB.generateKey(), serviceDB.generateKey());
        map.put(userToServicesPath, serviceDB);
        map.put(serviceToUsersPath, userDB);
        map.put(userServiceLookupPath, true);
        map.put(serviceUserLookupPath, true);
        FirebaseDatabase.getInstance().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
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
    public static DbListener<?> getServicesLive(@NonNull String categoryName, @NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, "category_id", DbUtil.getKey(new Category(categoryName)), listener);

    }

}
