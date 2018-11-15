package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.database.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class Service {


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
    public Service(String name, int rate, String categoryID) {
        if (!FieldValidation.serviceNameIsValid(name)) { throw new InvalidDataException("Invalid service name. " +
                FieldValidation.ILLEGAL_SERVICE_NAME_CHARS_MSG); }
        if (null == categoryID) { throw new IllegalArgumentException("The categoryID cannot be null."); }
        if (rate < 0) { throw new InvalidDataException("Rate cannot be negative. "); }
        this.name = name;
        this.rate = rate;
        this.categoryID = categoryID;
    }

    /**
     * Constructor for a new service Object
     * @param name the name of this Service
     * @param category The category that this service falls under
     * @param rate The rate per hour that this service costs
     */
    public Service(String name, int rate, Category category) {
        if (!FieldValidation.serviceNameIsValid(name)) { throw new InvalidDataException("Invalid service name. " +
                FieldValidation.ILLEGAL_SERVICE_NAME_CHARS_MSG); }
        if (null == category) { throw new IllegalArgumentException("The category cannot be null."); }
        if (rate < 0) { throw new InvalidDataException("Rate cannot be negative. "); }
        this.name = name;
        this.rate = rate;
        this.category = category;
        this.categoryID = DbUtil.getKey(category);
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

    public void getCategory(final AsyncSingleValueEventListener<Category> listener) {
        if (null != category) {
            listener.onSuccess(category);
        } else {
            DbCategory.getCategory(categoryID, new AsyncSingleValueEventListener<Category>() {
                @Override
                public void onSuccess(@NonNull Category item) {
                    category = item;
                    listener.onSuccess(item);
                }
                @Override
                public void onFailure(@NonNull AsyncEventFailureReason reason) { listener.onFailure(reason); }
            });
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

    public boolean equals(Service other) {
        return null != other && name.equals(other.name);
    }

    public String toString() {
        return this.name;
    }

}
