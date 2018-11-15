package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class Category {

    protected String key;
    protected String name;

    /**
     *Constructor for the Category object
     *
     * @param name the string that corresponds to this category
     * @paran services the services that will be associated with this category
     */
    public Category(@NonNull String name) {
        if (!FieldValidation.categoryNameIsValid(name)) {
            throw new InvalidDataException("Invalid Category Name. " + FieldValidation.ILLEGAL_CATEGORY_NAME_CHARS_MSG);
        }
        this.name = name;
    }

    public Category(@NonNull String key, @NonNull String name) {
        if (!FieldValidation.categoryNameIsValid(name)) {
            throw new InvalidDataException("Invalid Category Name. " + FieldValidation.ILLEGAL_CATEGORY_NAME_CHARS_MSG);
        }
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    /**
     *Getter for the name of this category
     *
     * @return String name of this category
     */
    public String getName(){
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

    public boolean equals(Category other) {
        if (null == other) { return false; }
        if (null != key && null != other.key) { return key.equals(other.key); }
        return null != getUniqueName() && getUniqueName().equals(other.getUniqueName());
    }

    public String toString() {
        return this.name;
    }

}
