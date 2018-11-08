package ca.uottawa.seg2105.project.cqondemand;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String errorMessage) {
        super(errorMessage);
    }
}
