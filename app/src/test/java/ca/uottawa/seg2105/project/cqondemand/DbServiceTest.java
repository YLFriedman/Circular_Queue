package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.database.DbService;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DbServiceTest {

    @Test
    public void validate_ConstructorKey() {
        DbService testDbService = new DbService(new Service("111","Camera Installation", 250, "category_test"));
        Service testService = testDbService.toDomainObj();
        assertEquals("Service getter Failed - Key", "111", testService.getKey());
        assertEquals("Service getter Failed - name", "Camera Installation", testService.getName());
        assertEquals("Service getter Failed - rate", 250, testService.getRate());
        assertEquals("Service getter Failed - categoryID", "category_test", testService.getCategoryKey());
    }
}
