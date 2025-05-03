package app.quantun.eb2c.exception;

/**
 * Exception thrown when a product is not found.
 * This exception is used to indicate that a requested product does not exist.
 */
public class ProductNotFoundException extends RuntimeException {
    /**
     * Constructs a new ProductNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}