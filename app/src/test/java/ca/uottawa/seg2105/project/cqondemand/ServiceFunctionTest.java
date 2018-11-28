package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServiceFunctionTest {

    @Test
    public void validate_Constructor() {

        // Test the creation of an object and its getters
        try {
            Service testService = new Service("Camera Installation", 250, "category_test");
            assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
            assertEquals("Service getter Failed - rate", 250, testService.getRate());
            assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryKey());
        } catch (InvalidDataException e) {
            fail("Availability construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new Service("Illegal-service+name", 45, "categoryKey");
            fail("Service Constructor Failed - Illegal Name");
        } catch (InvalidDataException ignore) {}
        try {
            new Service("service name", -45, "categoryKey");
            fail("Service Constructor Failed - Illegal rate");
        } catch (InvalidDataException ignore) {}
        try {
            new Service("service name", 45, "");
            fail("Service Constructor Failed - Illegal category key");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_ConstructorKey() {
        
        try {
            Service testService = new Service("111", "Camera Installation", 250, "category_test");
            assertEquals("Service getter Failed - Key", "111", testService.getKey());
            assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
            assertEquals("Service getter Failed - rate", 250, testService.getRate());
            assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryKey());
        } catch (InvalidDataException e) {
            fail("Availability construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new Service("", "service name", -45, "categoryKey");
            fail("Service Constructor w key Failed - Illegal key");
        } catch (InvalidDataException ignore) {}
        try {
            new Service("111", "Illegal-service+name", 45, "categoryKey");
            fail("Service Constructor w key Failed - Illegal Name");
        } catch (InvalidDataException ignore) {}
        try {
            new Service("111", "service name", -45, "categoryKey");
            fail("Service Constructor w key Failed - Illegal rate");
        } catch (InvalidDataException ignore) {}
        try {
            new Service("111", "service name", 45, "");
            fail("Service Constructor w key Failed - Illegal category key");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_Equals() {
        Service testService1a = new Service("Camera Installation", 250, "category_test");
        Service testService1b = new Service("Camera Installation", 250, "category_test");
        Service testService1c = new Service("Camera Installation New", 250, "category_test");
        Service testService1d = new Service("Camera Installation", 120, "category_test");
        Service testService1e = new Service("Camera Installation", 250, "category_test_new");

        Service testService2a = new Service("111", "Camera Installation", 250, "category_test");
        Service testService2b = new Service("111", "Camera Installation", 250, "category_test");
        Service testService2c = new Service("131", "Camera Installation", 250, "category_test");
        Service testService2d = new Service("111", "Camera Installation New", 250, "category_test");
        Service testService2e = new Service("111", "Camera Installation", 120, "category_test");
        Service testService2f = new Service("111", "Camera Installation", 250, "category_test_new");

        assertFalse("Service equals failed - Different Objects", testService1a.equals("Hello"));
        
        assertTrue("Service equals failed - Same Object", testService1a.equals(testService1a));
        assertTrue("Service equals failed - Same Values", testService1a.equals(testService1b));
        assertFalse("Service equals failed - Different Name", testService1a.equals(testService1c));
        assertFalse("Service equals failed - Different Rate", testService1a.equals(testService1d));
        assertFalse("Service equals failed - Different Category", testService1a.equals(testService1e));

        assertTrue("Service equals failed w Key - Same Object", testService2a.equals(testService2a));
        assertTrue("Service equals failed w Key - Same Values", testService2a.equals(testService2b));
        assertFalse("Service equals failed w Key - Different Key", testService2a.equals(testService2c));
        assertTrue("Service equals failed w Key - Different Name", testService2a.equals(testService2d));
        assertTrue("Service equals failed w Key - Different Rate", testService2a.equals(testService2e));
        assertTrue("Service equals failed w Key - Different Category", testService2a.equals(testService2f));

        assertTrue("Service Equals failed - Same Value, key vs no key", testService1a.equals(testService2a));
    }

}