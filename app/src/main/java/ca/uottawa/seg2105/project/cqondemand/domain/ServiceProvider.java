package ca.uottawa.seg2105.project.cqondemand.domain;

import android.support.annotation.NonNull;

public class ServiceProvider extends User {

    protected String companyName;

    protected boolean licensed;

    protected String phoneNumber;

    protected String unit;

    protected String streetNumber;

    protected String streetName;

    protected String city;

    protected String country;

    protected String postalCode;

    public ServiceProvider(@NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Types type, @NonNull String pass,
                           @NonNull String companyName, @NonNull boolean licenced, @NonNull String phoneNumber, @NonNull String unit, @NonNull String streetNumber, @NonNull String streetName,
                           @NonNull String city, @NonNull String country, @NonNull String postalCode) {

        super(firstName, lastName, username, email, type, pass);

        this.companyName = companyName;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.unit = unit;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public ServiceProvider(@NonNull String key, @NonNull String firstName, @NonNull String lastName, @NonNull String username, @NonNull String email, @NonNull Types type, @NonNull String pass,
                           @NonNull String companyName, @NonNull boolean licenced, @NonNull String phoneNumber, @NonNull String unit, @NonNull String streetNumber, @NonNull String streetName,
                           @NonNull String city, @NonNull String country, @NonNull String postalCode) {

        super(key, firstName, lastName, username, email, type, pass);

        this.companyName = companyName;
        this.licensed = licenced;
        this.phoneNumber = phoneNumber;
        this.unit = unit;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }
}
