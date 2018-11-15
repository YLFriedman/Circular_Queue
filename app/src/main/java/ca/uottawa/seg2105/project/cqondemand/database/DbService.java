package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
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
                if (item.equals(service)) { DbUtil.updateItem(service, listener); }
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

    @NonNull
    public static DbListener<?> getServicesByCategoryLive(@NonNull Category category, @NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, "category_id", category.getKey(), listener);
    }

}
