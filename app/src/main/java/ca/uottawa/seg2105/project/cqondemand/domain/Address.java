package ca.uottawa.seg2105.project.cqondemand.domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

/**
 * Class to represent a real world address, implements serializable so as to be better suited for Intents
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class Address implements Serializable {

    /**
     * Stores the class version for serialization and de-serialization
     */
    private static final long serialVersionUID = 1;
    /**
     * Stores the Unit number
     */
    protected String unit;
    /**
     * Stores the street number
     */
    protected int streetNumber;
    /**
     * Stores the street name
     */
    protected String street;
    /**
     * Stores the city name
     */
    protected String city;
    /**
     * Stores the country name
     */
    protected String country;
    /**
     * Stores the province name
     */
    protected String province;
    /**
     * Stores the postal code
     */
    protected String postalCode;

    //List to store province names in full and shortened versions
    protected static final String[] PROVINCES_SHORT = { "AB", "BC", "MB", "NB", "NL", "NT", "NS", "NU", "ON", "PE", "QC", "SK", "YT" };
    public static final String[] PROVINCES = { "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Northwest Territories",
                                                "Nova Scotia", "Nunavut", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon" };

    /**
     * Constructor for new Address object. Requires valid and non-null inputs for all fields, also assumes that the address is Canadian.
     *
     * @param unit The unit associated with this address
     * @param streetNumber The street number associated with this address
     * @param street The street name associated with this address
     * @param city The city name associated with this address
     * @param province The province associated with this address
     * @param country The country associated with this address
     * @param postalCode The postal code associated with this address
     */
    public Address(@NonNull String unit, @NonNull int streetNumber, @NonNull String street, @NonNull String city, @NonNull String province, @NonNull String country, @NonNull String postalCode) {
        if (!FieldValidation.unitIsValid(unit)) { throw new InvalidDataException("Invalid unit. Legal characters: " + FieldValidation.ADDRESS_UNIT_CHARS); }
        if (!FieldValidation.streetNumberIsValid(streetNumber)) { throw new InvalidDataException("Invalid streetNumber. Must be greater than 0. "); }
        if (!FieldValidation.streetNameIsValid(street)) { throw new InvalidDataException("Invalid street. Legal characters: " + FieldValidation.STREET_NAME_CHARS); }
        if (!FieldValidation.cityNameIsValid(city)) { throw new InvalidDataException("Invalid city. Legal characters: " + FieldValidation.CITY_NAME_CHARS); }
        if (!FieldValidation.provinceNameIsValid(province)) { throw new InvalidDataException("Invalid province. Legal characters: " + FieldValidation.PROVINCE_NAME_CHARS); }
        if (!FieldValidation.countryNameIsValid(country)) { throw new InvalidDataException("Invalid country. Legal characters: " + FieldValidation.COUNTRY_NAME_CHARS); }
        if (!FieldValidation.postalCodeIsValid(postalCode)) { throw new InvalidDataException("Invalid postalCode. User Canadian format."); }
        this.unit = unit.toUpperCase();
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.country = country;
        this.province = province;
        this.postalCode = postalCode.length() == 7 ? postalCode : (postalCode.substring(0, 3) + " " + postalCode.substring(3));
        this.postalCode.toUpperCase();
    }

    /**
     * Gets the unit of a particular address
     * @return the unit associated with this address
     */
    public String getUnit() {
        return unit;
    }

    /**
     *Gets the street number of a particular address
     * @return the street number associated with this address
     */
    public int getStreetNumber() {
        return streetNumber;
    }

    /**
     *Gets the street name of a particular address
     * @return the street name associated with this address
     */

    public String getStreet() {
        return street;
    }

    /**
     *Gets the city of a particular address
     * @return the city associated with this address
     */
    public String getCity() {
        return city;
    }

    /**
     *Gets the country of a particular address
     * @return the country associated with this address
     */
    public String getCountry() {
        return country;
    }

    /**
     *Gets the province of a particular address
     * @return the province associated with this address
     */
    public String getProvince() {
        return province;
    }

    /**
     *Gets the postal code of a particular address
     * @return the postal code associated with this address
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     *Checks if this address is the same as another object
     * @param otherObj the object this address will be compared to
     * @return
     */
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Address)) { return false; }
        if (this == otherObj) { return true; }
        Address other = (Address) otherObj;
        if ((null == unit)       != (null == other.unit)       || (null != unit       && !unit.equals(other.unit))) { return false; }
        if ((null == street)     != (null == other.street)     || (null != street     && !street.equals(other.street))) { return false; }
        if ((null == city)       != (null == other.city)       || (null != city       && !city.equals(other.city))) { return false; }
        if ((null == country)    != (null == other.country)    || (null != country    && !country.equals(other.country))) { return false; }
        if ((null == province)   != (null == other.province)   || (null != province   && !province.equals(other.province))) { return false; }
        if ((null == postalCode) != (null == other.postalCode) || (null != postalCode && !postalCode.equals(other.postalCode))) { return false; }
        return streetNumber == other.streetNumber;
    }

    /**
     *Returns a string representation of this address
     * @return a string representation of this address
     */
    @Override
    public String toString() {
        String output = "";
        if (!unit.isEmpty()) { output += unit + "-"; }
        output += streetNumber + " " + street;
        output += "\n" + city + " " + getShortProvince() + ", " + postalCode;
        return output;
    }

    /**
     *Gets a shortened version of this address' province
     * @return a shortened version of this address' province
     */
    public String getShortProvince() {
        String compare = province.toLowerCase();
        for (int i = 0; i < PROVINCES.length; i++) {
            if (PROVINCES[i].toLowerCase().equals(compare)) {
                return PROVINCES_SHORT[i];
            }
        }
        return province;
    }

}
