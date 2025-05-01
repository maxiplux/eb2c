package app.quantun.eb2c.exception;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Uses RFC 7807 Problem Details for HTTP APIs.
 */
@RestControllerAdvice
@Tag(name = "Error Handling", description = "Global error handling for the API")
public class GlobalExceptionHandler {

    @Operation(summary = "Handle entity not found exceptions", 
              description = "Processes exceptions when requested resources are not found")
    @ApiResponse(responseCode = "404", description = "Resource not found", 
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.b2bcommerce.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @Operation(summary = "Handle validation exceptions", 
              description = "Processes exceptions when request data fails validation")
    @ApiResponse(responseCode = "400", description = "Invalid input data", 
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://api.b2bcommerce.com/errors/validation"));

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errors", validationErrors);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @Operation(summary = "Handle constraint violation exceptions", 
              description = "Processes exceptions when data constraints are violated")
    @ApiResponse(responseCode = "400", description = "Constraint violation", 
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Constraint Violation");
        problemDetail.setType(URI.create("https://api.b2bcommerce.com/errors/constraint-violation"));

        Map<String, String> constraintViolations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            constraintViolations.put(propertyPath, message);
        });

        problemDetail.setProperty("violations", constraintViolations);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @Operation(summary = "Handle general exceptions", 
              description = "Processes all other unexpected exceptions")
    @ApiResponse(responseCode = "500", description = "Internal server error", 
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.b2bcommerce.com/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("exception", ex.getClass().getSimpleName());

        // In production, you might want to hide the actual exception message for security reasons
        // and just log it instead
        problemDetail.setProperty("message", ex.getMessage());

        return problemDetail;
    }
}