package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

/**
 * Abstract class that all database versions of domain classes must implement
 *
 * @param <T> The domain class being represented
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
abstract class DbItem<T> {

    /**
     * The database key associated with this item
     */
    @Exclude
    public String key;

    /**
     * Empty constructor for FireBase to create items
     */
    DbItem() { }

    /**
     * Creates a DbItem, assigning it the correct key
     * @param key the database key associated with this item
     */
    DbItem(String key) {
        this.key = key;
    }

    /**
     * Abstract method to convert DbItems to the domain object the are representing
     * @return the represented domain object
     */
    abstract @NonNull T toDomainObj();

    /**
     * Sets the key value of this DbItem
     * @param key the key to be associated with this item
     */
    @Exclude
    void setKey(@NonNull String key) {
        this.key = key;
    }

    /**
     * Retrieves the key associated with a DbItem
     * @return the associated key
     */
    @Exclude
    String getKey() {
        return key;
    }

}
