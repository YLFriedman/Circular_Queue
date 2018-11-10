package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

import static org.junit.Assert.assertEquals;

public class ServiceFunctionTest {

    @Test
    public void validate_Constructor() {
        Service testService = new Service("Camera Installation", 250, "category_test");
        assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
        assertEquals("Service getter Failed - rate", 250, testService.getRate());
        assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryID());
    }

    @Test
    public void service_Name_Validation() {
        assertEquals("Service name validation failed - apostrophe returns invalid", true, Service.nameIsValid("Chef's"));
        assertEquals("Service name validation failed - space returns invalid", true, Service.nameIsValid("My Space Case") );
        assertEquals("Service name validation failed - dash returns invalid", true, Service.nameIsValid("My-Dash-Case") );
        assertEquals("Service name validation failed - alphabet return invalid", true,
                Service.nameIsValid("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals("Service name validation failed - numbers return valid", false, Service.nameIsValid("1234567890"));
        assertEquals("Service name validation failed - tab return valid", false, Service.nameIsValid("\t"));
        assertEquals("Service name validation failed - symbols return valid", false,
                Service.nameIsValid("<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\"));
    }

}
