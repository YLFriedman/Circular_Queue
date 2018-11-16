package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

public class Address {

    protected String key;
    protected String unit;
    protected int streetNumber;
    protected String street;
    protected String city;
    protected String country;
    protected String postalCode;

    public Address(@NonNull String unit, @NonNull int streetNumber, @NonNull String street, @NonNull String city, @NonNull String country, @NonNull String postalCode) {
        this.unit = unit;
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Address(@NonNull String key, @NonNull String unit, @NonNull int streetNumber, @NonNull String street, @NonNull String city, @NonNull String country, @NonNull String postalCode) {
        this.key = key;
        this.unit = unit;
        this.streetNumber = streetNumber;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getKey() {
        return key;
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

    public String getPostalCode() {
        return postalCode;
    }

    public boolean equals(Address other) {
        return key != null && key.equals(other.key);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Address)) { return false; }
        if (this == otherObj) { return true; }
        Address other = (Address) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        if ((null == unit)       != (null == other.unit)       || (null != unit       && !unit.equals(other.unit))) { return false; }
        if ((null == street)     != (null == other.street)     || (null != street     && !street.equals(other.street))) { return false; }
        if ((null == city)       != (null == other.city)       || (null != city       && !city.equals(other.city))) { return false; }
        if ((null == country)    != (null == other.country)    || (null != country    && !country.equals(other.country))) { return false; }
        if ((null == postalCode) != (null == other.postalCode) || (null != postalCode && !postalCode.equals(other.postalCode))) { return false; }
        return streetNumber == other.streetNumber;
    }

}
