package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CategoryFunctionTest {

    @Test
    public void validate_constructor() {

        // Test the creation of an object and its getters
        try {
            Category testCategory = new Category("Test Name");
            assertEquals("Category getter Failed - name", "Test Name", testCategory.getName());
        } catch (InvalidDataException e) {
            fail("Category construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new Category("Illegal-category+name");
            fail("Category Constructor Failed - Illegal Name");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_key_constructor() {

        // Test the creation of an object and its getters
        try {
            Category testCategory = new Category("key", "Test Name");
            assertEquals("Category getter Failed - key", "key", testCategory.getKey());
            assertEquals("Category getter Failed - name", "Test Name", testCategory.getName());
        } catch (InvalidDataException e) {
            fail("Category construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new Category("1234", "Illegal-category+name");
            fail("Category Constructor Failed - Illegal Name");
        } catch (InvalidDataException ignore) {}
        try {
            new Category("", "catName");
            fail("Category Constructor Failed - Illegal Key");
        } catch (InvalidDataException ignore) {}

    }

    @Test
    public void validate_Equals() {
        
        Category testCategory1 = new Category("Name");
        Category testCategory2 = new Category("Name");
        Category testCategory3 = new Category("NameA");
        Category testCategory4 = new Category("000", "NameB");
        Category testCategory5 = new Category("000", "NameB");
        Category testCategory6 = new Category("111", "NameB");
        Category testCategory7 = new Category("111", "Name");

        assertFalse("Category Equals failed - Different Object Type", testCategory1.equals("Hello"));
        assertTrue("Category Equals failed - Same Object", testCategory1.equals(testCategory1));
        assertTrue("Category Equals failed - Same Values", testCategory1.equals(testCategory2));
        assertFalse("Category Equals failed - Different Name", testCategory1.equals(testCategory3));
        assertTrue("Category Equals failed - Same Object w Key", testCategory4.equals(testCategory4));
        assertTrue("Category Equals failed - Same Values w Key", testCategory4.equals(testCategory5));
        assertFalse("Category Equals failed - Different Key", testCategory4.equals(testCategory6));
        assertTrue("Category Equals failed - Same Value, key vs no key", testCategory1.equals(testCategory7));

    }

}


