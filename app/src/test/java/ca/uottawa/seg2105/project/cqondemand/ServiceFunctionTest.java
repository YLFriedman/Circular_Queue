package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

import static org.junit.Assert.assertEquals;

public class ServiceFunctionTest {

  @Test
    public void validate_Constructor(){
      Service testService = new Service("Home Security", 250, new ArrayList<User>(), "Camera Installation");
      assertEquals("getter Failed - category", "Camera Installation", testService.getName());
      assertEquals("getter Failed - rate", 250.0, testService.getRate(), 0.00001);

  }
}
