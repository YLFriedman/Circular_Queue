package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

public class DbCategory extends DbItem<Category> {

    public String unique_name;
    public String name;

    public DbCategory() {}

    public DbCategory(Category item) {
        super(item.getKey());
        unique_name = item.getUniqueName();
        name = item.getName();
    }

    @NonNull
    public Category toDomainObj() { return new Category(getKey(), name); }

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

    public static void deleteCategory(@NonNull Category category, @Nullable AsyncActionEventListener listener) {
        if (null == category.getKey() || category.getKey().isEmpty()) { throw new IllegalArgumentException("A category object with a key is required. Unable to delete from the database without the key."); }
        DbUtil.deleteItem(category, listener);
    }

    public static void getCategory(@NonNull String key, @NonNull AsyncSingleValueEventListener<Category> listener) {
        DbUtil.getItem(DbUtil.DataType.CATEGORY, key, listener);
    }

    public static void getCategoryByName(@NonNull String name, @NonNull final AsyncSingleValueEventListener<Category> listener) {
        DbUtil.getItems(DbUtil.DataType.CATEGORY, "unique_name", Category.getUniqueName(name), new AsyncValueEventListener<Category>() {
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

    public static void getCategories(@NonNull AsyncValueEventListener<Category> listener) {
        DbUtil.getItems(DbUtil.DataType.CATEGORY, listener);
    }

    @NonNull
    public static DbListenerHandle<?> getCategoriesLive(@NonNull final AsyncValueEventListener<Category> listener) {
        return DbUtil.getItemsLive(DbUtil.DataType.CATEGORY, listener);
    }

}
