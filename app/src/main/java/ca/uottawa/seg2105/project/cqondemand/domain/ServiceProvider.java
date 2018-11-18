package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ServiceProvider extends User {

    protected String companyName;

    protected boolean licensed;

    protected String phoneNumber;

    protected Address address;

    protected String description;

    public ServiceProvider(@NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull String pass,
                           @NonNull String companyName, boolean licenced, @NonNull String phoneNumber, @NonNull Address address, String description) {

        super(firstName, lastName, username, email, Type.SERVICE_PROVIDER, pass);

        this.companyName = companyName;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.description = description;
    }

    /**
     * CONSTRUCTOR WITH KEY
     * @param key
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param pass
     * @param companyName
     * @param licenced
     * @param phoneNumber
     * @param address
     */
    public ServiceProvider(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull String pass,
                           @NonNull String companyName, boolean licenced, @NonNull String phoneNumber, @NonNull Address address, @Nullable String description) {

        super(key, firstName, lastName, username, email, Type.SERVICE_PROVIDER, pass);

        this.companyName = companyName;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.description = description;
    }


    public String getCompanyName(){ return companyName; }

    public boolean isLicensed(){ return licensed; }

    public String getPhoneNumber(){ return phoneNumber; }

    /**
     * Returns the address object at index 0 of the service provider
     * @return Address object at index 0 of address array list
     */
    public Address getAddress(){ return address; }

    public String getDescription(){
        return description;
    }

}
