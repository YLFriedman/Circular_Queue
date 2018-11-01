package ca.uottawa.seg2105.project.cqondemand;

import java.util.ArrayList;

public class Category{

    public static String CategoryTypeName;
    public ArrayList<Service> services;

    /*
     *Constructor, checks that categorytype is a string and service is a service
     *
     * @param, takes String CategoryType and Service service
     */
    public void Category(String CategoryType, Service service){
        if(CategoryType instanceof String){
            System.out.print("Invalid input for Category name");
        }else {
            this.CategoryTypeName = CategoryType;
            this.services = new ArrayList<Service>();
            this.services.add(service);
        }
    }

    /*
     *sets name of category
     *
     * @param, takes String newName
     */
    public void setCategoryTypeName(String newName){
        this.CategoryTypeName = newName;
    }

    /*
     *add service to category
     *
     * @param; Service service
     */
    public void addService(Service service){
        this.services.add(service);
    }

    /*
     *removes service from service arraylist
     *
     * @param, takes service as input
     */
    public void removeUser(Service service){
        this.services.remove(service);
    }

    /*
     *gets name of category
     */
    public String getCategoryTypeName(){
        return this.CategoryTypeName;
    }

    /*
     *returns service of category, else returns null if no user found
     *
     * @param; Service service to return
     */
    public Service getService(Service service){
        if(this.services.contains(service)) {
            int i;
            i = this.services.indexOf(service);
            return this.services.get(i);
        }else{
            return null;
        }
    }

    /*
     *returns Services
     */
    public ArrayList<Service> getServices(){
        return this.services;
    }
}
