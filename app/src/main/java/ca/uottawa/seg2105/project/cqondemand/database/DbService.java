package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

public class DbService extends DbItem<Service> {

    public String name;
    public String category_id;
    public int rate;

    public DbService() {}

    DbService(Service item) {
        super(item.getKey());
        storeKey(item.getKey());
        name = item.getName();
        rate = item.getRate();
        category_id = item.getCategoryID();
    }

    @NonNull
    public Service toDomainObj() { return new Service(retrieveKey(), name, rate, category_id); }

    public static void createService(@NonNull Service service, @Nullable AsyncActionEventListener listener) {
        DbUtil.createItem(service, listener);
    }

    public static void updateService(@NonNull Service oldService, @NonNull Service newService, @Nullable AsyncActionEventListener listener) {
        if (oldService.equals(newService)) {
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

    public static void getServices(@NonNull Category category, @NonNull AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, "category_id", category.getKey(), listener);
    }

    @NonNull
    public static DbListener<?> getServicesLive(@NonNull Category category, @NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, "category_id", category.getKey(), listener);
    }

}
