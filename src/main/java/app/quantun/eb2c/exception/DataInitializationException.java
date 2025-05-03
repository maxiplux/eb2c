package app.quantun.eb2c.exception;

public class DataInitializationException extends RuntimeException {
    public DataInitializationException(String failedToInitializeSampleData, Exception e) {
        super(failedToInitializeSampleData, e);
    }
}
