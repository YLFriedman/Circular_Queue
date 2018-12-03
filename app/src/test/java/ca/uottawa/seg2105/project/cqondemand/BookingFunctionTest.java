package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import java.util.Date;

import androidx.annotation.NonNull;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * The class <b> BookingFunctionTest</b> tests all methods contained in the Booking class
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class BookingFunctionTest {

    public static Address genericAddress =  new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
    public static User genericHomeowner = new User("firstName", "lastName", "username", "{@TEST}",
                                                               User.Type.HOMEOWNER, "passtest");
    public static ServiceProvider genericProvider = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest",
            "companyName", true, "{@TEST}", genericAddress, "description");

    public static Service genericService = new Service("serviceName", 10, "key");
    public int hour = 1000 * 60 * 60;
    public long now = System.currentTimeMillis();
    public long startTime = now + (hour);
    public long endTime = now + (4 * hour);

    /**
     * This method validates the construction of a new Booking object, and getters
     */
    @Test
    public void validate_Constructor() {



        // Test the creation of an object and its getters
        try {

            Booking testBooking = new Booking(new Date(startTime), new Date(endTime), genericHomeowner, genericProvider, genericService);
            testBooking.cancelBooking(new Date(now), "A Reason", "genericProvider");
            assertEquals("Booking getter Failed - startTime", new Date(startTime), testBooking.getStartTime());
            assertEquals("Booking getter Failed - endTime", new Date(endTime), testBooking.getEndTime());
            assertEquals("Booking getter Failed - homeownerKey", genericHomeowner.getKey(), testBooking.getHomeownerKey());
            assertEquals("Booking getter Failed - status", Booking.Status.CANCELLED, testBooking.getStatus());
            assertEquals("Booking getter Failed - endTime", new Date(now), testBooking.getDateCancelledOrApproved());
            assertEquals("Booking getter Failed - serviceName", "serviceName", testBooking.getServiceName());
            assertEquals("Booking getter Failed - serviceRate", 10, testBooking.getServiceRate());
            assertEquals("Booking getter Failed - serviceProviderKey", genericProvider.getKey(), testBooking.getServiceProviderKey());
            assertEquals("Booking getter Failed - homeOwner", genericHomeowner, testBooking.getHomeowner());
            assertEquals("Booking getter Failed - serviceProvider", genericProvider, testBooking.getServiceProvider());
            assertEquals("Booking getter failed - cancelled reason", "A Reason", testBooking.getCancelledReason());
            assertEquals("Booking getter failed - cancelledby", "genericProvider", testBooking.getCancelledBy());

        } catch (InvalidDataException e) {
            fail("Review construction failed. Error: " + e.getMessage());
        }
    }

    /**
     * This method ensures that an exception will be thrown if an empty key is given on construction
     */
    @Test(expected = InvalidDataException.class)
    public void emptyKeyTest() {
        Booking testBooking = new Booking( "", new Date(startTime), new Date(endTime),
                new Date(now), new Date(now), genericHomeowner, "some Ket",
                Booking.Status.APPROVED, genericService.getName(), genericService.getRate(), "reason", "name");

    }
}
