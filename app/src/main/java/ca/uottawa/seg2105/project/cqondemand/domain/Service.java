package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class Service implements Serializable {

    private static final long serialVersionUID = 1;
    protected String key;
    protected String name;
    protected int rate;
    protected String categoryKey;
    protected Category category;

    /**
     * Constructor for a new service Object
     * @param name the name of this Service
     * @param categoryKey The category that this service falls under
     * @param rate The rate per hour that this service costs
     */
    public Service(@NonNull String name, int rate, @NonNull String categoryKey) {
        if (!FieldValidation.serviceNameIsValid(name)) { throw new InvalidDataException("Invalid service name. " + FieldValidation.SERVICE_NAME_CHARS); }
        if (rate < 0) { throw new InvalidDataException("Rate cannot be negative. "); }
        if (null == categoryKey || categoryKey.isEmpty()) { throw new InvalidDataException("The categoryKey cannot be null or empty."); }
        this.name = name;
        this.rate = rate;
        this.categoryKey = categoryKey;
    }

    public Service(@NonNull String key, @NonNull String name, int rate, @NonNull String categoryKey) {
        this(name, rate, categoryKey);
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     *Returns the category associated with this service
     *
     * @return the associated category
     */
    public String getCategoryKey(){
        return this.categoryKey;
    }

    public void getCategory(final AsyncSingleValueEventListener<Category> listener) {
        if (null != category) {
            listener.onSuccess(category);
        } else {
            DbCategory.getCategory(categoryKey, new AsyncSingleValueEventListener<Category>() {
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

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Service)) { return false; }
        if (this == otherObj) { return true; }
        Service other = (Service) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        if ((null == categoryKey) != (null == other.categoryKey) || (null != categoryKey && !categoryKey.equals(other.categoryKey))) { return false; }
        return null != getUniqueName() && getUniqueName().equals(other.getUniqueName()) && rate == other.rate;
    }

    public String toString() {
        return this.name;
    }

}
