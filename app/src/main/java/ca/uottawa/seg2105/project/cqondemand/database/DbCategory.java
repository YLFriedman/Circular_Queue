package ca.uottawa.seg2105.project.cqondemand.database;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;

public class DbCategory extends DbItem<Category> {

    public String name;

    public DbCategory() {}

    public DbCategory(Category category) {
        name = category.getName();
    }

    public Category toItem() { return new Category(name); }

    public String generateKey() { return DbUtil.getSanitizedKey(name); }

}
