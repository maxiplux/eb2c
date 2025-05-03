package app.quantun.eb2c.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles exceptions thrown by the application and returns appropriate responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ProductNotFoundException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Product Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle OrderNotFoundException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error("Order not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Order Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle EntityNotFoundException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Entity Not Found");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle MethodArgumentNotValidException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation error");
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleValidationExceptions(HttpMessageNotReadableException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        // Check if it's a missing request body error
        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            errors.put("body", "Request body is required but was not provided");
        } else {
            // For JSON parse errors and other issues
            String errorMessage = ex.getMessage();
            // Extract just the relevant part of the error for JSON parse errors
            if (errorMessage != null && errorMessage.contains("JSON parse error")) {
                int colonIndex = errorMessage.indexOf(':');
                if (colonIndex > 0 && colonIndex < errorMessage.length() - 1) {
                    errorMessage = errorMessage.substring(colonIndex + 1).trim();
                }
            }
            errors.put("message", errorMessage);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation error");
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    /**
     * Handle ConstraintViolationException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getMessage());
        problemDetail.setTitle("Constraint Violation");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle AccessDeniedException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied: " + ex.getMessage());
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Handle all other exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    /**
     * Handle IllegalStateException.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Operation");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllExceptions(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }


    /**
     * Handle UnexpectedTypeException for validation constraint mismatches.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(jakarta.validation.UnexpectedTypeException.class)
    public ProblemDetail handleUnexpectedTypeException(jakarta.validation.UnexpectedTypeException ex) {
        log.error("Validation constraint type mismatch: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        // Extract field name from error message
        String errorMessage = ex.getMessage();
        String fieldName = "unknown";
        String constraintType = "unknown";
        String fieldType = "unknown";

        // Parse the error message to extract useful information
        // Format: "HV000030: No validator could be found for constraint 'jakarta.validation.constraints.NotBlank' validating type 'java.lang.Long'. Check configuration for 'categoryId'"
        if (errorMessage != null) {
            // Extract constraint type
            int constraintStart = errorMessage.indexOf("constraint '") + 12;
            int constraintEnd = errorMessage.indexOf("'", constraintStart);
            if (constraintStart > 12 && constraintEnd > constraintStart) {
                constraintType = errorMessage.substring(constraintStart, constraintEnd);
                // Get just the simple name
                int lastDot = constraintType.lastIndexOf('.');
                if (lastDot > 0) {
                    constraintType = constraintType.substring(lastDot + 1);
                }
            }

            // Extract field type
            int typeStart = errorMessage.indexOf("type '") + 6;
            int typeEnd = errorMessage.indexOf("'", typeStart);
            if (typeStart > 6 && typeEnd > typeStart) {
                fieldType = errorMessage.substring(typeStart, typeEnd);
                // Get just the simple name
                int lastDot = fieldType.lastIndexOf('.');
                if (lastDot > 0) {
                    fieldType = fieldType.substring(lastDot + 1);
                }
            }

            // Extract field name
            int fieldStart = errorMessage.indexOf("Check configuration for '") + 24;
            int fieldEnd = errorMessage.indexOf("'", fieldStart);
            if (fieldStart > 24 && fieldEnd > fieldStart) {
                fieldName = errorMessage.substring(fieldStart, fieldEnd);
            }
        }

        // Build a clear error message
        String userFriendlyMessage = String.format(
                "Invalid validation: @%s cannot be used with %s type. Use an appropriate validator like @NotNull instead.",
                constraintType, fieldType);
        errors.put(fieldName, userFriendlyMessage);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Validation configuration error");
        problemDetail.setTitle("Invalid Validation Configuration");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleUnexpectedTypeException(MethodArgumentTypeMismatchException ex) {
        log.error("Validation constraint type mismatch: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        // Extract field name from error message
        String errorMessage = ex.getMessage();
        String fieldName = ex.getName();
        String constraintType = ex.getPropertyName() != null ? ex.getPropertyName() : "unknown";
        String fieldType = ex.getRequiredType().getSimpleName();

        // Parse the error message to extract useful information
        // Format: "HV000030: No validator could be found for constraint 'jakarta.validation.constraints.NotBlank' validating type 'java.lang.Long'. Check configuration for 'categoryId'"
        if (errorMessage != null) {
            // Extract constraint type
            int constraintStart = errorMessage.indexOf("constraint '") + 12;
            int constraintEnd = errorMessage.indexOf("'", constraintStart);
            if (constraintStart > 12 && constraintEnd > constraintStart) {
                constraintType = errorMessage.substring(constraintStart, constraintEnd);
                // Get just the simple name
                int lastDot = constraintType.lastIndexOf('.');
                if (lastDot > 0) {
                    constraintType = constraintType.substring(lastDot + 1);
                }
            }

            // Extract field type
            int typeStart = errorMessage.indexOf("type '") + 6;
            int typeEnd = errorMessage.indexOf("'", typeStart);
            if (typeStart > 6 && typeEnd > typeStart) {
                fieldType = errorMessage.substring(typeStart, typeEnd);
                // Get just the simple name
                int lastDot = fieldType.lastIndexOf('.');
                if (lastDot > 0) {
                    fieldType = fieldType.substring(lastDot + 1);
                }
            }

            // Extract field name
            int fieldStart = errorMessage.indexOf("Check configuration for '") + 24;
            int fieldEnd = errorMessage.indexOf("'", fieldStart);
            if (fieldStart > 24 && fieldEnd > fieldStart) {
                fieldName = errorMessage.substring(fieldStart, fieldEnd);
            }
        }

        // Build a clear error message
        String userFriendlyMessage = String.format(
                "Invalid validation: @%s cannot be used with %s type. Use an appropriate validator like @NotNull instead.",
                constraintType, fieldType);
        errors.put(fieldName, userFriendlyMessage);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Validation configuration error");
        problemDetail.setTitle("Invalid Validation Configuration");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

}
