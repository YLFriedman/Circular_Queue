package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

public class Address {

    protected String unit;
    protected int streetNumber;
    protected String street;
    protected String city;
    protected String country;
    protected String province;
    protected String postalCode;

    public static final String[] PROVINCES = { "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Northwest Territories",
            "Nova Scotia", "Nunavut", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon" };
    protected static final String[] PROVINCES_SHORT = { "AB", "BC", "MB", "NB", "NL", "NT", "NS", "NU", "ON", "PE", "QC", "SK", "YT" };

    public Address(@NonNull String unit, @NonNull int streetNumber, @NonNull String street, @NonNull String city, @NonNull String province, @NonNull String country, @NonNull String postalCode) {
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
