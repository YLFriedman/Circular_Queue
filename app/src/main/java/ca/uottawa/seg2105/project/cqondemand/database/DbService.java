package ca.uottawa.seg2105.project.cqondemand.database;

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

    public Service toDomainObj() { return new Service(name, rate, category_id); }

    public String generateKey() { return DbUtil.getSanitizedKey(name); }

    public static void createService(Service service, final AsyncActionEventListener listener) {
        DbUtil.createItem(service, listener);
    }

    public static void updateService(final Service oldService, final Service newService, final AsyncActionEventListener listener) {
        if (DbUtil.getKey(oldService).equals(DbUtil.getKey(newService))) {
            DbUtil.updateItem(newService, listener);
        } else {
            DbUtil.updateItem(oldService, newService, listener);
        }
    }

    public static void deleteService(Service service, final AsyncActionEventListener listener) {
        DbUtil.deleteItem(service, listener);
    }

    public static void getService(String name, final AsyncSingleValueEventListener<Service> listener) {
        DbUtil.getItem(DbUtil.DataType.SERVICE, name, listener);
    }

    public static void getServices(final AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, listener);
    }

    public static void getServices(String categoryName, final AsyncValueEventListener<Service> listener) {
        DbUtil.getItems(DbUtil.DataType.SERVICE, "category_id", DbUtil.getKey(new Category(categoryName)), listener);
    }

}
