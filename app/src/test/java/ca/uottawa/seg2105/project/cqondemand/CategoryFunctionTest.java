package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

import static org.junit.Assert.assertEquals;

public class CategoryFunctionTest {

    @Test
    public void validate_Constructor(){
        Category testCategory = new Category("Test Name");
        assertEquals("getter Failed - name", "Test Name", testCategory.getName());
    }

    @Test
    public void service_Name_Validation(){
        assertEquals("name validation failed - apostrophe returns invalid", true, Category.nameIsValid("'"));
        assertEquals("name validation failed - space returns invalid", true, Category.nameIsValid(" ") );
        assertEquals("name validation failed - alphabet return invalid", true,
                Category.nameIsValid("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals("name validation failed - numbers return valid", false, Category.nameIsValid("1234567890"));
        assertEquals("name validation failed - tab return valid", false, Category.nameIsValid("\t"));
        assertEquals("name validation failed - symbols return valid", false,
                Category.nameIsValid("<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\"));
    }
}


