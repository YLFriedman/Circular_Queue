package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import static org.junit.Assert.assertEquals;

public class ServiceFunctionTest {

    @Test
    public void validate_Constructor() {
        Service testService = new Service("Camera Installation", 250, "category_test");
        assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
        assertEquals("Service getter Failed - rate", 250, testService.getRate());
        assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryID());
    }

}