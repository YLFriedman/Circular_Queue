package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;

public class DbAddress extends DbItem<Address> {

    public String unit;
    public int street_number;
    public String street;
    public String city;
    public String country;
    public String postal_code;

    public DbAddress() {}

    DbAddress(Address address) {
        unit = address.getUnit();
        street_number = address.getStreetNumber();
        street = address.getStreet();
        city = address.getCity();
        country = address.getCountry();
        postal_code = address.getPostalCode();
    }

    @NonNull
    public Address toDomainObj() { return new Address(key, unit, street_number, street, city, country, postal_code); }

}