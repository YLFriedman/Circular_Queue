package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The class <b> AvailabilityFunctionTest</b> is used to test all functions in the Availability class
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class AvailabilityFunctionTest {

    /**
     * This method tests the contructors and getter of Availability
     */
    @Test
    public void validate_Constructor() {

        // Test the creation of an object and its getters
        try {
            Availability testAvailability = new Availability(Availability.Day.parse("SUNDAY"), 12, 15);
            assertEquals("Availability getter Failed - day", Availability.Day.SUNDAY, testAvailability.getDay());
            assertEquals("Availability getter Failed - startTime", 12, testAvailability.getStartTime());
            assertEquals("Availability getter Failed - endTime", 15, testAvailability.getEndTime());
        } catch (InvalidDataException e) {
            fail("Availability construction failed. Error: " + e.getMessage());
        }
    }

    /**
     * This method is used to make sure an exception is thrown when an invalid start time is provided
     */
    @Test(expected = InvalidDataException.class)
    public void invalidStartTimeTest() {
        new Availability(Availability.Day.WEDNESDAY, 24, 18);
        fail("Availability Constructor Failed - Illegal Start Time");
    }

    /**
     * This method is used to make sure an exception is thrown when an invalid end time is provided
     */
    @Test(expected = InvalidDataException.class)
    public void invalidEndTimeTest() {
        new Availability(Availability.Day.WEDNESDAY, 0, 0);
        fail("Availability Constructor Failed - Illegal End Time");
    }

    /**
     * This method is used to make sure an exception is thrown when the end time is before the start time
     */
    @Test(expected = InvalidDataException.class)
    public void endTimeBeforeStartTimeTest() {
    new Availability(Availability.Day.WEDNESDAY, 8, 5);
    fail("Availability Constructor Failed - End Time before Start Time");

    }

    /**
     * This method makes sure that the parseDay(String) method works as expected
     */
    @Test
    public void validate_ParseDayString() {
        assertEquals("Availability parseDay failed - String sunDay", Availability.Day.SUNDAY, Availability.Day.parse("sunDay"));
        assertEquals("Availability parseDay failed - String MONDay", Availability.Day.MONDAY, Availability.Day.parse("MONDay"));
        assertEquals("Availability parseDay failed - String TuesDay", Availability.Day.TUESDAY, Availability.Day.parse("TuesDay"));
        assertEquals("Availability parseDay failed - String WEDNESDAY", Availability.Day.WEDNESDAY, Availability.Day.parse("WEDNESDAY"));
        assertEquals("Availability parseDay failed - String thursday", Availability.Day.THURSDAY, Availability.Day.parse("thursday"));
        assertEquals("Availability parseDay failed - String Friday", Availability.Day.FRIDAY, Availability.Day.parse("Friday"));
        assertEquals("Availability parseDay failed - String saTURday", Availability.Day.SATURDAY, Availability.Day.parse("saTURday"));
    }

    /**
     * This method makes sure that the parseDay(int) method works as expected
     */
    @Test
    public void validate_ParseDayInt() {
        assertEquals("Availability parseDay failed - int 0", Availability.Day.SUNDAY, Availability.Day.parse(0));
        assertEquals("Availability parseDay failed - int 1", Availability.Day.MONDAY, Availability.Day.parse(1));
        assertEquals("Availability parseDay failed - int 2", Availability.Day.TUESDAY, Availability.Day.parse(2));
        assertEquals("Availability parseDay failed - int 3", Availability.Day.WEDNESDAY, Availability.Day.parse(3));
        assertEquals("Availability parseDay failed - int 4", Availability.Day.THURSDAY, Availability.Day.parse(4));
        assertEquals("Availability parseDay failed - int 5", Availability.Day.FRIDAY, Availability.Day.parse(5));
        assertEquals("Availability parseDay failed - int 6", Availability.Day.SATURDAY, Availability.Day.parse(6));
    }

    /**
     * This method makes sure that the availability equals method works as expected
     */
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
