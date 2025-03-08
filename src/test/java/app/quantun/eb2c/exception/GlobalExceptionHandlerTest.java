package app.quantun.eb2c.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final ServletWebRequest webRequest = new ServletWebRequest(request);

    @Test
    void handleEntityNotFoundException_ShouldReturnProblemDetail() {
        // Arrange
        String exceptionMessage = "Organization not found with id: 1";
        EntityNotFoundException exception = new EntityNotFoundException(exceptionMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleEntityNotFoundException(exception, webRequest);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Resource Not Found", problemDetail.getTitle());
        assertEquals(exceptionMessage, problemDetail.getDetail());
        assertNotNull(problemDetail.getDetail());
    }

    @Test
    void handleGlobalException_ShouldReturnProblemDetail() {
        // Arrange
        String exceptionMessage = "Test exception";
        Exception exception = new RuntimeException(exceptionMessage);

        // Act
        ProblemDetail problemDetail = exceptionHandler.handleGlobalException(exception, webRequest);

        // Assert
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals("Internal Server Error", problemDetail.getTitle());
        assertEquals("An unexpected error occurred", problemDetail.getDetail());

    }
}