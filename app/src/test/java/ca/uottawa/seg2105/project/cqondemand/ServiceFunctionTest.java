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

  @Test
    public void service_Name_Validation(){
      assertEquals("name validation failed - apostrophe returns invalid", true, Service.nameIsValid("'"));
      assertEquals("name validation failed - space returns invalid", true, Service.nameIsValid(" ") );
      assertEquals("name validation failed - alphabet return invalid", true,
              Service.nameIsValid("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
      assertEquals("name validation failed - numbers return valid", false, Service.nameIsValid("1234567890"));
      assertEquals("name validation failed - tab return valid", false, Service.nameIsValid("\t"));
      assertEquals("name validation failed - symbols return valid", false,
              Service.nameIsValid("<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\"));
  }
}
