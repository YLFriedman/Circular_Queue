package ca.uottawa.seg2105.project.cqondemand.utilities;

/**
 * The exception <b>InvalidDataException</b> is used detect if an error was caused
 * by the use of invalid data. Data validity is governed by the FieldValidation class.
 *
 * Course: SEG 2105 B
 * Final Project
 * Group: CircularQueue
 *
 * @author CircularQueue
 */
public class InvalidDataException extends RuntimeException {

    /**
     * The constructor of an InvalidDataException.
     * @param errorMessage the error message associated with the exceptions
     */
    public InvalidDataException(String errorMessage) {
        super(errorMessage);
    }

}
