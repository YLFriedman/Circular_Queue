package ca.uottawa.seg2105.project.cqondemand;


import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation.PasswordValidationResult;
import static ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation.usernameIsValid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FieldValidationTest {

    public String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public String numbers = "1234567890";
    public String stdSpecialChars = "<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\";

    /*@Test
    public void email_validation() {
        assertEquals("usernameIsValid failed - empty string",false, FieldValidation.emailIsValid(""));
        assertEquals("usernameIsValid failed - good address",true, FieldValidation.emailIsValid("david@uottawa.ca"));
        assertEquals("usernameIsValid failed - missing TLD",false, FieldValidation.emailIsValid("david@uottawa"));
    }*/

    @Test
    public void username_validation() {
        assertFalse("usernameIsValid failed - empty string", FieldValidation.usernameIsValid(""));
        assertFalse("usernameIsValid failed - invalid character: ;", FieldValidation.usernameIsValid("cq;on;demand"));
        assertFalse("usernameIsValid failed - invalid characters: {}", FieldValidation.usernameIsValid("cq{on}demand"));
        assertTrue("usernameIsValid failed - valid username", FieldValidation.usernameIsValid("cq_on_demand"));
    }

    @Test
    public void password_validation() {
        assertEquals("PasswordValidationResult failed - VALID", PasswordValidationResult.VALID, FieldValidation.validatePassword("test", "thisismypassword", "thisismypassword"));
        assertEquals("PasswordValidationResult failed - admin",  PasswordValidationResult.VALID, FieldValidation.validatePassword("admin", "admin", "admin"));
        assertEquals("PasswordValidationResult failed - EMPTY",  PasswordValidationResult.EMPTY, FieldValidation.validatePassword("test", "", ""));
        assertEquals("PasswordValidationResult failed - TOO_SHORT", PasswordValidationResult.TOO_SHORT, FieldValidation.validatePassword("test", "pass", "pass"));
        assertEquals("PasswordValidationResult failed - CONFIRM_MISMATCH", PasswordValidationResult.CONFIRM_MISMATCH, FieldValidation.validatePassword("test", "thisismypassword", "thisismypassword2"));
        assertEquals("PasswordValidationResult failed - ILLEGAL_PASSWORD", PasswordValidationResult.ILLEGAL_PASSWORD, FieldValidation.validatePassword("test", "password", "password"));
        assertEquals("PasswordValidationResult failed - CONTAINS_USERNAME",PasswordValidationResult.CONTAINS_USERNAME, FieldValidation.validatePassword("test", "passwordtest", "passwordtest"));
    }

    @Test
    public void person_name_validation() {
        // Illegal Name Characters: 0-9 < > ] [ } { \ / ! @ # $ % ^ & * _ + = ) ( : ;
        assertFalse("nameIsValid failed - empty string", FieldValidation.nameIsValid(""));
        assertFalse("nameIsValid failed - number", FieldValidation.nameIsValid("Ethan4"));
        assertFalse("nameIsValid failed - character: <", FieldValidation.nameIsValid("Jam<s"));
        assertFalse("nameIsValid failed - character: >", FieldValidation.nameIsValid("Samu>l"));
        assertFalse("nameIsValid failed - character: ]", FieldValidation.nameIsValid("Bru]e"));
        assertFalse("nameIsValid failed - character: [", FieldValidation.nameIsValid("S[mon"));
        assertFalse("nameIsValid failed - character: }", FieldValidation.nameIsValid("Ash}ey"));
        assertFalse("nameIsValid failed - character: {", FieldValidation.nameIsValid("{arl"));
        assertFalse("nameIsValid failed - character: \\", FieldValidation.nameIsValid("\\iam"));
        assertFalse("nameIsValid failed - character: /", FieldValidation.nameIsValid("/vana"));
        assertFalse("nameIsValid failed - character: !", FieldValidation.nameIsValid("Exc!aim"));
        assertFalse("nameIsValid failed - character: @", FieldValidation.nameIsValid("A@ron"));
        assertFalse("nameIsValid failed - character: #", FieldValidation.nameIsValid("S#a"));
        assertFalse("nameIsValid failed - character: $", FieldValidation.nameIsValid("Bill$"));
        assertFalse("nameIsValid failed - character: %", FieldValidation.nameIsValid("Pet%r"));
        assertFalse("nameIsValid failed - character: ^", FieldValidation.nameIsValid("Ush^e"));
        assertFalse("nameIsValid failed - character: &", FieldValidation.nameIsValid("&mmoy"));
        assertFalse("nameIsValid failed - character: *", FieldValidation.nameIsValid("Steph*nie"));
        assertFalse("nameIsValid failed - character: _", FieldValidation.nameIsValid("Pau_"));
        assertFalse("nameIsValid failed - character: +", FieldValidation.nameIsValid("+heresa"));
        assertFalse("nameIsValid failed - character: =", FieldValidation.nameIsValid("Elizab=th"));
        assertFalse("nameIsValid failed - character: )", FieldValidation.nameIsValid("Mad)son"));
        assertFalse("nameIsValid failed - character: (", FieldValidation.nameIsValid("(raig"));
        assertFalse("nameIsValid failed - character: :", FieldValidation.nameIsValid(":sabelle"));
        assertFalse("nameIsValid failed - character: ;", FieldValidation.nameIsValid("Sabr;na"));
        assertTrue("nameIsValid failed - valid name: '", FieldValidation.nameIsValid("D' Amico"));
        assertTrue("nameIsValid failed - valid name2: -", FieldValidation.nameIsValid("My-Dashed-Name"));
    }

    @Test
    public void service_Name_Validation() {
        assertTrue("name validation failed - apostrophe returns invalid", FieldValidation.serviceNameIsValid("'"));
        assertTrue("name validation failed - space returns invalid", FieldValidation.serviceNameIsValid(" ") );
        assertTrue("name validation failed - alphabet return invalid", FieldValidation.serviceNameIsValid(alphabet));
        assertFalse("name validation failed - exclamation mark returned valid", FieldValidation.serviceNameIsValid("hudi!"));
        assertFalse("name validation failed - numbers return valid", FieldValidation.serviceNameIsValid(numbers));
        assertFalse("name validation failed - tab return valid", FieldValidation.serviceNameIsValid("\t"));
        assertFalse("name validation failed - symbols return valid", FieldValidation.serviceNameIsValid(stdSpecialChars));
    }

    @Test
    public void category_Name_Validation() {
        assertTrue("name validation failed - apostrophe returns invalid", FieldValidation.categoryNameIsValid("'"));
        assertTrue("name validation failed - space returns invalid", FieldValidation.categoryNameIsValid(" ") );
        assertTrue("name validation failed - alphabet return invalid", FieldValidation.serviceNameIsValid(alphabet));
        assertFalse("name validation failed - exclamation mark returned valid", FieldValidation.categoryNameIsValid("hudi!"));
        assertFalse("name validation failed - numbers return valid", FieldValidation.categoryNameIsValid(numbers));
        assertFalse("name validation failed - tab return valid", FieldValidation.categoryNameIsValid("\t"));
        assertFalse("name validation failed - symbols return valid", FieldValidation.serviceNameIsValid(stdSpecialChars));
    }

}
