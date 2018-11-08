package ca.uottawa.seg2105.project.cqondemand.domain;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncActionEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncEventFailureReason;
import ca.uottawa.seg2105.project.cqondemand.utilities.AsyncValueEventListener;
import ca.uottawa.seg2105.project.cqondemand.utilities.DbUtil;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;
import ca.uottawa.seg2105.project.cqondemand.utilities.State;

public class Category{

    public static final String ILLEGAL_CATEGORY_NAME_CHARS_REGEX = ".*[^a-zA-Z-].*";
    public static final String ILLEGAL_CATEGORY_NAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z -";
    private String name;

    /**
     * Empty constructor for Firebase uses
     */
    public Category() {
    }

    /**
     *Constructor for the Category object
     *
     * @param name the string that corresponds to this category
     * @paran services the services that will be associated with this category
     */
    public Category(String name){
            if(!nameIsValid(name)){
                throw new InvalidDataException("Invalid Category Name " + ILLEGAL_CATEGORY_NAME_CHARS_MSG);
            }
            this.name = name;

    }


    /**
     *Getter for the name of this category
     *
     * @return String name of this category
     */
    public String getName(){
        return this.name;
    }




    /**
     *
     * @param listener
     */
    public static void getCategories(final AsyncValueEventListener<Category> listener) {
        DbUtil.getItems(DbUtil.DataType.CATEGORY, listener);
    }

    public void create(final AsyncActionEventListener listener) {
        DbUtil.createItem(this, listener);
    }

    public void update(final Category newCategory, final AsyncActionEventListener listener) {

        if (DbUtil.getKey(this).equals(DbUtil.getKey(newCategory))) {
            DbUtil.updateItem(newCategory, listener);
        } else {
            DbUtil.updateItem(this, newCategory, listener);
        }
    }

    public void delete(final AsyncActionEventListener listener) {
        DbUtil.deleteItem(this, listener);
    }

    public static void getCategory(String name, final AsyncValueEventListener<Category> listener){
        DbUtil.getItem(DbUtil.DataType.CATEGORY, name, listener);
    }

    public static boolean nameIsValid(String name){
        if(name == null || name.isEmpty()) { return false;  }
        return name.matches(ILLEGAL_CATEGORY_NAME_CHARS_REGEX);
    }

}