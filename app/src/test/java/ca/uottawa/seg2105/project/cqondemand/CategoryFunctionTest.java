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
}
