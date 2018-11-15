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

    DbAddress(Address item) {
        super(item.getKey());
        unit = item.getUnit();
        street_number = item.getStreetNumber();
        street = item.getStreet();
        city = item.getCity();
        country = item.getCountry();
        postal_code = item.getPostalCode();
    }

    @NonNull
    public Address toDomainObj() { return new Address(retrieveKey(), unit, street_number, street, city, country, postal_code); }

}