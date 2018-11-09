package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

import static org.junit.Assert.assertEquals;

public class ServiceFunctionTest {

  @Test
    public void validate_Constructor(){
      Service testService = new Service("Camera Installation", 250, "category_test");
      assertEquals("getter Failed - category", "Camera Installation", testService.getName());
      assertEquals("getter Failed - rate", 250, testService.getRate());

  }
}
