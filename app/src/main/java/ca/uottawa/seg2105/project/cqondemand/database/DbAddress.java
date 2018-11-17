package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;

public class DbAddress extends DbItem<Address> {

    public String unit;
    public int street_number;
    public String street;
    public String city;
    public String country;
    public String province;
    public String postal_code;

    public DbAddress() {}

    DbAddress(Address item) {
        unit = item.getUnit();
        street_number = item.getStreetNumber();
        street = item.getStreet();
        city = item.getCity();
        province = item.getProvince();
        country = item.getCountry();
        postal_code = item.getPostalCode();
    }

    @NonNull
    public Address toDomainObj() { return new Address(unit, street_number, street, city, province, country, postal_code); }

}