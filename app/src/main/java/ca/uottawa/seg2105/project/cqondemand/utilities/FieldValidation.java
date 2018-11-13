package ca.uottawa.seg2105.project.cqondemand.utilities;

public class FieldValidation {

    private static final String ILLEGAL_USERNAME_CHARS_REGEX = ".*[^a-zA-Z0-9_].*";
    public static final String ILLEGAL_USERNAME_CHARS_MSG = "Only the following characters are allowed: a-z A-Z 0-9 _";

    // Illegal Name Characters: 0-9 < > ] [ } { \ / ! @ # $ % ^ & * _ + = ) (
    private static final String ILLEGAL_PERSON_NAME_CHARS_REGEX = ".*[0-9<>\\]\\[}{\\\\/!@#$%^&*_+=)(:;].*";
    public static final String ILLEGAL_PERSON_NAME_CHARS_MSG = "The following characters are NOT allowed: 0-9 < > ] [ } { \\ / ! @ # $ % ^ & * _ + = ) ( '";

    public static final int PASSWORD_MIN_LENGTH = 6;
    private static final String[] ILLEGAL_PASSWORDS = { "password" };
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

    public static boolean usernameIsValid(String username) {
        if (null == username || username.isEmpty()) { return false; }
        return !username.matches(ILLEGAL_USERNAME_CHARS_REGEX);
    }

    public static boolean nameIsValid(String name) {
        if (null == name || name.isEmpty()) { return false; }
        return !name.matches(ILLEGAL_PERSON_NAME_CHARS_REGEX);
    }

    public static boolean emailIsValid(CharSequence email) {
        if (null == email || email.toString().isEmpty()) { return false; }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static PasswordValidationResult validatePassword(String username, String password, String confirmPassword) {
        if (null == password || password.isEmpty()) { return PasswordValidationResult.EMPTY; }
        if (null != username && username.equals("admin")) { return PasswordValidationResult.VALID; }
        if (password.length() < PASSWORD_MIN_LENGTH) { return PasswordValidationResult.TOO_SHORT; }
        if (null != username && password.toLowerCase().contains(username.toLowerCase())) { return PasswordValidationResult.CONTAINS_USERNAME; }
        for (String illegalPW: ILLEGAL_PASSWORDS) {
            if (password.toLowerCase().equals(illegalPW)) { return PasswordValidationResult.ILLEGAL_PASSWORD; }
        }
        if (!password.equals(confirmPassword)) { return PasswordValidationResult.CONFIRM_MISMATCH; }
        return PasswordValidationResult.VALID;
    }

}
