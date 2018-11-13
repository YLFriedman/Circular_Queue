package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncSingleValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;

public class DbCategory extends DbItem<Category> {

    public String name;

    public DbCategory() {}

    public DbCategory(Category category) {
        name = category.getName();
    }

    @NonNull
    public Category toDomainObj() { return new Category(name); }

    @NonNull
    public String generateKey() { return DbUtil.getSanitizedKey(name); }

    public static void createCategory(@NonNull Category category, @Nullable AsyncActionEventListener listener) {
        DbUtil.createItem(category, listener);
    }

    public static void updateCategory(@NonNull Category oldCategory, @NonNull Category newCategory, @Nullable AsyncActionEventListener listener) {
        if (DbUtil.getKey(oldCategory).equals(DbUtil.getKey(newCategory))) {
            DbUtil.updateItem(newCategory, listener);
        } else {
            DbUtil.updateItem(oldCategory, newCategory, listener);
        }
    }

    public static void deleteCategory(@NonNull Category category, @Nullable AsyncActionEventListener listener) {
        DbUtil.deleteItem(category, listener);
    }

    public static void getCategory(@NonNull String name, @NonNull AsyncSingleValueEventListener<Category> listener) {
        DbUtil.getItem(DbUtil.DataType.CATEGORY, name, listener);
    }

    public static void getCategories(@NonNull AsyncValueEventListener<Category> listener) {
        DbUtil.getItems(DbUtil.DataType.CATEGORY, listener);
    }

}
