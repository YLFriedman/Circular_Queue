package ca.uottawa.seg2105.project.cqondemand;


import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation;
import ca.uottawa.seg2105.project.cqondemand.utilities.FieldValidation.PasswordValidationResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * The class <b> FieldValidationTest </b> extensively tests all field validation methods
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 * */
public class FieldValidationTest {
    /**
     * Stores all letters
     */
    public String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Stores all numbers
     */
    public String numbers = "1234567890";

    /**
     * Stores special characters
     */
    public String stdSpecialChars = "<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\";


    /**
     * This method tests the username validation method
     */
    @Test
    public void username_validation() {
        assertFalse("usernameIsValid failed - empty string", FieldValidation.usernameIsValid(""));
        assertFalse("usernameIsValid failed - invalid character: ;", FieldValidation.usernameIsValid("cq;on;demand"));
        assertFalse("usernameIsValid failed - invalid characters: {}", FieldValidation.usernameIsValid("cq{on}demand"));
        assertTrue("usernameIsValid failed - valid username", FieldValidation.usernameIsValid("cq_on_demand"));
    }

    /**
     * This method tests the password validation method
     */
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

    /**
     * This method tests the name validation method
     */
    @Test
    public void person_name_validation() {
        // Illegal Name Characters: 0-9 < > ] [ } { \ / ! @ # $ % ^ & * _ + = ) ( : ;
        assertFalse("personNameIsValid failed - empty string", FieldValidation.personNameIsValid(""));
        assertFalse("personNameIsValid failed - number", FieldValidation.personNameIsValid("Ethan4"));
        assertFalse("personNameIsValid failed - character: <", FieldValidation.personNameIsValid("Jam<s"));
        assertFalse("personNameIsValid failed - character: >", FieldValidation.personNameIsValid("Samu>l"));
        assertFalse("personNameIsValid failed - character: ]", FieldValidation.personNameIsValid("Bru]e"));
        assertFalse("personNameIsValid failed - character: [", FieldValidation.personNameIsValid("S[mon"));
        assertFalse("personNameIsValid failed - character: }", FieldValidation.personNameIsValid("Ash}ey"));
        assertFalse("personNameIsValid failed - character: {", FieldValidation.personNameIsValid("{arl"));
        assertFalse("personNameIsValid failed - character: \\", FieldValidation.personNameIsValid("\\iam"));
        assertFalse("personNameIsValid failed - character: /", FieldValidation.personNameIsValid("/vana"));
        assertFalse("personNameIsValid failed - character: !", FieldValidation.personNameIsValid("Exc!aim"));
        assertFalse("personNameIsValid failed - character: @", FieldValidation.personNameIsValid("A@ron"));
        assertFalse("personNameIsValid failed - character: #", FieldValidation.personNameIsValid("S#a"));
        assertFalse("personNameIsValid failed - character: $", FieldValidation.personNameIsValid("Bill$"));
        assertFalse("personNameIsValid failed - character: %", FieldValidation.personNameIsValid("Pet%r"));
        assertFalse("personNameIsValid failed - character: ^", FieldValidation.personNameIsValid("Ush^e"));
        assertFalse("personNameIsValid failed - character: &", FieldValidation.personNameIsValid("&mmoy"));
        assertFalse("personNameIsValid failed - character: *", FieldValidation.personNameIsValid("Steph*nie"));
        assertFalse("personNameIsValid failed - character: _", FieldValidation.personNameIsValid("Pau_"));
        assertFalse("personNameIsValid failed - character: +", FieldValidation.personNameIsValid("+heresa"));
        assertFalse("personNameIsValid failed - character: =", FieldValidation.personNameIsValid("Elizab=th"));
        assertFalse("personNameIsValid failed - character: )", FieldValidation.personNameIsValid("Mad)son"));
        assertFalse("personNameIsValid failed - character: (", FieldValidation.personNameIsValid("(raig"));
        assertFalse("personNameIsValid failed - character: :", FieldValidation.personNameIsValid(":sabelle"));
        assertFalse("personNameIsValid failed - character: ;", FieldValidation.personNameIsValid("Sabr;na"));
        assertTrue("personNameIsValid failed - valid name: '", FieldValidation.personNameIsValid("D' Amico"));
        assertTrue("personNameIsValid failed - valid name2: -", FieldValidation.personNameIsValid("My-Dashed-Name"));
    }

    /**
     * This method tests the service name validation method
     */
    @Test
    public void service_name_validation() {
        assertTrue("name validation failed - apostrophe returns invalid", FieldValidation.serviceNameIsValid("'"));
        assertTrue("name validation failed - space returns invalid", FieldValidation.serviceNameIsValid(" ") );
        assertTrue("name validation failed - alphabet return invalid", FieldValidation.serviceNameIsValid(alphabet));
        assertFalse("name validation failed - exclamation mark returned valid", FieldValidation.serviceNameIsValid("hudi!"));
        assertFalse("name validation failed - numbers return valid", FieldValidation.serviceNameIsValid(numbers));
        assertFalse("name validation failed - tab return valid", FieldValidation.serviceNameIsValid("\t"));
        assertFalse("name validation failed - symbols return valid", FieldValidation.serviceNameIsValid(stdSpecialChars));
    }

    /**
     * This method tests the category name validation method
     */
    @Test
    public void category_name_validation() {
        assertTrue("name validation failed - apostrophe returns invalid", FieldValidation.categoryNameIsValid("'"));
        assertTrue("name validation failed - space returns invalid", FieldValidation.categoryNameIsValid(" ") );
        assertTrue("name validation failed - alphabet return invalid", FieldValidation.serviceNameIsValid(alphabet));
        assertFalse("name validation failed - exclamation mark returned valid", FieldValidation.categoryNameIsValid("hudi!"));
        assertFalse("name validation failed - numbers return valid", FieldValidation.categoryNameIsValid(numbers));
        assertFalse("name validation failed - tab return valid", FieldValidation.categoryNameIsValid("\t"));
        assertFalse("name validation failed - symbols return valid", FieldValidation.serviceNameIsValid(stdSpecialChars));
    }

    /**
     * This method tests the address unit validation method
     */
    @Test
    public void address_unit_validation() {
        assertTrue("address unit validation failed - empty", FieldValidation.unitIsValid(""));
        assertTrue("address unit validation failed - numbers", FieldValidation.unitIsValid(numbers));
        assertTrue("address unit validation failed - alphabet", FieldValidation.unitIsValid(alphabet));
        assertFalse("address unit validation failed - stdSpecialChars", FieldValidation.unitIsValid(stdSpecialChars));
    }

    /**
     * This method tests the postal code validation method
     */
    @Test
    public void postal_code_validation() {
        assertFalse("address unit validation failed - empty", FieldValidation.postalCodeIsValid(""));
        assertFalse("address unit validation failed - lower case", FieldValidation.postalCodeIsValid("k1s 2F5"));
        assertFalse("address unit validation failed - Illegal Characters: DFI", FieldValidation.postalCodeIsValid("D1F 2I2"));
        assertFalse("address unit validation failed - Illegal Characters: DFI", FieldValidation.postalCodeIsValid("D1F 2I2"));
        assertFalse("address unit validation failed - Start with W", FieldValidation.postalCodeIsValid("W1A 2A2"));
        assertFalse("address unit validation failed - Start with Z", FieldValidation.postalCodeIsValid("Z1A 2A2"));
        assertTrue("address unit validation failed - valid", FieldValidation.postalCodeIsValid("K1S 2L5"));
        assertTrue("address unit validation failed - valid", FieldValidation.postalCodeIsValid("K1S2L5"));
    }

}
