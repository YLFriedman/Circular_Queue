package ca.uottawa.seg2105.project.cqondemand.utilities;

import android.support.annotation.NonNull;

public class FieldValidation {

    private static final String ILLEGAL_USERNAME_CHARS_REGEX = ".*[^a-zA-Z0-9_.-].*";
    public static final String ILLEGAL_USERNAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z 0-9 _ . -";

    private static final String ILLEGAL_STREET_CHARS_REGEX = "[a-zA-Z]+";
    private static final String ILLEGAL_POSTAL_CODE_REGEX = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";

    // Illegal Name Characters: 0-9 < > ] [ } { \ / ! @ # $ % ^ & * _ + = ) (
    private static final String ILLEGAL_PERSON_NAME_CHARS_REGEX = ".*[0-9<>\\]\\[}{\\\\/!?@#$%^&*_+=)(:;].*";
    public static final String ILLEGAL_PERSON_NAME_CHARS_MSG = "The following characters are NOT allowed: 0-9 < > ] [ } { \\ / ! @ # $ % ^ & * _ + = ) ( '";

    public static final int PASSWORD_MIN_LENGTH = 6;
    private static final String[] ILLEGAL_PASSWORDS = { "password" };
    private static final String[] RESERVED_USERNAMES = { "admin" };
    public enum PasswordValidationResult { VALID, EMPTY, TOO_SHORT, CONFIRM_MISMATCH, ILLEGAL_PASSWORD, CONTAINS_USERNAME }

    private static final String ILLEGAL_OBJECT_NAME_CHARS_REGEX = ".*[^'a-zA-Z -].*";
    public static final String ILLEGAL_SERVICE_NAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z - space '";
    public static final String ILLEGAL_CATEGORY_NAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z - space '";

    public static boolean serviceNameIsValid(String name) {
        if (name == null || name.isEmpty()) { return false;  }
        return !name.matches(ILLEGAL_OBJECT_NAME_CHARS_REGEX);
    }

    public static boolean categoryNameIsValid(String name){
        if(name == null || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_OBJECT_NAME_CHARS_REGEX);
    }

    public static boolean usernameIsReserved(String username) {
        return FieldValidation.contains(RESERVED_USERNAMES, username);
    }

    public static boolean usernameIsValid(String username) {
        if (null == username || username.isEmpty()) { return false; }
        if (username.matches(ILLEGAL_USERNAME_CHARS_REGEX)) { return false; }
        return true;
    }

    public static boolean nameIsValid(String name) {
        if (null == name || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_PERSON_NAME_CHARS_REGEX);
    }

    public static boolean emailIsValid(CharSequence email) {
        if (null == email || email.toString().isEmpty()) { return false; }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean phoneIsValid(CharSequence phone) {
        if (null == phone || phone.toString().isEmpty()) { return false; }
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean unitIsValid(CharSequence unit) {
        if (unit.length() > 9)
            return false;
        String str = "^[0-9]*$";            //Only numbers are allowed
        return unit.toString().matches(str);
    }

    public static boolean numberIsValid(CharSequence num) {
        if (null == num || num.length() > 9)
            return false;
        String str = "^[0-9]*$";            //Only numbers are allowed
        return num.toString().matches(str);
    }

    public static boolean letterNameIsValid(String street) {
        if (null == street || street.isEmpty()) { return false; }
        return street.matches(ILLEGAL_STREET_CHARS_REGEX);
    }

    public static boolean postalCodeIsValid(CharSequence zip){
        if (null == zip || zip.toString().isEmpty()) { return false; }
        return zip.toString().matches(ILLEGAL_POSTAL_CODE_REGEX);
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

    private static boolean contains(@NonNull String[] list, @NonNull String value) {
        for (String item: list) {
            if (value.toLowerCase().equals(item)) { return true; }
        }
        return false;
    }
}
