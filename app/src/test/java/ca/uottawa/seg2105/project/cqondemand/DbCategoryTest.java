package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.database.DbCategory;
import ca.uottawa.seg2105.project.cqondemand.domain.Category;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DbCategoryTest {
    @Test
    public void validate_constructor() {
        DbCategory testDbCategory = new DbCategory(new Category("Test Name"));
        Category testCategory = testDbCategory.toDomainObj();
        assertEquals("getter Failed - name", "Test Name", testCategory.getName());
    }

    @Test
    public void validate_key_constructor() {
        DbCategory testDbCategory = new DbCategory(new Category("key","Test Name"));
        Category testCategory = testDbCategory.toDomainObj();
        assertEquals("getter Failed - key", "key", testCategory.getKey());
        assertEquals("getter Failed - name", "Test Name", testCategory.getName());
    }
}
