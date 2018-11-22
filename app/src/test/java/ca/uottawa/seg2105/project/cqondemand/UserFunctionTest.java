package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.Authentication;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.*;

public class UserFunctionTest {

    @Test
    public void user_constructor_test() {

        // Test the creation of an object and its getters
        try {
            User testUser = new User("firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            assertEquals("User getter Failed - firstName", "firstName", testUser.getFirstName());
            assertEquals("User getter Failed - lastName", "lastName", testUser.getLastName());
            assertEquals("User getter Failed - username", "username", testUser.getUsername());
            assertEquals("User getter Failed - email", "{@TEST}", testUser.getEmail());
            assertEquals("User getter Failed - type", User.Type.HOMEOWNER, testUser.getType());
            assertEquals("User getter Failed - password", "passtest", testUser.getPassword());
        } catch (InvalidDataException e) {
            fail("User construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new User("firstName1", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal firstName");
        } catch (InvalidDataException ignore) {}
        try {
            new User("firstName", "lastName2", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal lastName");
        } catch (InvalidDataException ignore) {}
        try {
            new User("firstName", "lastName", "username^", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal username");
        } catch (InvalidDataException ignore) {}
        try {
            new User("firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "password");
            fail("User Constructor Failed - Illegal password");
        } catch (InvalidDataException ignore) {}
        try {
            new User("firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "username");
            fail("User Constructor Failed - Illegal password");
        } catch (InvalidDataException ignore) {}
        try {
            new User("firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "");
            fail("User Constructor Failed - Illegal password");
        } catch (InvalidDataException ignore) {}

    }


    @Test
    public void user_constructorKey_test() {

        // Test the creation of an object and its getters
        try {
            User testUser = new User("key", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            assertEquals("User getter Failed - key", "key", testUser.getKey());
            assertEquals("User getter Failed - firstName", "firstName", testUser.getFirstName());
            assertEquals("User getter Failed - lastName", "lastName", testUser.getLastName());
            assertEquals("User getter Failed - username", "username", testUser.getUsername());
            assertEquals("User getter Failed - email", "{@TEST}", testUser.getEmail());
            assertEquals("User getter Failed - type", User.Type.HOMEOWNER, testUser.getType());
            assertEquals("User getter Failed - password", "passtest", testUser.getPassword());
        } catch (InvalidDataException e) {
            fail("User construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new User("", "firstName1", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal key");
        } catch (InvalidDataException ignore) {}
        try {
            new User("key", "firstName1", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal firstName");
        } catch (InvalidDataException ignore) {}
        try {
            new User("key", "firstName", "lastName2", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal lastName");
        } catch (InvalidDataException ignore) {}
        try {
            new User("key", "firstName", "lastName", "username^", "{@TEST}", User.Type.HOMEOWNER, "passtest");
            fail("User Constructor Failed - Illegal username");
        } catch (InvalidDataException ignore) {}
        try {
            new User("key", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "password");
            fail("User Constructor Failed - Illegal password");
        } catch (InvalidDataException ignore) {}
        try {
            new User("key", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "username");
            fail("User Constructor Failed - Illegal password");
        } catch (InvalidDataException ignore) {}
        try {
            new User("key", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "");
            fail("User Constructor Failed - Illegal password");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_Equals() {
        User testUser1a = new User("firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser1b = new User("firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser1c = new User("firstNameDif", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser1d = new User("firstName", "lastNameDif", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser1e = new User("firstName", "lastName", "usernameDif", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser1f = new User("firstName", "lastName", "usernameDif", "{@TEST}", User.Type.ADMIN, "passtest");
        User testUser1g = new User("firstName", "lastName", "usernameDif", "{@TEST}", User.Type.HOMEOWNER, "passtestDif");

        User testUser2a = new User("key", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser2b = new User("key", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser2c = new User("key", "firstNameDif", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser2d = new User("key", "firstName", "lastNameDif", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser2e = new User("key", "firstName", "lastName", "usernameDif", "{@TEST}", User.Type.HOMEOWNER, "passtest");
        User testUser2f = new User("key", "firstName", "lastName", "usernameDif", "{@TEST}", User.Type.ADMIN, "passtest");
        User testUser2g = new User("key", "firstName", "lastName", "usernameDif", "{@TEST}", User.Type.HOMEOWNER, "passtestDif");
        User testUser2h = new User("keyDif", "firstName", "lastName", "username", "{@TEST}", User.Type.HOMEOWNER, "passtest");

        assertFalse("User equals failed - Different Objects", testUser1a.equals("Hello"));

        assertTrue("User equals failed - Same Object", testUser1a.equals(testUser1a));
        assertTrue("User equals failed - Same Values", testUser1a.equals(testUser1b));
        assertFalse("User equals failed - Different firstName", testUser1a.equals(testUser1c));
        assertFalse("User equals failed - Different lastName", testUser1a.equals(testUser1d));
        assertFalse("User equals failed - Different username", testUser1a.equals(testUser1e));
        assertFalse("User equals failed - Different type", testUser1a.equals(testUser1f));
        assertFalse("User equals failed - Different password", testUser1a.equals(testUser1g));

        assertTrue("User equals failed w Key - Same Object", testUser2a.equals(testUser2a));
        assertTrue("User equals failed w Key - Same Values", testUser2a.equals(testUser2b));
        assertTrue("User equals failed w Key - Different firstName", testUser2a.equals(testUser2c));
        assertTrue("User equals failed w Key - Different lastName", testUser2a.equals(testUser2d));
        assertTrue("User equals failed w Key - Different username", testUser2a.equals(testUser2e));
        assertTrue("User equals failed w Key - Different type", testUser2a.equals(testUser2f));
        assertTrue("User equals failed w Key - Different password", testUser2a.equals(testUser2g));
        assertFalse("User equals failed w Key - Different Key", testUser2a.equals(testUser2h));

        assertTrue("User Equals failed - Same Value, key vs no key", testUser1a.equals(testUser2a));

    }

}
