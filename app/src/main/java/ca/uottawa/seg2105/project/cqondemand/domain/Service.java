package ca.uottawa.seg2105.project.cqondemand.domain;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

import static android.text.TextUtils.isEmpty;

public class Service {

    public static final String ILLEGAL_SERVICENAME_CHARS_REGEX = ".*[^a-zA-Z -].*";
    public static final String ILLEGAL_SERVICENAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z - space";

    private Category category;
    private String categoryID;
    private ArrayList<String> serviceProviderIDs;
    private String name;
    private int rate;


    /**
     * Empty constructor for Firebase Use
     */

    public Service(){

    }
    /**
     * Constructor for a new service Object
     * @param name the name of this Service
     * @param category The category that this service falls under
     * @param rate The rate per hour that this service costs
     * @param serviceProviders an ArrayList of Users that provide this service, must be of type Service Provider
     */
    public Service(String category, int rate, ArrayList<User> serviceProviders, String name){
            this.serviceProviderIDs = new ArrayList<>();
            for(User user : serviceProviders){
                if(!(user.getType() == User.Types.SERVICE_PROVIDER)){
                    throw new IllegalArgumentException("Only Service Providers can provide a service");
                }
                serviceProviderIDs.add(user.getUserName());
            }

            if(!nameIsValid(name)){
                throw new InvalidDataException("Invalid service name " + ILLEGAL_SERVICENAME_CHARS_MSG);
            }
            if(!nameIsValid(category)){
                throw new InvalidDataException("Invalid category name " + ILLEGAL_SERVICENAME_CHARS_MSG);
            }

            if(rate < 0){
                throw new InvalidDataException("Rate must be non-negative");
            }


            this.categoryID = category;
            this.rate = rate;
            this.name = name;
    }

    /**
     *Returns an ArrayList of all the service providers that provide this service
     *
     * @return an ArrayList of the service providers that provide this service
     */

    public ArrayList<String> getServiceProviderIDs(){
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

    /**
     *Returns the rate per hour of this service
     *
     * @return the rate associated with this Service
     */
    public int getRate(){
        return this.rate;
    }

    /**
     * Getter for the name of this Service
     *
     * @return the name of this service
     */

    public String getName(){
        return this.name;
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

    public static void getServices(final AsyncValueEventListener<Service> listener){
        DbUtil.getItems(DbUtil.DataType.SERVICE, listener);
    }

    public static void getService(String name, final AsyncValueEventListener<Service> listener){
        DbUtil.getItem(DbUtil.DataType.SERVICE, name, listener);
    }

    public static boolean nameIsValid(String name){
        if(name == null || name.isEmpty()) { return false;  }
        return name.matches(ILLEGAL_SERVICENAME_CHARS_REGEX);
    }

}
