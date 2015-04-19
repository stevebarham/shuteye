package net.ethx.shuteye.http.except;

public class TemplateException extends ShuteyeException {
    public TemplateException(final String message) {
        super(message);
    }

    public TemplateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
