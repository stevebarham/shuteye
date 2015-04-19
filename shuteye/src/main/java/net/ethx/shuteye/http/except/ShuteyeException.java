package net.ethx.shuteye.http.except;

public class ShuteyeException extends RuntimeException {
    public ShuteyeException(final String message) {
        super(message);
    }

    public ShuteyeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
