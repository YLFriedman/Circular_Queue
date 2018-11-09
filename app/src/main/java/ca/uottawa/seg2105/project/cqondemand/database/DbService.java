package ca.uottawa.seg2105.project.cqondemand.database;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;

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

    public Service toItem() { return new Service(name, rate, category_id); }

    public String generateKey() { return DbUtil.getSanitizedKey(name); }

}
