package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ca.uottawa.seg2105.project.cqondemand.domain.Booking;
import ca.uottawa.seg2105.project.cqondemand.domain.User;
import ca.uottawa.seg2105.project.cqondemand.domain.Review;
import ca.uottawa.seg2105.project.cqondemand.utilities.InvalidDataException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ReviewFunctionTest {
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

        // Make sure InvalidDataException is thrown for invalid data
        /*try {
            new Review("", new Date(2018), 5, "Good", "Fixing", "Jeff", "123");
            fail("Review Constructor Failed - Illegal key");
        } catch (InvalidDataException ignore) {}

        try {
            new Review("111", new Date(-2000), 5, "Good", "Fixing", "Jeff", "123");
            fail("Review Constructor Failed - Illegal Date");
        } catch (InvalidDataException ignore) {}
        try {
            new Review("111", new Date(2018), 50000, "Good", "Fixing", "Jeff", "123");
            fail("Address Constructor Failed - Illegal Rating");
        } catch (InvalidDataException ignore) {}
        try {
            new Review("111", new Date(2018), 5, "IllegalDate", "Fixing", "Jeff", "123");
            fail("Address Constructor Failed - Illegal Comment");
        } catch (InvalidDataException ignore) {}
        try {
            new Review("111", new Date(2018), 5, "Good", "Illegal+NAME___", "Jeff", "123");
            fail("Address Constructor Failed - Illegal ServiceName");
        } catch (InvalidDataException ignore) {}
        try {
            new Review("111", new Date(2018), 5, "Good", "Fixing", "Jeff___+++Illegal", "123");
            fail("Address Constructor Failed - Illegal ReviewerName");
        } catch (InvalidDataException ignore) {}
        try {
            new Review("111", new Date(2018), 5, "Good", "Fixing", "Jeff", "");
            fail("Address Constructor Failed - Illegal ReviewerKey");
        } catch (InvalidDataException ignore) {}
        */

    }

    public void validate_Constructor() {

        User user = new User("firstName", "lastName", "username", "email",
                User.Type.ADMIN, "password");

        Booking booked = new Booking(new Date(2018), new Date(2019), new Date(2018), user,
                "SPKey", "HOKey", Booking.Status.APPROVED, "serviceName",
                10, "cancelledReason", true);

        // Test the creation of an object and its getters
        try {
            Review testReview = new Review(5, "Good", user, booked);
            assertEquals("Review getter Failed - Rating", 5, testReview.getRating());
            assertEquals("Review getter Failed - comment", "Good", testReview.getComment());
            assertEquals("Review getter Failed - serviceName", booked.getServiceName(), testReview.getServiceName());
            assertEquals("Review getter Failed - reviewerName", user.getFullName(), testReview.getReviewerName());
            assertEquals("Review getter Failed - reviewerKey", user.getKey(), testReview.getReviewerKey());
            assertEquals("Review getter Failed - Key", booked.getKey(), testReview.getKey());

        } catch (InvalidDataException e) {
            fail("Review construction failed. Error: " + e.getMessage());
        }
    }
}
