package ca.uottawa.seg2105.project.cqondemand.domain;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class Service {

    public static final String ILLEGAL_SERVICENAME_CHARS_REGEX = ".*[^a-zA-Z -].*";
    public static final String ILLEGAL_SERVICENAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z - space";

    private String name;
    private int rate;
    private String categoryID;
    private Category category;
    private ArrayList<String> serviceProviderIDs;

    /**
     * Constructor for a new service Object
     * @param name the name of this Service
     * @param categoryID The category that this service falls under
     * @param rate The rate per hour that this service costs
     */
    public Service(String name, int rate, String categoryID){
        /*this.serviceProviderIDs = new ArrayList<>();
        for (User user: serviceProviders) {
            if (!(user.getType() == User.Types.SERVICE_PROVIDER)) {
                throw new IllegalArgumentException("Only Service Providers can provide a service");
            }
            serviceProviderIDs.add(user.getUserName());
        }*/
        if (!nameIsValid(name)) { throw new InvalidDataException("Invalid service name. " + ILLEGAL_SERVICENAME_CHARS_MSG); }
        if (null == categoryID) { throw new IllegalArgumentException("The categoryID cannot be null."); }
        if (rate < 0) { throw new InvalidDataException("Rate cannot be negative. "); }

        this.name = name;
        this.rate = rate;
        this.categoryID = categoryID;
    }

    /**
     *Returns an ArrayList of all the service providers that provide this service
     *
     * @return an ArrayList of the service providers that provide this service
     */

    public ArrayList<String> getServiceProviderIDs() {
        return this.serviceProviderIDs;
    }

    /**
     *Returns the category associated with this service
     *
     * @return the associated category
     */
    public String getCategoryID(){
        return this.categoryID;
    }

    public void getCategory(AsyncSingleValueEventListener<Category> listener) {
        if (null != category) {
            listener.onSuccess(category);
        } else {
            Category.getCategory(categoryID, listener);
        }
    }

    /**
     *Returns the rate per hour of this service
     *
     * @return the rate associated with this Service
     */
    public int getRate(){
        return rate;
    }

    /**
     * Getter for the name of this Service
     *
     * @return the name of this service
     */

    public String getName() {
        return name;
    }

    public static boolean nameIsValid(String name) {
        if (name == null || name.isEmpty()) { return false;  }
        return !name.matches(ILLEGAL_SERVICENAME_CHARS_REGEX);
    }

    public void create(final AsyncActionEventListener listener) {
        DbUtil.createItem(this, listener);
    }

    public void update(final Service newService, final AsyncActionEventListener listener) {

        if (DbUtil.getKey(this).equals(DbUtil.getKey(newService))) {
            DbUtil.updateItem(newService, listener);
        } else {
            DbUtil.updateItem(this, newService, listener);
        }
    }

    public void delete(final AsyncActionEventListener listener) {
        DbUtil.deleteItem(this, listener);
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
