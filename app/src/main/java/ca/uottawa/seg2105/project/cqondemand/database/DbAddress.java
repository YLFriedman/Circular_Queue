package ca.uottawa.seg2105.project.cqondemand.database;

import androidx.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;

/**
 * The class <b> DbAddress </b> is a class used to take information from an Address object, and put it
 * into a form more easily stored in the database. Methods to go back and forth between Address and DbAddress
 * are also provided
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class DbAddress extends DbItem<Address> {

    /**
     * The unit name associated with an Address
     */
    public String unit;

    /**
     * The street number associated with an Address
     */
    public Integer street_number;

    /**
     * The street name associated with an Address
     */
    public String street;

    /**
     * The city name associated with an Address
     */
    public String city;

    /**
     * The country name associated with an Address
     */
    public String country;

    /**
     * The province name associated with an Address
     */
    public String province;

    /**
     * The postal code associated with an Address
     */
    public String postal_code;

    /**
     * Empty constructor, used by FireBase to add DbAddress objects
     */
    public DbAddress() {}

    /**
     * Creates a new DbAddress object based on an input Address object
     *
     * @param item Address to base the new DbAddress off of
     */
    public DbAddress(Address item) {
        unit = item.getUnit();
        street_number = item.getStreetNumber();
        street = item.getStreet();
        city = item.getCity();
        province = item.getProvince();
        country = item.getCountry();
        postal_code = item.getPostalCode();
    }

    /**
     * Method for converting a DbAddress into an Address
     *
     * @return the Address version of this DbAddress
     */
    @NonNull
    public Address toDomainObj() { return new Address(unit, street_number, street, city, province, country, postal_code); }

}