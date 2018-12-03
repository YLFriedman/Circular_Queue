package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.database.DbAvailability;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The class <b> DbAvailabilityFunctionTest </b> tests functions belonging to the DbAvailability class
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class DbAvailabilityFunctionTest {

    /**
     * This method tests the constructor and getters of DbAvailability
     */
    @Test
    public void validate_Constructor() {
        DbAvailability testDbAvailability = new DbAvailability(new Availability(Availability.Day.parse("SUNDAY"), 12, 15));
        Availability testAvailability = testDbAvailability.toDomainObj();
        assertEquals("Availability getter Failed - day", Availability.Day.SUNDAY, testAvailability.getDay());
        assertEquals("Availability getter Failed - startTime", 12, testAvailability.getStartTime());
        assertEquals("Availability getter Failed - endTime", 15, testAvailability.getEndTime());
        try {
            new Availability(Availability.Day.WEDNESDAY, 24, 18);
            fail("Availability Constructor Failed - Illegal Start Time");
        } catch (InvalidDataException ignore) {} catch (IllegalArgumentException ignore) {}
        try {
            new Availability(Availability.Day.WEDNESDAY, 0, 0);
            fail("Availability Constructor Failed - Illegal End Time");
        } catch (InvalidDataException ignore) {} catch (IllegalArgumentException ignore) {}
        try {
            new Availability(Availability.Day.WEDNESDAY, 8, 5);
            fail("Availability Constructor Failed - End Time before Start Time");
        } catch (InvalidDataException ignore) {} catch (IllegalArgumentException ignore) {}
    }

}
