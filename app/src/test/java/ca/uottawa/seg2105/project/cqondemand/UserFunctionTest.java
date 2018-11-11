package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.User;

import static org.junit.Assert.*;

public class UserFunctionTest {

    @Test
    public void admin_account() {
        try {
            User adminUser = new User("Admin", "User", "admin", "yfrie071@uottawa.ca", User.Types.ADMIN, "admin");
        } catch(Exception e) {
            System.out.println("Unable to create the admin user. Error: " + e.getMessage());
        }

    }



}
