package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void validate_Equals() {
        Category testCategory1 = new Category("Name");
        Category testCategory2 = new Category("Name");
        Category testCategory3 = new Category("NameA");
        Category testCategory4 = new Category("000", "NameB");
        Category testCategory5 = new Category("111", "Name");
        Category testCategory6 = new Category("111", "Name");
        Category testCategory7 = new Category("111", "NameC");

        assertTrue("Equals validation failed - Same Object", testCategory1.equals(testCategory1));
        assertTrue("Equals validation failed - Same Object", testCategory1.equals(testCategory2));
        assertTrue("Equals validation failed - Same Object w Key", testCategory1.equals(testCategory5));
        assertTrue("Equals validation failed - Same Object w Keys", testCategory5.equals(testCategory6));
        assertTrue("Equals validation failed - Same Object w Keys", testCategory5.equals(testCategory7));
        assertFalse("Equals validation failed - Different Object", testCategory1.equals(testCategory3));
        assertFalse("Equals validation failed - Different Object", testCategory1.equals(testCategory4));
        assertFalse("Equals validation failed - Different Object w Key", testCategory5.equals(testCategory3));



    }

}


