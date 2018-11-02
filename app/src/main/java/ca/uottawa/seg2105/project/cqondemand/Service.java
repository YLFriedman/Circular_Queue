package ca.uottawa.seg2105.project.cqondemand;

import java.util.ArrayList;

public class Service {

    private String category;
    private ArrayList<String> serviceProviderIDs;
    private String name;
    private double rate;


    /**
     * Empty constructor for Firebase Use
     */

    public Service(){

    }
    /**
     * Constructor for a new service Object
     * @param name the name of this Service
     * @param category The category that this service falls under
     * @param rate The rate per hour that this service costs
     * @param serviceProviders an ArrayList of Users that provide this service, must be of type Service Provider
     */
    public Service(String category, double rate, ArrayList<User> serviceProviders, String name){
            this.serviceProviderIDs = new ArrayList<>();
            for(User user : serviceProviders){
                if(!(user.getType() == User.Types.SERVICE_PROVIDER)){
                    throw new IllegalArgumentException("Only Service Providers can provide a service");
                }
                serviceProviderIDs.add(user.getUserName());
            }

            this.category = category;
            this.rate = rate;
            this.name = name;
    }

    /**
     *Returns an ArrayList of all the service providers that provide this service
     *
     * @return an ArrayList of the service providers that provide this service
     */

    public ArrayList<String> getServiceProviderIDs(){
        return this.serviceProviderIDs;

    }


    /**
     *Returns the category associated with this service
     *
     * @return the associated category
     */
    public String getCategory(){
        return this.category;
    }

    /**
     *Returns the rate per hour of this service
     *
     * @return the rate associated with this Service
     */
    public double getRate(){
        return this.rate;
    }

    /**
     * Getter for the name of this Service
     *
     * @return the name of this service
     */

    public String getName(){
        return this.name;
    }



    /**
     *Sets the rate per hour of this service
     *
     * @param newRate the new rate for this service
     */
    public void setRate(double newRate){
        this.rate = newRate;
    }

    /**
     *Set the category associated with this service
     *
     * @param newCategory the category to be associated with this service
     */

    public void setCategory(String newCategory){
        this.category = newCategory;
    }

    /**
     * Setter for the serviceProvider field
     *
     * @param serviceProviders an ArrayList of users to represent the providers of this service,
     *                         throws an exception if any User in the list is not of type SERVICE_PROVIDER
     */

    public void setServiceProviders(ArrayList<User> serviceProviders){
        ArrayList<String> newIDs = new ArrayList<>();
        for(User user : serviceProviders){
            if(user.getType() != User.Types.SERVICE_PROVIDER) {
                throw new IllegalArgumentException("Only a service provider can provide a service!");
            }
            newIDs.add(user.getUserName());
        }
        this.serviceProviderIDs = newIDs;

    }

    public void setName(String newName){
        this.name = newName;
    }
}
