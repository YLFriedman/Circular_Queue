package ca.uottawa.seg2105.project.cqondemand.database;

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

    public Category toDomainObj() { return new Category(name); }

    public String generateKey() { return DbUtil.getSanitizedKey(name); }

    public static void createCategory(Category category, final AsyncActionEventListener listener) {
        DbUtil.createItem(category, listener);
    }

    public static void updateCategory(final Category oldCategory, final Category newCategory, final AsyncActionEventListener listener) {
        if (DbUtil.getKey(oldCategory).equals(DbUtil.getKey(newCategory))) {
            DbUtil.updateItem(newCategory, listener);
        } else {
            DbUtil.updateItem(oldCategory, newCategory, listener);
        }
    }

    public static void deleteCategory(Category category, final AsyncActionEventListener listener) {
        DbUtil.deleteItem(category, listener);
    }

    public static void getCategory(String name, final AsyncSingleValueEventListener<Category> listener) {
        DbUtil.getItem(DbUtil.DataType.CATEGORY, name, listener);
    }

    public static void getCategories(final AsyncValueEventListener<Category> listener) {
        DbUtil.getItems(DbUtil.DataType.CATEGORY, listener);
    }

}
