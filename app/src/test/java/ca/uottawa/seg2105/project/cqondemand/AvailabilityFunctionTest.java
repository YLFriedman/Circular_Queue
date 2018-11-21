package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AvailabilityFunctionTest {

    @Test
    public void validate_Constructor() {

        // Test the creation of an object and its getters
        try {
            Availability testAvailability = new Availability(Availability.parseDay("SUNDAY"), 12, 15);
            assertEquals("Availability getter Failed - day", Availability.Day.SUNDAY, testAvailability.getDay());
            assertEquals("Availability getter Failed - startTime", 12, testAvailability.getStartTime());
            assertEquals("Availability getter Failed - endTime", 15, testAvailability.getEndTime());
        } catch (InvalidDataException e) {
            fail("Availability construction failed. Error: " + e.getMessage());
        }

        // Make sure InvalidDataException is thrown for invalid data
        try {
            new Availability(Availability.Day.WEDNESDAY, 24, 18);
            fail("Availability Constructor Failed - Illegal Start Time");
        } catch (InvalidDataException ignore) {}
        try {
            new Availability(Availability.Day.WEDNESDAY, 0, 0);
            fail("Availability Constructor Failed - Illegal End Time");
        } catch (InvalidDataException ignore) {}
        try {
            new Availability(Availability.Day.WEDNESDAY, 8, 5);
            fail("Availability Constructor Failed - End Time before Start Time");
        } catch (InvalidDataException ignore) {}
    }

    @Test
    public void validate_ParseDayString() {
        assertEquals("Availability parseDay failed - String sunDay", Availability.Day.SUNDAY, Availability.parseDay("sunDay"));
        assertEquals("Availability parseDay failed - String MONDay", Availability.Day.MONDAY, Availability.parseDay("MONDay"));
        assertEquals("Availability parseDay failed - String TuesDay", Availability.Day.TUESDAY, Availability.parseDay("TuesDay"));
        assertEquals("Availability parseDay failed - String WEDNESDAY", Availability.Day.WEDNESDAY, Availability.parseDay("WEDNESDAY"));
        assertEquals("Availability parseDay failed - String thursday", Availability.Day.THURSDAY, Availability.parseDay("thursday"));
        assertEquals("Availability parseDay failed - String Friday", Availability.Day.FRIDAY, Availability.parseDay("Friday"));
        assertEquals("Availability parseDay failed - String saTURday", Availability.Day.SATURDAY, Availability.parseDay("saTURday"));
    }

    @Test
    public void validate_ParseDayInt() {
        assertEquals("Availability parseDay failed - int 0", Availability.Day.SUNDAY, Availability.parseDay(0));
        assertEquals("Availability parseDay failed - int 1", Availability.Day.MONDAY, Availability.parseDay(1));
        assertEquals("Availability parseDay failed - int 2", Availability.Day.TUESDAY, Availability.parseDay(2));
        assertEquals("Availability parseDay failed - int 3", Availability.Day.WEDNESDAY, Availability.parseDay(3));
        assertEquals("Availability parseDay failed - int 4", Availability.Day.THURSDAY, Availability.parseDay(4));
        assertEquals("Availability parseDay failed - int 5", Availability.Day.FRIDAY, Availability.parseDay(5));
        assertEquals("Availability parseDay failed - int 6", Availability.Day.SATURDAY, Availability.parseDay(6));
    }

    @Test
    public void validate_Equals() {
        Availability testAvailability = new Availability(Availability.Day.SUNDAY, 12, 15);
        Availability differentObjectSameValues = new Availability(Availability.Day.SUNDAY, 12, 15);
        Availability differentDay = new Availability(Availability.Day.MONDAY, 12, 15);
        Availability differentStartTime = new Availability(Availability.Day.SUNDAY, 14, 15);
        Availability differentEndTime = new Availability(Availability.Day.SUNDAY, 12, 18);
        assertTrue("Availability Equals failed - Same Object", testAvailability.equals(testAvailability));
        assertTrue("Availability Equals failed - Different Object, Same values", testAvailability.equals(differentObjectSameValues));
        assertFalse("Availability Equals failed - Different Object Type", testAvailability.equals("Hello"));
        assertFalse("Availability Equals failed - Different Day", testAvailability.equals(differentDay));
        assertFalse("Availability Equals failed - Different Start Time", testAvailability.equals(differentStartTime));
        assertFalse("Availability Equals failed - Different End Time", testAvailability.equals(differentEndTime));
    }

}
