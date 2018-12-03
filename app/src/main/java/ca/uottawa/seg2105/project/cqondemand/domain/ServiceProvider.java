package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * Class to represent a Service Provider, extends the User class.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class ServiceProvider extends User implements Serializable {

    /**
     * Stores the class version for serialization and de-serialization
     */
    private static final long serialVersionUID = 1;
    /**
     * Stores the name of the service provider's company
     */
    protected String companyName;
    /**
     * Stores the service provider's description
     */
    protected String description;
    /**
     * Stores the license status of the service provider
     */
    protected boolean licensed;
    /**
     * Stores the phone number of the service provider
     */
    protected String phoneNumber;
    /**
     * Stores the address of the service provider
     */
    protected Address address;
    /**
     * Stores the overall rating of the service provider
     */
    protected int rating;
    /**
     * Stores the running total of ratings of the service provider
     */
    protected long runningRatingTotal;
    /**
     * Stores the number of ratings that are associated with the service provider
     */
    protected int numRatings;

    List<Availability> availabilities;

    /**
     * Constructor for a Service Provider object. Does not require a key, intended to be used before the
     * provider has been stored in the database.
     *
     * @param firstName the first name of the service provider
     * @param lastName the last name of the service provider
     * @param username the username of the service provider
     * @param email the email of the service provider
     * @param password the password of the service provider
     * @param companyName the name of the provider's Company
     * @param licenced whether or not the provider is licensed
     * @param phoneNumber the phone number of the provider
     * @param address the address of the provider
     * @param description An optional description of the provider
     */
    public ServiceProvider(@NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull String password,
                           @NonNull String companyName, boolean licenced, @NonNull String phoneNumber, @NonNull Address address, @Nullable String description) {
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
     * Constructor for a Service Provider object. Requires a key, intended to be used to convert database
     * information into an object.
     *
     * @param key the key associated with this service provider
     * @param firstName the first name of the service provider
     * @param lastName the last name of the service provider
     * @param username the username of the service provider
     * @param email the email of the service provider
     * @param password the password of the service provider
     * @param companyName the name of the provider's Company
     * @param licenced whether or not the provider is licensed
     * @param phoneNumber the phone number of the provider
     * @param address the address of the provider
     * @param description An optional description of the provider
     */
    public ServiceProvider(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull String password,
                           @NonNull String companyName, boolean licenced, @NonNull String phoneNumber, @NonNull Address address, @Nullable String description, int rating,
                           long runningRatingTotal, int numRatings, @Nullable List<Availability> availabilities) {
        this(firstName, lastName, username, email, password, companyName, licenced, phoneNumber, address, description);
        if (null == key || key.isEmpty()) { throw new InvalidDataException("The key cannot be null or empty."); }
        this.key = key;
        this.rating = rating;
        this.runningRatingTotal = runningRatingTotal;
        this.numRatings = numRatings;
        this.availabilities = availabilities;
    }

    /**
     * Getter fora particular service provider's company name
     * @return name of this provider's company
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Returns whether or not a particular provider is licensed
     * @return the license status of this provider
     */
    public boolean isLicensed() {
        return licensed;
    }

    /**
     * Gets the phone number associated with a particular service provider
     * @return the associated phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the Address associated with a particular service provider
     * @return the associated address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Gets the description associated with a particular service provider
     * @return the associated description
     */
    public String getDescription(){
        return description;
    }

    /**
     * Gets the user rating associated with a particular service provider
     * @return the associated user rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the running total of ratings associated with a particular service provider. Used for
     * averaging purposes.
     * @return the running total of ratings for this provider
     */
    public long getRunningRatingTotal() {
        return runningRatingTotal;
    }

    /**
     * Gets the number of ratings associated with a particular service provider. Used for averaging
     * purposes.
     * @return the number of ratings this provider has
     */
    public int getNumRatings() {
        return numRatings;
    }

    /**
     * Gets the availabilities associated with a particular service provider, in List form
     * @return a List of this provider's availabilities
     */
    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    /**
     * Sets the service provider's availabilities
     * @param availabilities List of availabilities
     */
    public void setAvailabilities(List<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    /**
     * Applies the rating to the provider's overall rating by adding the rating to the
     * running total and dividing by the total number of ratings.
     * @param newRating the rating value to be applied
     */
    public void applyRating(int newRating) {
        numRatings++;
        runningRatingTotal += newRating;
        float calcRating = (runningRatingTotal / (float) numRatings) * 100;
        rating = Math.round(calcRating);
    }

    /**
     * Method to determine if a particular ServiceProvider is the same as a given object
     * @param otherObj the object to be compared to
     * @return whether this service provider is equal to the given object
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof ServiceProvider)) { return false; }
        if (this == otherObj) { return true; }
        ServiceProvider other = (ServiceProvider) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        if (!super.equals(otherObj)) { return false; }
        if ((null == companyName) != (null == other.companyName) || (null != companyName && !companyName.equals(other.companyName))) { return false; }
        if ((null == phoneNumber) != (null == other.phoneNumber) || (null != phoneNumber && !phoneNumber.equals(other.phoneNumber))) { return false; }
        if ((null == address)     != (null == other.address)     || (null != address     && !address.equals(other.address))) { return false; }
        if ((null == description) != (null == other.description) || (null != description && !description.equals(other.description))) { return false; }
        return licensed == other.licensed;
    }

    public boolean isAvailable(Availability.Day day) {
        if (null == availabilities || availabilities.size() < 1) { return false; }
        for (Availability a: availabilities) {
            if (a.day == day) { return true; }
        }
        return false;
    }

    public boolean isAvailable(Availability.Day day, int startTime, int endTime) {
        if (null == availabilities || availabilities.size() < 1 || startTime < 0 || endTime > 24 || startTime >= endTime) { return false; }
        for (Availability a: availabilities) {
            if (a.day != day) { continue; }
            if (a.startTime <= startTime && endTime <= a.endTime) { return true; }
        }
        return false;
    }

}
