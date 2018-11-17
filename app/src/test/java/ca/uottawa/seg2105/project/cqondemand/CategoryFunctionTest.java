package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import static org.junit.Assert.assertEquals;

public class CategoryFunctionTest {

    @Test
    public void validate_constructor() {
        Category testCategory = new Category("Test Name");
        assertEquals("getter Failed - name", "Test Name", testCategory.getName());
    }

    @Test
    public void validate_key_constructor() {
        Category testCategory = new Category("key", "Test Name");
        assertEquals("getter Failed - key", "key", testCategory.getKey());
        assertEquals("getter Failed - name", "Test Name", testCategory.getName());
    }

}


