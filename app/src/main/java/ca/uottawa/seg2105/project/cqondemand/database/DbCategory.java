package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

/**
 * The class <b> DbCategory </b> is a class used to take information from a Category object, and put it
 * in a form better suited for FireBase storage. Method for switching between Category and DbCategory are
 * provided, as well as database read/write operations
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbCategory extends DbItem<Category> {

    /**
     * The unique name associated with this category
     */
    public String unique_name;

    /**
     * The display name associated with this category
     */
    public String name;

    /**
     * Empty constructor for FireBase to create a new DbCategory
     */
    public DbCategory() {}

    /**
     * Constructor the creates a new DbCategory based on a Category
     * @param item the Category to base this DbCategory off of
     */
    public DbCategory(Category item) {
        super(item.getKey());
        unique_name = item.getUniqueName();
        name = item.getName();
    }

    /**
     * Method for converting a DbCategory to a Category object
     * @return a Category object based off of this DbCategory
     */
    @NonNull
    public Category toDomainObj() { return new Category(getKey(), name); }

    /**
     * Method to add a category to the database
     *
     * @param category the category to be added
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void createCategory(@NonNull final Category category, @Nullable final AsyncActionEventListener listener) {
        getCategoryByName(category.getName(), new AsyncSingleValueEventListener<Category>() {
            @Override
            public void onSuccess(@NonNull Category item) {
                // Failure Condition: Category already exists
                if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) {
                    // Success Condition: Category does not exist
                    DbUtil.createItem(category, listener);
                } else if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    /**
     * Method to update a category in the database
     * @param category the category to update
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void updateCategory(@NonNull final Category category, @Nullable final AsyncActionEventListener listener) {
        if (null == category.getKey() || category.getKey().isEmpty()) { throw new IllegalArgumentException("A category object with a key is required. Unable to update the database without the key."); }
        // Check if the name is already in use
        getCategoryByName(category.getName(), new AsyncSingleValueEventListener<Category>() {
            @Override
            public void onSuccess(@NonNull Category item) {
                // Success Condition: The only category with this name is the category being updated
                if (category.getKey().equals(item.getKey())) { DbUtil.updateItem(category, listener); }
                else if (null != listener) { listener.onFailure(AsyncEventFailureReason.ALREADY_EXISTS); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) {
                // Success Condition: Name is not in use
                if (AsyncEventFailureReason.DOES_NOT_EXIST == reason) { DbUtil.updateItem(category, listener); }
                else if (null != listener) { listener.onFailure(reason); }
            }
        });
    }

    /**
     * Method to delete a category from the database
     * @param category the category to be deleted
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void deleteCategory(@NonNull Category category, @Nullable AsyncActionEventListener listener) {
        if (null == category.getKey() || category.getKey().isEmpty()) { throw new IllegalArgumentException("A category object with a key is required. Unable to delete from the database without the key."); }
        DbUtil.deleteItem(category, listener);
    }

    /**
     * Method to get a specific category from the database, based on a key
     * @param key the key associated with the desired category
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void getCategory(@NonNull String key, @NonNull AsyncSingleValueEventListener<Category> listener) {
        DbUtil.getItem(DbUtil.DataType.CATEGORY, key, listener);
    }

    /**
     * Method to get a specific category from the database, based on the category name
     * @param name the name associated with the desired category
     * @param listener the listener that will handle the success/failure of this operation
     */
    public static void getCategoryByName(@NonNull String name, @NonNull final AsyncSingleValueEventListener<Category> listener) {
        DbQuery query = DbQuery.createChildValueQuery("unique_name").setEqualsFilter(Category.getUniqueName(name));
        DbUtil.getItems(DbUtil.DataType.CATEGORY, query, new AsyncValueEventListener<Category>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Category> data) {
                if (data.size() == 1) { listener.onSuccess(data.get(0)); }
                else if (data.size() == 0) { listener.onFailure(AsyncEventFailureReason.DOES_NOT_EXIST); }
                else { listener.onFailure(AsyncEventFailureReason.NOT_UNIQUE); }
            }
            @Override
            public void onFailure(@NonNull AsyncEventFailureReason reason) { listener.onFailure(reason); }
        });
    }

    /**
     * Method to get all the categories from the database. Date updates in real-time in response to
     * any changes made
     *
     * @param listener the listener that will handle the success/failure of this operation
     * @return a DbListenerHandle that handles the ValueEventListener attached to the database
     */
    @NonNull
    public static DbListenerHandle<?> getCategoriesLive(@NonNull final AsyncValueEventListener<Category> listener) {
        DbQuery query = DbQuery.createChildValueQuery("unique_name");
        return DbUtil.getItemsLive(DbUtil.DataType.CATEGORY, query, listener);
    }

}
