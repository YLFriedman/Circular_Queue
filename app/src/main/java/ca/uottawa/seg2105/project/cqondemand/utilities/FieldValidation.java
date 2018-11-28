package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Patterns;

/**
 * The class <b>FieldValidation</b> is used to manage the validation of data fields throughout the application.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class FieldValidation {

    /**
     * The general REGEX used for fields which only support letters or spaces
     */
    private static final String LETTER_OR_SPACE_REGEX = "^[\\p{L} ]+$";

    /**
     * The human friendly message used to display which characters are allowed by the LETTER_OR_SPACE_REGEX filter
     */
    public static final String LETTER_OR_SPACE_CHARS = "a-z A-Z space";

    /**
     * The REGEX used for to detect illegal characters in a username
     */
    private static final String ILLEGAL_USERNAME_REGEX = ".*[^a-zA-Z0-9_.-].*";

    /**
     * The human friendly message used to display which characters are allowed in the username
     */
    public static final String USERNAME_CHARS = "a-z A-Z 0-9 _ . -";

    /**
     * The REGEX used for to detect illegal characters in a company name
     */
    private static final String ILLEGAL_COMPANY_NAME_REGEX = ".*[^'\\p{L} &+.-].*";

    /**
     * The human friendly message used to display which characters are allowed in a company name
     */
    public static final String COMPANY_NAME_CHARS = "a-z A-Z - ' & + . space";

    /**
     * The REGEX used for to detect a valid canadian postal code
     */
    private static final String POSTAL_CODE_REGEX = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";

    /**
     * The REGEX used for to detect a valid address unit
     */
    private static final String ADDRESS_UNIT_REGEX = "^[a-zA-Z0-9/]*$";

    /**
     * The human friendly message used to display which characters are allowed in an address unit
     */
    public static final String ADDRESS_UNIT_CHARS = "0-9 a-z A-Z /";

    /**
     * The REGEX used for to detect a valid street name
     */
    private static final String STREET_NAME_REGEX = "^[\\p{L}0-9/ '.-]+$"; // \p{L} represents Unicode letters (upper and lower case)

    /**
     * The human friendly message used to display which characters are allowed in a street name
     */
    public static final String STREET_NAME_CHARS = "a-z A-Z / ' . - space";

    /**
     * The human friendly message used to display which characters are allowed in a city name
     */
    public static final String CITY_NAME_CHARS = LETTER_OR_SPACE_CHARS;

    /**
     * The human friendly message used to display which characters are allowed in a province name
     */
    public static final String PROVINCE_NAME_CHARS = LETTER_OR_SPACE_CHARS;

    /**
     * The human friendly message used to display which characters are allowed in a country name
     */
    public static final String COUNTRY_NAME_CHARS = LETTER_OR_SPACE_CHARS;

    /**
     * The REGEX used for to detect illegal characters in a person's name
     */
    private static final String ILLEGAL_PERSON_NAME_REGEX = ".*[0-9<>\\]\\[}{\\\\/!?@#$%^&*_+=)(:;].*";

    /**
     * The human friendly message used to display which characters are not allowed in a person's name
     */
    public static final String ILLEGAL_PERSON_NAME_CHARS = "0-9 < > ] [ } { \\ / ! @ # $ % ^ & * _ + = ) (";

    /**
     * An enumeration of the possible validation results for a password validation check
     */
    public enum PasswordValidationResult { VALID, EMPTY, TOO_SHORT, CONFIRM_MISMATCH, ILLEGAL_PASSWORD, CONTAINS_USERNAME }

    /**
     * The minimum number of characters allowed in a valid password
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * A list of passwords that are banned form being used
     */
    private static final String[] ILLEGAL_PASSWORDS = { "password" };

    /**
     * A list of usernames that are reserved for special use
     */
    private static final String[] RESERVED_USERNAMES = { "admin" };

    /**
     * The REGEX used for to detect illegal characters in an objects name (ie. Category or Service Name)
     */
    private static final String ILLEGAL_OBJECT_NAME_REGEX = ".*[^'\\p{L} -].*";

    /**
     * The human friendly message used to display which characters are allowed in a service name
     */
    public static final String SERVICE_NAME_CHARS = "a-z A-Z - ' space";

    /**
     * The human friendly message used to display which characters are allowed in a category name
     */
    public static final String CATEGORY_NAME_CHARS = SERVICE_NAME_CHARS;


    /*******************************************
     * Generic, Re-usable functions
     *******************************************/

    /**
     * A generic name checking function that tests the validity of an object name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    private static boolean objectNameIsValid(String name) {
        if (name == null || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_OBJECT_NAME_REGEX);
    }

    /**
     * A utility method used to test if a string is contained within a list.
     * The method uses a case-insensitive comparison.
     * @param list the list of strings to test against
     * @param value the value to test with
     * @return true if value is contained within the list, false otherwise
     */
    private static boolean contains(@NonNull String[] list, @Nullable String value) {
        if (null == value) { return false; }
        for (String item: list) { if (value.toLowerCase().equals(item)) { return true; } }
        return false;
    }

    /*******************************************
     * Validation of Service fields
     *******************************************/

    /**
     * Checks the validity of a service name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean serviceNameIsValid(String name) {
        return objectNameIsValid(name);
    }

    /*******************************************
     * Validation of Category fields
     *******************************************/

    /**
     * Checks the validity of a category name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean categoryNameIsValid(String name){
        return objectNameIsValid(name);
    }

    /*******************************************
     * Validation of User Account fields
     *******************************************/

    /**
     * Checks the validity of a person's name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean personNameIsValid(String name) {
        if (null == name || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_PERSON_NAME_REGEX);
    }

    /**
     * Checks if a username is reserved.
     * @param username the username to test
     * @return true if the name is reserved, false otherwise
     */
    public static boolean usernameIsReserved(String username) {
        return FieldValidation.contains(RESERVED_USERNAMES, username);
    }

    /**
     * Checks the validity of a username.
     * @param username the username to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean usernameIsValid(String username) {
        if (null == username || username.isEmpty()) { return false; }
        return !username.matches(ILLEGAL_USERNAME_REGEX);
    }

    /**
     * Checks the validity of an email address.
     * @param email the email to test
     * @return true if the email is valid, false otherwise
     */
    public static boolean emailIsValid(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks the validity of a password.
     * @param username the username of the account that the password is being used for
     * @param password the password to check for validity
     * @param confirmPassword the second attempt at entering the password by the user
     * @return the validation result of the password test
     */
    public static PasswordValidationResult validatePassword(String username, String password, String confirmPassword) {
        if (null == password || password.isEmpty()) { return PasswordValidationResult.EMPTY; }
        if (null != username && username.equals("admin") && password.equals("admin")) { return PasswordValidationResult.VALID; }
        if (password.length() < PASSWORD_MIN_LENGTH) { return PasswordValidationResult.TOO_SHORT; }
        if (null != username && password.toLowerCase().contains(username.toLowerCase())) { return PasswordValidationResult.CONTAINS_USERNAME; }
        if (FieldValidation.contains(ILLEGAL_PASSWORDS, password)) { return PasswordValidationResult.ILLEGAL_PASSWORD; }
        if (!password.equals(confirmPassword)) { return PasswordValidationResult.CONFIRM_MISMATCH; }
        return PasswordValidationResult.VALID;
    }

    /**
     * Checks the validity of a company name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean companyNameIsValid(String name) {
        if (name == null || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_COMPANY_NAME_REGEX);
    }

    /**
     * Checks the validity of a phone number.
     * @param phone the name to test
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean phoneIsValid(String phone) {
        if (null == phone || phone.isEmpty()) { return false; }
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    /*******************************************
     * Validation of Address fields
     *******************************************/

    /**
     * Checks the validity of an address unit.
     * @param unit the unit to test
     * @return true if the unit is valid, false otherwise
     */
    public static boolean unitIsValid(String unit) {
        return null == unit || unit.matches(ADDRESS_UNIT_REGEX);
    }

    /**
     * Checks the validity of a street name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean streetNameIsValid(String name) {
        return null != name && name.matches(STREET_NAME_REGEX);
    }

    /**
     * Checks the validity of a street number.
     * @param num the number to test
     * @return true if the number is valid, false otherwise
     */
    public static boolean streetNumberIsValid(int num) {
        return num > 0;
    }

    /**
     * Checks the validity of a city name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean cityNameIsValid(String name) {
        return null != name && name.matches(LETTER_OR_SPACE_REGEX);
    }

    /**
     * Checks the validity of a province name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean provinceNameIsValid(String name) {
        return null != name && name.matches(LETTER_OR_SPACE_REGEX);
    }

    /**
     * Checks the validity of a country name.
     * @param name the name to test
     * @return true if the name is valid, false otherwise
     */
    public static boolean countryNameIsValid(String name) {
        return null != name && name.matches(LETTER_OR_SPACE_REGEX);
    }

    /**
     * Checks the validity of a Canadian postal code.
     * @param postalCode the postal code to test
     * @return true if the postal code is valid, false otherwise
     */
    public static boolean postalCodeIsValid(String postalCode){
        if (null == postalCode || postalCode.toString().isEmpty()) { return false; }
        return postalCode.matches(POSTAL_CODE_REGEX);
    }

    /*******************************************
     * Validation of Review fields
     *******************************************/

    public static boolean ratingIsValid(int rating){
        return rating >= 0 && rating <= 5;
    }
}
