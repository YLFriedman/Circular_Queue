package ca.uottawa.seg2105.project.cqondemand.database;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

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

    DbAddress(Map<Object, Object> map){
        unit = (String) map.get("unit");
        street_number = (Integer) map.get("street_number");
        street = (String) map.get("street");
        city = (String) map.get("city");
        country = (String) map.get("country");
        postal_code = (String) map.get("postal_code");
    }

    @NonNull
    public Address toDomainObj() { return new Address(unit, street_number, street, city, country, postal_code); }

    public Map<Object, Object> toMap(){
        Map<Object, Object> map = new HashMap<>();
        map.put("unit", unit);
        map.put("street_number", street_number);
        map.put("street", street);
        map.put("city", city);
        map.put("country", country);
        map.put("postal_code", postal_code);
        return map;
    }

}