package ca.uottawa.seg2105.project.cqondemand;

import java.util.ArrayList;

public class Service {

    private Category category;
    private ArrayList<User> serviceProviders;
    private double rate;


    /**
     * Empty constructor for Firebase Use
     */

    public Service(){

    }
    /**
     * Constructor for a new service Object
     * @param category The category that this service falls under
     * @param rate The rate per hour that this service costs
     * @param serviceProviders an ArrayList of Users that provide this service, must be of type Service Provider
     */
    public Service(Category category, double rate, ArrayList<User> serviceProviders){
            for(User user : serviceProviders){
                if(!(user.getType() == User.Types.SERVICE_PROVIDER)){
                    throw new IllegalArgumentException("Only Service Providers can provide a service");
                }
            }
            this.serviceProviders = serviceProviders;
            this.category = category;
            this.rate =rate;
    }

    /**
     *Returns an ArrayList of all the service providers that provide this service
     *
     * @return an ArrayList of the service providers that provide this service
     */

    public ArrayList<User> getServiceProviders(){
        return this.serviceProviders;

    }


    /**
     *Returns the category associated with this service
     *
     * @return the associated category
     */
    public Category getCategory(){
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
     *adds user to the list of ServiceProviders
     *
     *@param, takes single User user as input
     */
    public void addProvider(User user){
        if(user.getType() != User.Types.SERVICE_PROVIDER){
            throw new IllegalArgumentException("Only a Service Provider can provide a service!");
        }

        serviceProviders.add(user);
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

    public void setCategory(Category newCategory){
        this.category = newCategory;
    }

    /**
     * Setter for the serviceProvider field
     *
     * @param serviceProviders an ArrayList of users to represent the providers of this service,
     *                         throws an exception if any User in the list is not of type SERVICE_PROVIDER
     */

    public void setServiceProviders(ArrayList<User> serviceProviders){
        for(User user : serviceProviders){
            if(user.getType() != User.Types.SERVICE_PROVIDER) {
                throw new IllegalArgumentException("Only a service provider can provide a service!");
            }
        }
        this.serviceProviders = serviceProviders;

    }
}
