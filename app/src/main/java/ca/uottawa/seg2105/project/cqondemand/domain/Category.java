package ca.uottawa.seg2105.project.cqondemand.domain;

import java.util.ArrayList;

public class Category{

    private String name;
    private ArrayList<Service> services;

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
    public Category(String name, ArrayList<Service> services){
            this.name = name;
            this.services = services;
    }

    /**
     *Sets name of category
     *
     * @param, takes String newName
     */
    public void setName(String newName){
        this.name = newName;
    }

    /**
     * Setter for the services associated with this category
     *
     * @param services an ArrayList of Services to be associated with this category
     */

    public void setServices(ArrayList<Service> services) {
        this.services = services;
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
     *returns the list of services associated with this Category
     *
     * @return an ArrayList of Services associated with this Category
     */
    public ArrayList<Service> getServices(){
        return this.services;
    }
}
