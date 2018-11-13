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

    @NonNull
    public static DbListener<?> getServicesLive(@NonNull String categoryName, @NonNull final AsyncValueEventListener<Service> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.SERVICE, "category_id", DbUtil.getKey(new Category(categoryName)), listener);
    }

}
