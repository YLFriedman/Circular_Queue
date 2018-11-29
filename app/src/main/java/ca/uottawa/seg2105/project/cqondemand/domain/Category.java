package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * This represents service categories. Categories have a name and a key. In the database, each category
 * is associated with a set of Services.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class Category implements Serializable {

    private static final long serialVersionUID = 1;
    /**
     * Stores the database key associated with the category
     */
    protected String key;
    /**
     * Stores the name of the category
     */
    protected String name;

    /**
     *Constructor for the Category object that does not require a key. Intended to be used before this
     * category has been added to the database.
     *
     * @param name the string that corresponds to this category
     * @paran services the services that will be associated with this category
     */
    public Category(@NonNull String name) {
        if (!FieldValidation.categoryNameIsValid(name)) { throw new InvalidDataException("Invalid Category Name. " + FieldValidation.CATEGORY_NAME_CHARS); }
        this.name = name;
    }

    /**
     * Constructor for Category that does require a key.
     * @param key the associated key
     * @param name the name of this category
     */
    public Category(@NonNull String key, @NonNull String name) {
        this(name);
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
    }

    /**
     * Gets the key associated with this category
     * @return the associated key
     */
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

    /**
     * Gets the unique name of this category, for database purposes.
     * @return the unique name of this category
     */
    public String getUniqueName() {
        return getUniqueName(name);
    }

    /**
     * Static method to generate unique name. Sets all letters to lowercase, and replaces spaces
     * and non alpha-numeric characters with " _ "
     * @param name the name to be converted
     * @return the unique version of the name
     */
    public static String getUniqueName(@NonNull String name) {
        String uniqueName = name.toLowerCase();
        uniqueName = uniqueName.replaceAll("[^a-z0-9]+", "_");
        return uniqueName;
    }

    /**
     * Method to compare this Category to another category
     *
     * @param otherObj the object to be compared to
     * @return whether this Category is equal to the given object
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Category)) { return false; }
        if (this == otherObj) { return true; }
        Category other = (Category) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        return null != getUniqueName() && getUniqueName().equals(other.getUniqueName());
    }

    /**
     * Returns a string representation of this category
     *
     * @return A string representation of this category
     */
    public String toString() {
        return this.name;
    }

}
