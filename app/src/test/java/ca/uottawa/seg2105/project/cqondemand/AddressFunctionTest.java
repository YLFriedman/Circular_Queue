package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddressFunctionTest {

    @Test
    public void validate_Constructor() {

        // Test the creation of an object and its getters
        try {
            Address testAddress = new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
            assertEquals("Address getter Failed - unit", "2A", testAddress.getUnit());
            assertEquals("Address getter Failed - streetNumber", 12, testAddress.getStreetNumber());
            assertEquals("Address getter Failed - street", "Bullvue", testAddress.getStreet());
            assertEquals("Address getter Failed - city", "Ottawa", testAddress.getCity());
            assertEquals("Address getter Failed - city", "Ontario", testAddress.getProvince());
            assertEquals("Address getter Failed - country", "Canada", testAddress.getCountry());
            assertEquals("Address getter Failed - postalCode", "K0A 1A0", testAddress.getPostalCode());
        } catch (InvalidDataException e) {
            fail("Address construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new Address("2&a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
            fail("Address Constructor Failed - Illegal unit");
        } catch (InvalidDataException ignore) {}
        try {
            new Address("2a", -5, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
            fail("Address Constructor Failed - Illegal Street Number");
        } catch (InvalidDataException ignore) {}
        try {
            new Address("2a", 5, "Bull+vu=", "Ottawa", "Ontario", "Canada", "K0A1A0");
            fail("Address Constructor Failed - Illegal Street Name");
        } catch (InvalidDataException ignore) {}
        try {
            new Address("2a", 5, "Bullvue", "Ott-awa", "Ontario", "Canada", "K0A1A0");
            fail("Address Constructor Failed - Illegal City");
        } catch (InvalidDataException ignore) {}
        try {
            new Address("2a", 5, "Bullvue", "Ottawa", "Ontar+o2", "Canada", "K0A1A0");
            fail("Address Constructor Failed - Illegal Province");
        } catch (InvalidDataException ignore) {}
        try {
            new Address("2a", 5, "Bullvue", "Ottawa", "Ontario", "Canada2+", "K0A1A0");
            fail("Address Constructor Failed - Illegal Country");
        } catch (InvalidDataException ignore) {}
        try {
            new Address("2a", 5, "Bullvue", "Ottawa", "Ontario", "Canada", "5R3 TK2");
            fail("Address Constructor Failed - Illegal Postal Code");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_Equals() {
        Address testAddress = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address sameValues = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address differentUnit = new Address( "4b", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address differentStreetNumber = new Address( "2a", 15, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address differentStreetName = new Address( "2a", 12, "King Edward", "Ottawa", "Ontario", "Canada", "K0A1A0");
        Address differentCity = new Address( "2a", 12, "Bullvue", "Toronto", "Ontario", "Canada", "K0A1A0");
        Address differentProvince = new Address( "2a", 12, "Bullvue", "Ottawa", "Quebec", "Canada", "K0A1A0");
        Address differentCountry = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "USA", "K0A1A0");
        Address differentPostalCode = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K1A2B0");
        assertFalse("Address Equals failed - Different Object Type", testAddress.equals("Hello"));
        assertTrue("Address Equals failed - Same Object", testAddress.equals(testAddress));
        assertTrue("Address Equals failed - Different Object, Same Values", testAddress.equals(sameValues));
        assertFalse("Address Equals failed - Different Object, differentUnit", testAddress.equals(differentUnit));
        assertFalse("Address Equals failed - Different Object, differentStreetNumber", testAddress.equals(differentStreetNumber));
        assertFalse("Address Equals failed - Different Object, differentStreetName", testAddress.equals(differentStreetName));
        assertFalse("Address Equals failed - Different Object, differentCity", testAddress.equals(differentCity));
        assertFalse("Address Equals failed - Different Object, differentProvince", testAddress.equals(differentProvince));
        assertFalse("Address Equals failed - Different Object, differentCountry", testAddress.equals(differentCountry));
        assertFalse("Address Equals failed - Different Object, differentPostalCode", testAddress.equals(differentPostalCode));
    }

}
