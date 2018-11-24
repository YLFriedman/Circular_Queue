package ca.uottawa.seg2105.project.cqondemand.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Patterns;

public class FieldValidation {

    private static final String LETTER_OR_SPACE_REGEX = "^[\\p{L} ]+$";
    public static final String LETTER_OR_SPACE_CHARS = "a-z A-Z space";

    private static final String ILLEGAL_USERNAME_REGEX = ".*[^a-zA-Z0-9_.-].*";
    public static final String USERNAME_CHARS = "a-z A-Z 0-9 _ . -";

    private static final String ILLEGAL_COMPANY_NAME_REGEX = ".*[^'\\p{L} &+.-].*";
    public static final String COMPANY_NAME_CHARS = "a-z A-Z - ' & + . space";

    private static final String POSTAL_CODE_REGEX = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
    private static final String ADDRESS_UNIT_REGEX = "^[a-zA-Z0-9/]*$";
    public static final String ADDRESS_UNIT_CHARS = "0-9 a-z A-Z /";
    private static final String STREET_NAME_REGEX = "^[\\p{L}0-9/ '.-]+$"; // \p{L} represents Unicode letters (upper and lower case)
    public static final String STREET_NAME_CHARS = "a-z A-Z / ' . - space";
    public static final String CITY_NAME_CHARS = LETTER_OR_SPACE_CHARS;
    public static final String PROVINCE_NAME_CHARS = LETTER_OR_SPACE_CHARS;
    public static final String COUNTRY_NAME_CHARS = LETTER_OR_SPACE_CHARS;

    private static final String ILLEGAL_PERSON_NAME_REGEX = ".*[0-9<>\\]\\[}{\\\\/!?@#$%^&*_+=)(:;].*";
    public static final String ILLEGAL_PERSON_NAME_CHARS = "0-9 < > ] [ } { \\ / ! @ # $ % ^ & * _ + = ) (";

    public enum PasswordValidationResult { VALID, EMPTY, TOO_SHORT, CONFIRM_MISMATCH, ILLEGAL_PASSWORD, CONTAINS_USERNAME }
    public static final int PASSWORD_MIN_LENGTH = 6;
    private static final String[] ILLEGAL_PASSWORDS = { "password" };
    private static final String[] RESERVED_USERNAMES = { "admin" };

    private static final String ILLEGAL_OBJECT_NAME_REGEX = ".*[^'\\p{L} -].*";
    public static final String SERVICE_NAME_CHARS = "a-z A-Z - ' space";
    public static final String CATEGORY_NAME_CHARS = SERVICE_NAME_CHARS;


    /*******************************************
     * Generic, Re-usable functions
     *******************************************/

    private static boolean objectNameIsValid(String name) {
        if (name == null || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_OBJECT_NAME_REGEX);
    }

    private static boolean contains(@NonNull String[] list, @Nullable String value) {
        if (null == value) { return false; }
        for (String item: list) { if (value.toLowerCase().equals(item)) { return true; } }
        return false;
    }

    /*******************************************
     * Validation of Service fields
     *******************************************/

    public static boolean serviceNameIsValid(String name) {
        return objectNameIsValid(name);
    }

    /*******************************************
     * Validation of Category fields
     *******************************************/

    public static boolean categoryNameIsValid(String name){
        return objectNameIsValid(name);
    }

    /*******************************************
     * Validation of User Account fields
     *******************************************/

    public static boolean personNameIsValid(String name) {
        if (null == name || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_PERSON_NAME_REGEX);
    }

    public static boolean usernameIsReserved(String username) {
        return FieldValidation.contains(RESERVED_USERNAMES, username);
    }

    public static boolean usernameIsValid(String username) {
        if (null == username || username.isEmpty()) { return false; }
        return !username.matches(ILLEGAL_USERNAME_REGEX);
    }

    public static boolean emailIsValid(String email) {
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static PasswordValidationResult validatePassword(String username, String password, String confirmPassword) {
        if (null == password || password.isEmpty()) { return PasswordValidationResult.EMPTY; }
        if (null != username && username.equals("admin") && password.equals("admin")) { return PasswordValidationResult.VALID; }
        if (password.length() < PASSWORD_MIN_LENGTH) { return PasswordValidationResult.TOO_SHORT; }
        if (null != username && password.toLowerCase().contains(username.toLowerCase())) { return PasswordValidationResult.CONTAINS_USERNAME; }
        if (FieldValidation.contains(ILLEGAL_PASSWORDS, password)) { return PasswordValidationResult.ILLEGAL_PASSWORD; }
        if (!password.equals(confirmPassword)) { return PasswordValidationResult.CONFIRM_MISMATCH; }
        return PasswordValidationResult.VALID;
    }

    public static boolean companyNameIsValid(String name) {
        if (name == null || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_COMPANY_NAME_REGEX);
    }

    public static boolean phoneIsValid(String phone) {
        if (null == phone || phone.isEmpty()) { return false; }
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    /*******************************************
     * Validation of Address fields
     *******************************************/

    public static boolean unitIsValid(String unit) {
        return null == unit || unit.matches(ADDRESS_UNIT_REGEX);
    }

    public static boolean streetNameIsValid(String name) {
        return null != name && name.matches(STREET_NAME_REGEX);
    }

    public static boolean streetNumberIsValid(int num) {
        return num > 0;
    }

    public static boolean cityNameIsValid(String name) {
        return null != name && name.matches(LETTER_OR_SPACE_REGEX);
    }

    public static boolean provinceNameIsValid(String name) {
        return null != name && name.matches(LETTER_OR_SPACE_REGEX);
    }

    public static boolean countryNameIsValid(String name) {
        return null != name && name.matches(LETTER_OR_SPACE_REGEX);
    }

    public static boolean postalCodeIsValid(String postalCode){
        if (null == postalCode || postalCode.toString().isEmpty()) { return false; }
        return postalCode.matches(POSTAL_CODE_REGEX);
    }

}
