package app.quantun.eb2c.exception;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when attempting to sort by an invalid field
 */
public class InvalidSortFieldException extends RuntimeException {
    private final List<String> validFields;

    public InvalidSortFieldException(String message, List<String> validFields) {
        super(message);
        this.validFields = validFields;
    }

    public InvalidSortFieldException(String message) {
        super(message);
        this.validFields = Collections.emptyList();
    }

    public InvalidSortFieldException(String message, Throwable cause) {
        super(message, cause);
        this.validFields = Collections.emptyList();
    }

    public List<String> getValidFields() {
        return validFields;
    }
}
