package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

import java.io.Serializable;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

public class Address implements Serializable {

    private static final long serialVersionUID = 1;
    protected String unit;
    protected int streetNumber;
    protected String street;
    protected String city;
    protected String country;
    protected String province;
    protected String postalCode;

    protected static final String[] PROVINCES_SHORT = { "AB", "BC", "MB", "NB", "NL", "NT", "NS", "NU", "ON", "PE", "QC", "SK", "YT" };
    public static final String[] PROVINCES = { "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Northwest Territories",
                                                "Nova Scotia", "Nunavut", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon" };

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

    public String getUnit() {
        return unit;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

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

    @Override
    public String toString() {
        String output = "";
        if (!unit.isEmpty()) { output += unit + "-"; }
        output += streetNumber + " " + street;
        output += "\n" + city + " " + getShortProvince() + " " + postalCode;
        return output;
    }

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
