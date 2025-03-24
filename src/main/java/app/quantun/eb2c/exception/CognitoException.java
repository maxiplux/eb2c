package app.quantun.eb2c.exception;

public class CognitoException extends RuntimeException {
    public CognitoException(String message) {
        super(message);
    }

    public CognitoException(String message, Throwable cause) {
        super(message, cause);
    }
}
