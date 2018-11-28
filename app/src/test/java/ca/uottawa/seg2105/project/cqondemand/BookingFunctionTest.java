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

public class BookingFunctionTest {

    @Test
    public void validate_Constructor() {

        User user = new User("firstName", "lastName", "username", "{@TEST}",
                User.Type.ADMIN, "passtest");
        Address testAddressA = new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
        ServiceProvider provider = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest", "companyName", true, "{@TEST}", testAddressA, "description");

        // Test the creation of an object and its getters
        try {
            Booking testBooking = new Booking(new Date(2018), new Date(2019), user, provider, new Service("serviceName", 10, "key"));
            testBooking.cancelBooking(new Date(), null);
            assertEquals("Booking getter Failed - startTime", new Date(2018), testBooking.getStartTime());
            assertEquals("Booking getter Failed - endTime", new Date(2019), testBooking.getEndTime());
            assertEquals("Booking getter Failed - homeownerKey", user.getKey(), testBooking.getHomeownerKey());
            assertEquals("Booking getter Failed - status", Booking.Status.CANCELLED, testBooking.getStatus());
            assertEquals("Booking getter Failed - endTime", new Date(2019), testBooking.getEndTime());
            assertEquals("Booking getter Failed - serviceName", "serviceName", testBooking.getServiceName());
            assertEquals("Booking getter Failed - serviceRate", 10, testBooking.getServiceRate());
            assertEquals("Booking getter Failed - serviceProviderKey", provider.getKey(), testBooking.getServiceProviderKey());
            assertEquals("Booking getter Failed - homeOwner", user, testBooking.getHomeowner());
            assertEquals("Booking getter Failed - serviceProvider", provider, testBooking.getServiceProvider());

        } catch (InvalidDataException e) {
            fail("Review construction failed. Error: " + e.getMessage());
        }
    }
}
