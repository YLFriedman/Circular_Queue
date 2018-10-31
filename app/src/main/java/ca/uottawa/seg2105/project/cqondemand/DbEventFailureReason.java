package ca.uottawa.seg2105.project.cqondemand;

public enum DbEventFailureReason {

    // General Errors
    DATABASE_ERROR,
    DOES_NOT_EXIST,
    ALREADY_EXISTS,

    // User Specific Errors
    PASSWORD_MISMATCH,
    BAD_USER;

}
