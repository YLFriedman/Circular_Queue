package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class ServiceProvider extends User {

    protected String companyName;

    protected boolean licensed;

    protected String phoneNumber;

    protected ArrayList<Address> address;   //address at index 0 can be seen as primary address

    public ServiceProvider(@NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Types type, @NonNull String pass,
                           @NonNull String companyName, @NonNull boolean licenced, @NonNull String phoneNumber, @NonNull Address address) {

        super(firstName, lastName, username, email, type, pass);

        this.companyName = companyName;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.address.add(address);
    }

    /**
     * CONSTRUCTOR WITH KEY
     * @param key
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param type
     * @param pass
     * @param companyName
     * @param licenced
     * @param phoneNumber
     * @param address
     */
    public ServiceProvider(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Types type, @NonNull String pass,
                           @NonNull String companyName, @NonNull boolean licenced, @NonNull String phoneNumber, @NonNull Address address) {

        super(key, firstName, lastName, username, email, type, pass);

        this.companyName = companyName;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.address.add(address);
    }


    public String getCompanyName(){ return companyName; }

    public boolean getlicensed(){ return licensed; }

    public String getphoneNumber(){ return phoneNumber; }

    /**
     * Returns the address object at index 0 of the service provider
     * @return Address object at index 0 of address array list
     */
    public Address getprimaryAddress(){ return address.get(0); }

    /**
     * returns the arraylist of addresses of the service provider
     * @return ArrayList<Address> of service provider
     */
    public ArrayList<Address> getAllAddress() { return address; }

    /**
     * inserts an address object into the address array list in service provider
     * @param address to be inserted into the array list
     */
    public void addAddress(Address address){ this.address.add(address); }

    /**
     * Inserts an address (primary address) object at index 0 of the Address list
     * @param address the primary address to be inserted
     */
    public void addPrimaryAddress(Address address){ this.address.add(0, address); }

    /**
     * UNCAUGHT EXCEPTION WHEN REMOVING OUT OF BOUNDS ERROR, check before calling
     * Removed the first(primary) address of the service provider
     */
    public void removePrimaryAddress() { address.remove(0); }

    /**
     * UNCAUGHT EXCEPTION WHEN REMOVING AN OUT OF BOUNDS ERROR, check before calling
     * Removes the address at given index
     * @param n the index to remove the stored address
     */
    public void removeAddressAtIndex(int n){ address.remove(n); }

    /**
     * returns the amount of addresses the service provider has
     * @return number of addresses as int
     */
    public int getNumAddress() { return address.size(); }

}
