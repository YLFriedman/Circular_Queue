package ca.uottawa.seg2105.project.cqondemand;


import org.junit.Test;

import ca.uottawa.seg2105.project.cqondemand.domain.User;
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
        assertEquals("nameIsValid failed - empty string",false, FieldValidation.nameIsValid(""));
        assertEquals("nameIsValid failed - number",false, FieldValidation.nameIsValid("Ethan4"));
        assertEquals("nameIsValid failed - character: <",false, FieldValidation.nameIsValid("Jam<s"));
        assertEquals("nameIsValid failed - character: >",false, FieldValidation.nameIsValid("Samu>l"));
        assertEquals("nameIsValid failed - character: ]",false, FieldValidation.nameIsValid("Bru]e"));
        assertEquals("nameIsValid failed - character: [",false, FieldValidation.nameIsValid("S[mon"));
        assertEquals("nameIsValid failed - character: }",false, FieldValidation.nameIsValid("Ash}ey"));
        assertEquals("nameIsValid failed - character: {",false, FieldValidation.nameIsValid("{arl"));
        assertEquals("nameIsValid failed - character: \\",false, FieldValidation.nameIsValid("\\iam"));
        assertEquals("nameIsValid failed - character: /",false, FieldValidation.nameIsValid("/vana"));
        assertEquals("nameIsValid failed - character: !",false, FieldValidation.nameIsValid("Exc!aim"));
        assertEquals("nameIsValid failed - character: @",false, FieldValidation.nameIsValid("A@ron"));
        assertEquals("nameIsValid failed - character: #",false, FieldValidation.nameIsValid("S#a"));
        assertEquals("nameIsValid failed - character: $",false, FieldValidation.nameIsValid("Bill$"));
        assertEquals("nameIsValid failed - character: %",false, FieldValidation.nameIsValid("Pet%r"));
        assertEquals("nameIsValid failed - character: ^",false, FieldValidation.nameIsValid("Ush^e"));
        assertEquals("nameIsValid failed - character: &",false, FieldValidation.nameIsValid("&mmoy"));
        assertEquals("nameIsValid failed - character: *",false, FieldValidation.nameIsValid("Steph*nie"));
        assertEquals("nameIsValid failed - character: _",false, FieldValidation.nameIsValid("Pau_"));
        assertEquals("nameIsValid failed - character: +",false, FieldValidation.nameIsValid("+heresa"));
        assertEquals("nameIsValid failed - character: =",false, FieldValidation.nameIsValid("Elizab=th"));
        assertEquals("nameIsValid failed - character: )",false, FieldValidation.nameIsValid("Mad)son"));
        assertEquals("nameIsValid failed - character: (",false, FieldValidation.nameIsValid("(raig"));
        assertEquals("nameIsValid failed - character: :",false, FieldValidation.nameIsValid(":sabelle"));
        assertEquals("nameIsValid failed - character: ;",false, FieldValidation.nameIsValid("Sabr;na"));
        assertEquals("nameIsValid failed - valid name: ",true, FieldValidation.nameIsValid("D' Amico"));
        assertEquals("nameIsValid failed - valid name2: ",true, FieldValidation.nameIsValid("My-Dashed-Name"));
    }

    @Test
    public void service_Name_Validation(){
        assertEquals("name validation failed - apostrophe returns invalid", true, FieldValidation.serviceNameIsValid("'"));
        assertEquals("name validation failed - space returns invalid", true, FieldValidation.serviceNameIsValid(" ") );
        assertEquals("name validation failed - alphabet return invalid", true,
                FieldValidation.serviceNameIsValid("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals("name validation failed - exclamation mark returned valid", false, FieldValidation.serviceNameIsValid("hudi!"));
        assertEquals("name validation failed - numbers return valid", false, FieldValidation.serviceNameIsValid("1234567890"));
        assertEquals("name validation failed - tab return valid", false, FieldValidation.serviceNameIsValid("\t"));
        assertEquals("name validation failed - symbols return valid", false,
                FieldValidation.serviceNameIsValid("<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\"));
    }


    @Test
    public void category_Name_Validation(){
        assertEquals("name validation failed - apostrophe returns invalid", true, FieldValidation.categoryNameIsValid("'"));
        assertEquals("name validation failed - space returns invalid", true, FieldValidation.categoryNameIsValid(" ") );
        assertEquals("name validation failed - alphabet return invalid", true,
                FieldValidation.serviceNameIsValid("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals("name validation failed - exclamation mark returned valid", false, FieldValidation.categoryNameIsValid("hudi!"));
        assertEquals("name validation failed - numbers return valid", false, FieldValidation.categoryNameIsValid("1234567890"));
        assertEquals("name validation failed - tab return valid", false, FieldValidation.categoryNameIsValid("\t"));
        assertEquals("name validation failed - symbols return valid", false,
                FieldValidation.serviceNameIsValid("<>,./?;:/\"|[]{}`~=_+@#$%^&*()\\"));
    }


}
