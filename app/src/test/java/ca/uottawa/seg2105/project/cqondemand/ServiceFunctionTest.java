package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServiceFunctionTest {

    @Test
    public void validate_Constructor() {
        Service testService = new Service("Camera Installation", 250, "category_test");
        assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
        assertEquals("Service getter Failed - rate", 250, testService.getRate());
        assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryID());
    }

    @Test
    public void validate_ConstructorKey() {
        Service testService = new Service("111", "Camera Installation", 250, "category_test");
        assertEquals("Service getter Failed - Key", "111", testService.getKey());
        assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
        assertEquals("Service getter Failed - rate", 250, testService.getRate());
        assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryID());
    }

    @Test
    public void validate_Equals() {
        Service testService1 = new Service("Camera Installation", 250, "category_test");
        Service testService2 = new Service("Camera Installation", 250, "category_test");
        Service testService3 = new Service("111", "Camera Installation", 250, "category_test");
        Service testService4 = new Service("111", "Camera Installation", 1000, "category_test");
        Service testService5 = new Service("211", "Camera Installation", 250, "category_test");
        Service testService6 = new Service("211", "Installation", 450, "category_test");
        Service testService7 = new Service( "Camera Installation", 250, "category_test");
        assertTrue("Equals Service validation failed - Same Object", testService1.equals(testService1));
        assertTrue("Equals Service validation failed - Same Object Values", testService1.equals(testService2));
        assertTrue("Equals Service validation failed - Same Key", testService3.equals(testService4));
        assertTrue("Equals Service validation failed - Different Key", testService3.equals(testService5));
        assertFalse("Equals Service validation failed - Different Key Different Values", testService3.equals(testService6));
        assertFalse("Equals Service validation failed - Different Values (No Key)", testService3.equals(testService7));
        assertFalse("Equals Service validation failed - Different Objects", testService3.equals("Hello"));

    }

    /*
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Service)) { return false; }
        if (this == otherObj) { return true; }
        Service other = (Service) otherObj;
        if (null != key && null != other.key) { return key.equals(other.key); }
        return null != getUniqueName() && getUniqueName().equals(other.getUniqueName());
    }
     */
}