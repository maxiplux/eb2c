package app.quantun.eb2c.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 *
 * This class handles various exceptions that may occur during the execution of the application.
 * It provides standardized error responses using ProblemDetail.
 * The chosen exception handling strategy ensures that the application returns meaningful error messages
 * and appropriate HTTP status codes for different types of errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions.
     *
     * This method handles MethodArgumentNotValidException, which occurs when validation on an argument
     * annotated with @Valid fails. It returns a ProblemDetail object with a BAD_REQUEST status and
     * includes field-specific validation errors.
     *
     * @param ex the MethodArgumentNotValidException
     * @return ProblemDetail object with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        // Explicitly set title and detail
        problemDetail.setTitle("Validation Error");
        problemDetail.setDetail("Validation failed");
        problemDetail.setProperty("timestamp", Instant.now());

        // Optional: Add field-specific validation errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        problemDetail.setProperty("errors", fieldErrors);

        return problemDetail;
    }

    /**
     * Handles entity not found exceptions.
     *
     * This method handles EntityNotFoundException, which occurs when an entity is not found in the database.
     * It returns a ProblemDetail object with a NOT_FOUND status and includes the exception message.
     *
     * @param exception the EntityNotFoundException
     * @param webRequest the ServletWebRequest
     * @return ProblemDetail object with resource not found error details
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException exception, ServletWebRequest webRequest) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, exception.getMessage());

        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        return problemDetail;
    }

    /**
     * Handles global exceptions.
     *
     * This method handles all other exceptions that are not specifically handled by other methods.
     * It returns a ProblemDetail object with an INTERNAL_SERVER_ERROR status and a generic error message.
     *
     * @param exception the Exception
     * @param webRequest the ServletWebRequest
     * @return ProblemDetail object with internal server error details
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception exception, ServletWebRequest webRequest) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        return problemDetail;
    }
}
