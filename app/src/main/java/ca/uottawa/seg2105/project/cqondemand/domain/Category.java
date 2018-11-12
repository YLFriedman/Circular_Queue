package ca.uottawa.seg2105.project.cqondemand.domain;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class Category {

    private String name;

    /**
     *Constructor for the Category object
     *
     * @param name the string that corresponds to this category
     * @paran services the services that will be associated with this category
     */
    public Category(String name) {
        if (!FieldValidation.categoryNameIsValid(name)) {
            throw new InvalidDataException("Invalid Category Name. " + FieldValidation.ILLEGAL_CATEGORY_NAME_CHARS_MSG);
        }
        this.name = name;
    }

    /**
     *Getter for the name of this category
     *
     * @return String name of this category
     */
    public String getName(){
        return name;
    }

    public boolean equals(Category other) {
        return null != other && name.equals(other.name);
    }

}
