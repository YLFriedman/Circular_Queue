package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class ServiceProvider extends User implements Serializable {

    private static final long serialVersionUID = 1;

    protected String companyName;

    protected String description;

    protected boolean licensed;

    protected String phoneNumber;

    protected Address address;

    public ServiceProvider(@NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull String password,
                           @NonNull String companyName, boolean licenced, @NonNull String phoneNumber, @NonNull Address address, String description) {
        super(firstName, lastName, username, email, Type.SERVICE_PROVIDER, password);
        if (!FieldValidation.companyNameIsValid(companyName)) { throw new InvalidDataException("Invalid companyName. Legal characters: " + FieldValidation.COMPANY_NAME_CHARS); }
        if (null != phoneNumber && !phoneNumber.equals("{@TEST}") && !FieldValidation.phoneIsValid(phoneNumber)) { throw new InvalidDataException("Invalid phoneNumber. "); }
        if (null == address) { throw new InvalidDataException("The address cannot be null."); }

        this.companyName = companyName;
        this.description = description;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    /**
     * CONSTRUCTOR WITH KEY
     * @param key
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     * @param companyName
     * @param licenced
     * @param phoneNumber
     * @param address
     */
    public ServiceProvider(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull String password,
                           @NonNull String companyName, boolean licenced, @NonNull String phoneNumber, @NonNull Address address, @Nullable String description) {
        this(firstName, lastName, username, email, password, companyName, licenced, phoneNumber, address, description);
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
    }

    public String getCompanyName() {
        return companyName;
    }

    public boolean isLicensed() {
        return licensed;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public String getDescription(){
        return description;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof ServiceProvider)) { return false; }
        if (this == otherObj) { return true; }
        ServiceProvider other = (ServiceProvider) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        if (!super.equals(otherObj)) { return false; }
        if ((null == companyName) != (null == other.companyName) || (null != companyName && !companyName.equals(other.companyName))) { return false; }
        if ((null == phoneNumber)  != (null == other.phoneNumber)  || (null != phoneNumber  && !phoneNumber.equals(other.phoneNumber))) { return false; }
        if ((null == address)     != (null == other.address)     || (null != address     && !address.equals(other.address))) { return false; }
        if ((null == description)  != (null == other.description)  || (null != description  && !description.equals(other.description))) { return false; }
        return licensed == other.licensed;
    }

}
