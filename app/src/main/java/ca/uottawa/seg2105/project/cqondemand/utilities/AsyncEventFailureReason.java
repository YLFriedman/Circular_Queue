package ca.uottawa.seg2105.project.cqondemand.utilities;

public enum AsyncEventFailureReason {

    // Database Reasons
    DATABASE_ERROR,
    DOES_NOT_EXIST,
    ALREADY_EXISTS,
    INVALID_DATA,

    // User Specific Reasons
    PASSWORD_MISMATCH;

}
