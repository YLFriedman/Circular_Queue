package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServiceProviderFunctionTest {

    public static Address testAddressA = new Address( "2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
    public static Address testAddressB = new Address( "2b", 15, "King Edward", "Ottawa", "Ontario", "Canada", "K0A1A0");

    @Test
    public void ServiceProvider_constructor_test() {

        // Test the creation of an object and its getters
        try {
            ServiceProvider testServiceProvider = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");
            assertEquals("ServiceProvider getter Failed - firstName", "firstName", testServiceProvider.getFirstName());
            assertEquals("ServiceProvider getter Failed - lastName", "lastName", testServiceProvider.getLastName());
            assertEquals("ServiceProvider getter Failed - username", "username", testServiceProvider.getUsername());
            assertEquals("ServiceProvider getter Failed - email", "{@TEST}", testServiceProvider.getEmail());
            assertEquals("ServiceProvider getter Failed - type", ServiceProvider.Type.SERVICE_PROVIDER, testServiceProvider.getType());
            assertEquals("ServiceProvider getter Failed - password", "passtest", testServiceProvider.getPassword());
            assertEquals("ServiceProvider getter Failed - companyName", "companyName", testServiceProvider.getCompanyName());
            assertTrue("ServiceProvider getter Failed - licensed", testServiceProvider.isLicensed());
            assertEquals("ServiceProvider getter Failed - {@TEST}", "{@TEST}", testServiceProvider.getPhoneNumber());
            assertEquals("ServiceProvider getter Failed - address", testAddressA, testServiceProvider.getAddress());
            assertEquals("ServiceProvider getter Failed - description", "description", testServiceProvider.getDescription());

        } catch (InvalidDataException e) {
            fail("ServiceProvider construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName!", true, "{@TEST}", testAddressA, "description");
            fail("ServiceProvider Constructor Failed - Illegal companyName");
        } catch (InvalidDataException ignore) {}
        try {
            new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", null, "description");
            fail("ServiceProvider Constructor Failed - Illegal address");
        } catch (InvalidDataException ignore) {}

    }


    @Test
    public void ServiceProvider_constructorKey_test() {

        // Test the creation of an object and its getters
        try {
            ServiceProvider testServiceProvider = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");
            assertEquals("ServiceProvider getter Failed - key", "key", testServiceProvider.getKey());
            assertEquals("ServiceProvider getter Failed - firstName", "firstName", testServiceProvider.getFirstName());
            assertEquals("ServiceProvider getter Failed - lastName", "lastName", testServiceProvider.getLastName());
            assertEquals("ServiceProvider getter Failed - username", "username", testServiceProvider.getUsername());
            assertEquals("ServiceProvider getter Failed - email", "{@TEST}", testServiceProvider.getEmail());
            assertEquals("ServiceProvider getter Failed - type", ServiceProvider.Type.SERVICE_PROVIDER, testServiceProvider.getType());
            assertEquals("ServiceProvider getter Failed - password", "passtest", testServiceProvider.getPassword());
            assertEquals("ServiceProvider getter Failed - companyName", "companyName", testServiceProvider.getCompanyName());
            assertTrue("ServiceProvider getter Failed - licensed", testServiceProvider.isLicensed());
            assertEquals("ServiceProvider getter Failed - {@TEST}", "{@TEST}", testServiceProvider.getPhoneNumber());
            assertEquals("ServiceProvider getter Failed - address", testAddressA, testServiceProvider.getAddress());
            assertEquals("ServiceProvider getter Failed - description", "description", testServiceProvider.getDescription());

        } catch (InvalidDataException e) {
            fail("ServiceProvider construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName!", true, "{@TEST}", testAddressA, "description");
            fail("ServiceProvider Constructor Failed - Illegal companyName");
        } catch (InvalidDataException ignore) {}
        try {
            new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", null, "description");
            fail("ServiceProvider Constructor Failed - Illegal address");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_Equals() {
        ServiceProvider testServiceProvider1a = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider1b = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider1c = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyNameDif", true, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider1d = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", false, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider1f = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressB, "description");
        ServiceProvider testServiceProvider1g = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "descriptionDif");

        ServiceProvider testServiceProvider2a = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider2b = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider2c = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyNameDif", true, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider2d = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", false, "{@TEST}", testAddressA, "description");
        ServiceProvider testServiceProvider2f = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressB, "description");
        ServiceProvider testServiceProvider2g = new ServiceProvider("key", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "descriptionDif");
        ServiceProvider testServiceProvider2h = new ServiceProvider("keyDif", "firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "descriptionDif");

        assertFalse("ServiceProvider equals failed - Different Objects", testServiceProvider1a.equals("Hello"));

        assertTrue("ServiceProvider equals failed - Same Object", testServiceProvider1a.equals(testServiceProvider1a));
        assertTrue("ServiceProvider equals failed - Same Values", testServiceProvider1a.equals(testServiceProvider1b));
        assertFalse("ServiceProvider equals failed - Different companyName", testServiceProvider1a.equals(testServiceProvider1c));
        assertFalse("ServiceProvider equals failed - Different licensed", testServiceProvider1a.equals(testServiceProvider1d));
        assertFalse("ServiceProvider equals failed - Different address", testServiceProvider1a.equals(testServiceProvider1f));
        assertFalse("ServiceProvider equals failed - Different description", testServiceProvider1a.equals(testServiceProvider1g));

        assertTrue("ServiceProvider equals failed w Key - Same Object", testServiceProvider2a.equals(testServiceProvider2a));
        assertTrue("ServiceProvider equals failed w Key - Same Values", testServiceProvider2a.equals(testServiceProvider2b));
        assertTrue("ServiceProvider equals failed w Key - Different firstName", testServiceProvider2a.equals(testServiceProvider2c));
        assertTrue("ServiceProvider equals failed w Key - Different lastName", testServiceProvider2a.equals(testServiceProvider2d));
        assertTrue("ServiceProvider equals failed w Key - Different type", testServiceProvider2a.equals(testServiceProvider2f));
        assertTrue("ServiceProvider equals failed w Key - Different password", testServiceProvider2a.equals(testServiceProvider2g));
        assertFalse("ServiceProvider equals failed w Key - Different Key", testServiceProvider2a.equals(testServiceProvider2h));

        assertTrue("ServiceProvider Equals failed - Same Value, key vs no key", testServiceProvider1a.equals(testServiceProvider2a));

    }



}
