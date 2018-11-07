package ca.uottawa.seg2105.project.cqondemand;

public enum DbEventFailureReason {

    // General Errors
    DATABASE_ERROR,
    DOES_NOT_EXIST,
    ALREADY_EXISTS,
    INVALID_DATA,

    // User Specific Errors
    PASSWORD_MISMATCH;

}
