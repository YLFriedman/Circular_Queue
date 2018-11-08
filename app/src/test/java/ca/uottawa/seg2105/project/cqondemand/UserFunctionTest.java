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

    @Test
    public void username_validation() {
        assertEquals("userNameIsValid failed - empty string",false, User.userNameIsValid(""));
        assertEquals("userNameIsValid failed - invalid character: .",false, User.userNameIsValid("cq.on.demand"));
        assertEquals("userNameIsValid failed - invalid characters: {}",false, User.userNameIsValid("cq{on}demand"));
        assertEquals("userNameIsValid failed - valid username",true, User.userNameIsValid("cq_on_demand"));
    }

    @Test
    public void password_validation() {
        assertEquals("PasswordValidationResult failed - VALID", User.PasswordValidationResult.VALID, User.validatePassword("test", "thisismypassword", "thisismypassword"));
        assertEquals("PasswordValidationResult failed - admin", User.PasswordValidationResult.VALID, User.validatePassword("admin", "admin", "admin"));
        assertEquals("PasswordValidationResult failed - EMPTY", User.PasswordValidationResult.EMPTY, User.validatePassword("test", "", ""));
        assertEquals("PasswordValidationResult failed - TOO_SHORT", User.PasswordValidationResult.TOO_SHORT, User.validatePassword("test", "pass", "pass"));
        assertEquals("PasswordValidationResult failed - CONFIRM_MISMATCH", User.PasswordValidationResult.CONFIRM_MISMATCH, User.validatePassword("test", "thisismypassword", "thisismypassword2"));
        assertEquals("PasswordValidationResult failed - ILLEGAL_PASSWORD", User.PasswordValidationResult.ILLEGAL_PASSWORD, User.validatePassword("test", "password", "password"));
        assertEquals("PasswordValidationResult failed - CONTAINS_USERNAME", User.PasswordValidationResult.CONTAINS_USERNAME, User.validatePassword("test", "passwordtest", "passwordtest"));
    }

    @Test
    public void name_validation() {
        // Illegal Name Characters: 0-9 < > ] [ } { \ / ! @ # $ % ^ & * _ + = ) ( : ;
        assertEquals("nameIsValid failed - empty string",false, User.nameIsValid(""));
        assertEquals("nameIsValid failed - number",false, User.nameIsValid("Ethan4"));
        assertEquals("nameIsValid failed - character: <",false, User.nameIsValid("Jam<s"));
        assertEquals("nameIsValid failed - character: >",false, User.nameIsValid("Samu>l"));
        assertEquals("nameIsValid failed - character: ]",false, User.nameIsValid("Bru]e"));
        assertEquals("nameIsValid failed - character: [",false, User.nameIsValid("S[mon"));
        assertEquals("nameIsValid failed - character: }",false, User.nameIsValid("Ash}ey"));
        assertEquals("nameIsValid failed - character: {",false, User.nameIsValid("{arl"));
        assertEquals("nameIsValid failed - character: \\",false, User.nameIsValid("\\iam"));
        assertEquals("nameIsValid failed - character: /",false, User.nameIsValid("/vana"));
        assertEquals("nameIsValid failed - character: !",false, User.nameIsValid("Exc!aim"));
        assertEquals("nameIsValid failed - character: @",false, User.nameIsValid("A@ron"));
        assertEquals("nameIsValid failed - character: #",false, User.nameIsValid("S#a"));
        assertEquals("nameIsValid failed - character: $",false, User.nameIsValid("Bill$"));
        assertEquals("nameIsValid failed - character: %",false, User.nameIsValid("Pet%r"));
        assertEquals("nameIsValid failed - character: ^",false, User.nameIsValid("Ush^e"));
        assertEquals("nameIsValid failed - character: &",false, User.nameIsValid("&mmoy"));
        assertEquals("nameIsValid failed - character: *",false, User.nameIsValid("Steph*nie"));
        assertEquals("nameIsValid failed - character: _",false, User.nameIsValid("Pau_"));
        assertEquals("nameIsValid failed - character: +",false, User.nameIsValid("+heresa"));
        assertEquals("nameIsValid failed - character: =",false, User.nameIsValid("Elizab=th"));
        assertEquals("nameIsValid failed - character: )",false, User.nameIsValid("Mad)son"));
        assertEquals("nameIsValid failed - character: (",false, User.nameIsValid("(raig"));
        assertEquals("nameIsValid failed - character: :",false, User.nameIsValid(":sabelle"));
        assertEquals("nameIsValid failed - character: ;",false, User.nameIsValid("Sabr;na"));
        assertEquals("nameIsValid failed - valid name: ",true, User.nameIsValid("D' Amico"));
    }

}
