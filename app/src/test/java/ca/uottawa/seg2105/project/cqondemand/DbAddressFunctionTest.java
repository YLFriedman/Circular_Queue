package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.database.DbAddress;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The class <b> DbAddressFunctionTest </b> tests all methods in the DbAddress class
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class DbAddressFunctionTest {

    /**
     * This method tests the constructor and getters of DbAddress
     */
    @Test
    public void validate_Constructor() {

        DbAddress testDbAddress = new DbAddress(new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0"));
        Address testAddress = testDbAddress.toDomainObj();
        assertEquals("Service getter Failed - unit", "2A", testAddress.getUnit());
        assertEquals("Service getter Failed - streetNumber", 12, testAddress.getStreetNumber());
        assertEquals("Service getter Failed - street", "Bullvue", testAddress.getStreet());
        assertEquals("Service getter Failed - city", "Ottawa", testAddress.getCity());
        assertEquals("Service getter Failed - city", "Ontario", testAddress.getProvince());
        assertEquals("Service getter Failed - country", "Canada", testAddress.getCountry());
        assertEquals("Service getter Failed - postalCode", "K0A 1A0", testAddress.getPostalCode());

    }

}
