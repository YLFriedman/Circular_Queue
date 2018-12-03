package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import ca.uottawa.seg2105.project.cqondemand.domain.Address;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.Service;
import ca.uottawa.seg2105.project.cqondemand.domain.ServiceProvider;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The class <b> ReviewFunctionTest</b> tests all methods belonging to the Review Class
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class ReviewFunctionTest {

    public static Address genericAddress =  new Address("2a", 12, "Bullvue", "Ottawa", "Ontario", "Canada", "K0A1A0");
    public static User genericHomeowner = new User( "key", "firstName", "lastName", "username", "{@TEST}",
            User.Type.HOMEOWNER, "passtest");
    public static ServiceProvider genericProvider = new ServiceProvider("firstName", "lastName", "username", "{@TEST}", "passtest",
            "companyName", true, "{@TEST}", genericAddress, "description");

    public static Service genericService = new Service("serviceName", 10, "key");
    public static Date now = new Date(System.currentTimeMillis() + 100000);
    public static Date later = new Date(System.currentTimeMillis() + 1000000);

    public static Booking genericBooking = new Booking(now, later, genericHomeowner, genericProvider, genericService);

    /**
     * This method tests the Review constructor (with key) and various getters
     */
    @Test
    public void validate_ConstructorRawInputs() {

        // Test the creation of an object and its getters
        try {
            Review testReview = new Review("111", new Date(2018), 5, "Good", "Fixing", "Jeff", "123");
            assertEquals("Review getter Failed - key", "111", testReview.getKey());
            assertEquals("Review getter Failed - date", new Date(2018), testReview.getDateCreated());
            assertEquals("Review getter Failed - rating", 5, testReview.getRating());
            assertEquals("Review getter Failed - comment", "Good", testReview.getComment());
            assertEquals("Review getter Failed - serviceName", "Fixing", testReview.getServiceName());
            assertEquals("Review getter Failed - reviewerName", "Jeff", testReview.getReviewerName());
            assertEquals("Review getter Failed - reviewerKey", "123", testReview.getReviewerKey());

        } catch (InvalidDataException e) {
            fail("Review construction failed. Error: " + e.getMessage());
        }
    }

    /**
     * This method ensures that the keyless constructor works as expected
     */
    @Test
    public void validateConstructorNoKey() {
        try {
            Review testReview = new Review(5, "good", genericHomeowner, genericBooking);
            assertEquals("Review getter Failed - rating", 5, testReview.getRating());
            assertEquals("Review getter Failed - comment", "good", testReview.getComment());
            assertEquals("Review getter Failed - serviceName", genericService.getName(), testReview.getServiceName());
            assertEquals("Review getter Failed - reviewerName", genericHomeowner.getFullName(), testReview.getReviewerName());
            assertEquals("Review getter Failed - reviewerKey", genericHomeowner.getKey(), testReview.getReviewerKey());

        } catch (InvalidDataException e) {
            fail("Review construction failed. Error: " + e.getMessage());
        }
    }

    /**
     * This method ensures that an exception will be thrown if an empty key is passed to the constructor
     */
    @Test(expected = InvalidDataException.class)
    public void invalidKeyTest() {

        new Review("", new Date(2018), 5, "Good", "Fixing", "Jeff", "123");
        fail("Review Constructor Failed - Illegal key");
    }

    /**
     * This method ensures that an exception will be thrown if an illegal rating is passed to the constructor
     */
    @Test(expected = InvalidDataException.class)
    public void invalidRatingTest() {
        new Review(50000, "Good", genericHomeowner, genericBooking);
        fail("Address Constructor Failed - Illegal Rating");
    }


}
