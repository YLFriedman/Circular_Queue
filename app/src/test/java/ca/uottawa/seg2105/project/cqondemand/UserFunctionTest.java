package ca.uottawa.seg2105.project.cqondemand;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserFunctionTest {

    @Test
    public void username_validation() {
        assertEquals("userNameIsValid failed - empty string",false, User.userNameIsValid(""));
        assertEquals("userNameIsValid failed - invalid character: .",false, User.userNameIsValid("cq.on.demand"));
        assertEquals("userNameIsValid failed - invalid characters: {}",false, User.userNameIsValid("cq{on}demand"));
        assertEquals("userNameIsValid failed - valid username",true, User.userNameIsValid("cq-on_demand"));
    }

    @Test
    public void password_validation() {
        assertEquals("PasswordValidationResult failed - VALID", User.PasswordValidationResult.VALID, User.validatePassword("test", "thisismypassword", "thisismypassword"));
        assertEquals("PasswordValidationResult failed - EMPTY", User.PasswordValidationResult.EMPTY, User.validatePassword("test", "", ""));
        assertEquals("PasswordValidationResult failed - TOO_SHORT", User.PasswordValidationResult.TOO_SHORT, User.validatePassword("test", "pass", "pass"));
        assertEquals("PasswordValidationResult failed - CONFIRM_MISMATCH", User.PasswordValidationResult.CONFIRM_MISMATCH, User.validatePassword("test", "thisismypassword", "thisismypassword2"));
        assertEquals("PasswordValidationResult failed - ILLEGAL_PASSWORD", User.PasswordValidationResult.ILLEGAL_PASSWORD, User.validatePassword("test", "password", "password"));
        assertEquals("PasswordValidationResult failed - CONTAINS_USERNAME", User.PasswordValidationResult.CONTAINS_USERNAME, User.validatePassword("test", "passwordtest", "passwordtest"));
    }

}
