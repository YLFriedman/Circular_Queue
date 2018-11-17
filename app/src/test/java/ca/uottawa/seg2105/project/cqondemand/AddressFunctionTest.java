package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddressFunctionTest {

    @Test
    public void validate_Constructor() {
        Address testAddress = new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        assertEquals("Service getter Failed - unit", "2A", testAddress.getUnit());
        assertEquals("Service getter Failed - streetNumber", 12, testAddress.getStreetNumber());
        assertEquals("Service getter Failed - street", "Bullvue", testAddress.getStreet());
        assertEquals("Service getter Failed - city", "Ottawa", testAddress.getCity());
        assertEquals("Service getter Failed - city", "Ontario", testAddress.getProvince());
        assertEquals("Service getter Failed - country", "Canada", testAddress.getCountry());
        assertEquals("Service getter Failed - postalCode", "K0A 1A0", testAddress.getPostalCode());
    }

    @Test
    public void validate_equals() {
        Address testAddress = new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address testAddress2 = new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        assertTrue("Address Equals Failed - same object", testAddress.equals(testAddress));
        assertTrue("Address Equals Failed - same values", testAddress.equals(testAddress2));

    }

}
