package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    public void validate_Equals() {
        Address testAddress1 = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address testAddress2 = new Address( "2b", 22, "OStreet", "Ottawa", "Ontario", "USA", "K0A1A0");
        Address testAddress3 = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");

        assertTrue("Equals validation failed - Same Object", testAddress1.equals(testAddress1));
        assertTrue("Equals validation failed - Same Object", testAddress1.equals(testAddress3));
        assertFalse("Equals validation failed - Different Objects Values", testAddress1.equals(testAddress2));
        assertFalse("Equals validation failed - Different Objects", testAddress1.equals("Hello"));
    }

}
