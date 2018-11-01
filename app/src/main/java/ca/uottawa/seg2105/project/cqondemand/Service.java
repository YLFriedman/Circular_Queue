package ca.uottawa.seg2105.project.cqondemand;

import java.util.ArrayList;

public class Service {

    public Category category;
    public ArrayList<User> Users;
    public static double rate;

    /*
    *Constructor, checks that category is a category and that rate is a double
    *
    * @param, takes Category cat, double rat, and single User user as input
     */
    public void Service(Category cat, double rat, User user){
            this.Users = new ArrayList<User>();
            this.Users.add(user);
            this.category = cat;
            this.rate =rat;
    }

    /*
     *returns user of service, else returns null if no user found
     *
     * @param; user to return
     */
    public User getUser(User user){
        if(this.Users.contains(user)) {
            int i;
            i = this.Users.indexOf(user);
            return this.Users.get(i);
        }else{
            return null;
        }
    }

    /*
     *returns Users of service
     */
    public ArrayList<User> getUsers(){
        return this.Users;
    }

    /*
     *returns category of service
     */
    public Category getCategory(){
        return this.category;
    }

    /*
     *returns hourly rate of service
     */
    public double getRate(){
        return this.rate;
    }

    /*
     *adds user to user arraylist
     *
     * @param, takes single User user as input
     */
    public void addUser(User user){
        this.Users.add(user);
    }

    /*
     *removes user from user arraylist
     *
     * @param, takes single User user as input
     */
    public void removeUser(User user){
        this.Users.remove(user);
    }

    /*
     *changes rate of service
     *
     * @param, takes double newrate as input
     */
    public void editRate(double newRate){
        this.rate = newRate;
    }

    /*
     *changes category of service
     *
     * @param, takes a category as input
     */
    public void editCategory(Category newCategory){
        this.category = newCategory;
    }
}
