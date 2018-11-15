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

    protected String key;
    protected String name;
    protected int rate;
    protected String categoryID;
    protected Category category;
    protected ArrayList<String> serviceProviderIDs;

    /**
     * Constructor for a new service Object
     * @param name the name of this Service
     * @param categoryID The category that this service falls under
     * @param rate The rate per hour that this service costs
     */
    public Service(@NonNull String name, int rate, @NonNull String categoryID) {
        if (!FieldValidation.serviceNameIsValid(name)) { throw new InvalidDataException("Invalid service name. " +
                FieldValidation.ILLEGAL_SERVICE_NAME_CHARS_MSG); }
        if (rate < 0) { throw new InvalidDataException("Rate cannot be negative. "); }
        this.name = name;
        this.rate = rate;
        this.categoryID = categoryID;
    }

    public Service(@NonNull String key, @NonNull String name, int rate, @NonNull String categoryID) {
        if (!FieldValidation.serviceNameIsValid(name)) { throw new InvalidDataException("Invalid service name. " +
                FieldValidation.ILLEGAL_SERVICE_NAME_CHARS_MSG); }
        if (rate < 0) { throw new InvalidDataException("Rate cannot be negative. "); }
        this.key = key;
        this.name = name;
        this.rate = rate;
        this.categoryID = categoryID;
    }

    public String getKey() {
        return key;
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

    public String getUniqueName() {
        return getUniqueName(name);
    }

    public static String getUniqueName(@NonNull String name) {
        String uniqueName = name.toLowerCase();
        uniqueName = uniqueName.replaceAll("[^a-z0-9]+", "_");
        return uniqueName;
    }

    public boolean equals(Service other) {
        return key != null && key.equals(other.key);
    }

    public String toString() {
        return this.name;
    }

}
