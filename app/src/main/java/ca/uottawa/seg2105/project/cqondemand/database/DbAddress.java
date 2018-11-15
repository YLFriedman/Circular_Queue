package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;

public class DbAddress extends DbItem<Address> {

    protected String unit;
    protected int street_number;
    protected String street;
    protected String city;
    protected String country;
    protected String postal_code;

    public DbAddress() {}

    public DbAddress(Address address) {
        unit = address.getUnit();
        street_number = address.getStreetNumber();
        street = address.getStreet();
        city = address.getCity();
        country = address.getCountry();
        postal_code = address.getPostalCode();
    }

    @NonNull
    public Address toDomainObj() { return new Address(unit, street_number, street, city, country, postal_code); }

    @NonNull
    public String generateKey() { return DbUtil.getSanitizedKey(""); }

}