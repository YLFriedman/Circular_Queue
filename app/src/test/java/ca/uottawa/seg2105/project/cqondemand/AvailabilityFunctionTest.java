package ca.uottawa.seg2105.project.cqondemand;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.ArrayList;

import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
import ca.uottawa.seg2105.project.cqondemand.domain.User;

import static org.junit.Assert.assertEquals;

public class AvailabilityFunctionTest {
    @Test
    public void validate_ConstructorNoKey() {
        Availability testAvailability = new Availability(Availability.parseDay("SUNDAY"), 12, 15);
        assertEquals("Service getter Failed - day", Availability.Day.SUNDAY, testAvailability.getDay());
        assertEquals("Service getter Failed - startTime", 12, testAvailability.getStartTime());
        assertEquals("Service getter Failed - endTime", 15, testAvailability.getEndTime());
    }

    @Test
    public void validate_ConstructorKey() {
        Availability testAvailability = new Availability("111", Availability.parseDay("SUNDAY"), 12, 15);
        assertEquals("Service getter Failed - key", Availability.Day.SUNDAY, testAvailability.getKey());
        assertEquals("Service getter Failed - day", "SUNDAY", testAvailability.getDay());
        assertEquals("Service getter Failed - startTime", 12, testAvailability.getStartTime());
        assertEquals("Service getter Failed - endTime", 15, testAvailability.getEndTime());
    }
}
