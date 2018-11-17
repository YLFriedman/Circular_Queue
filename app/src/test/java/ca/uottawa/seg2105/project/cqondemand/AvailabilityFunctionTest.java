package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;
import ca.uottawa.seg2105.project.cqondemand.domain.Availability;
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
        assertEquals("Service getter Failed - key", "111", testAvailability.getKey());
        assertEquals("Service getter Failed - day", Availability.Day.SUNDAY, testAvailability.getDay());
        assertEquals("Service getter Failed - startTime", 12, testAvailability.getStartTime());
        assertEquals("Service getter Failed - endTime", 15, testAvailability.getEndTime());
    }

    @Test
    public void validate_ParseDayString(){
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.SUNDAY, Availability.parseDay("sunDay"));
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.MONDAY, Availability.parseDay("MONDay"));
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.TUESDAY, Availability.parseDay("TuesDay"));
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.WEDNESDAY, Availability.parseDay("WEDNESDAY"));
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.THURSDAY, Availability.parseDay("thursday"));
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.FRIDAY, Availability.parseDay("Friday"));
        assertEquals("Day validation failed - String_of_letters_Day", Availability.Day.SATURDAY, Availability.parseDay("saTURday"));
    }

    @Test
    public void validate_ParseDayInt(){
        assertEquals("Day validation failed - Int_Day", Availability.Day.SUNDAY, Availability.parseDay(0));
        assertEquals("Day validation failed - Int_Day", Availability.Day.MONDAY, Availability.parseDay(1));
        assertEquals("Day validation failed - Int_Day", Availability.Day.TUESDAY, Availability.parseDay(2));
        assertEquals("Day validation failed - Int_Day", Availability.Day.WEDNESDAY, Availability.parseDay(3));
        assertEquals("Day validation failed - Int_Day", Availability.Day.THURSDAY, Availability.parseDay(4));
        assertEquals("Day validation failed - Int_Day", Availability.Day.FRIDAY, Availability.parseDay(5));
        assertEquals("Day validation failed - Int_Day", Availability.Day.SATURDAY, Availability.parseDay(6));
    }

}
