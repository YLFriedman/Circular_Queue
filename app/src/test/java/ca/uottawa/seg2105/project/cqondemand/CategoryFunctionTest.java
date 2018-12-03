package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The class <b> CategoryFunctionTest </b> is used to test all methods of the Category class
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class CategoryFunctionTest {

    /**
     * This method tests Category key-less constructor and getters
     */
    @Test
    public void validate_constructor() {

        try {
            Category testCategory = new Category("Test Name");
            assertEquals("Category getter Failed - name", "Test Name", testCategory.getName());
        } catch (InvalidDataException e) {
            fail("Category construction failed. Error: " + e.getMessage());
        }
    }

    /**
     * This method tests that an exception will be thrown when an invalid name is passed to the constructor
     */
    @Test(expected = InvalidDataException.class)
    public void invalidNameTest() {
        new Category("Illegal-category+name");
        fail("Category Constructor Failed - Illegal Name");
    }


    /**
     * This method tests that the category key constructor and getters work
     */
    @Test
    public void validate_key_constructor() {

        try {
            Category testCategory = new Category("key", "Test Name");
            assertEquals("Category getter Failed - key", "key", testCategory.getKey());
            assertEquals("Category getter Failed - name", "Test Name", testCategory.getName());
        } catch (InvalidDataException e) {
            fail("Category construction failed. Error: " + e.getMessage());
        }
    }

    /**
     * This method tests that an exception will be thrown when an empty key is passed to the constructor
     */
    @Test(expected = InvalidDataException.class)
    public void emptyKeyTest() {
        new Category("", "catName");
        fail("Category Constructor Failed - Illegal Key");

    }

    /**
     * This method tests that the category equals function works as expected
     */
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


