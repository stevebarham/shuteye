package net.ethx.shuteye.http.except;

import net.ethx.shuteye.http.response.Response;

public class ResponseException extends ShuteyeException {
    private final Response response;

    public ResponseException(final Response response) {
        super(String.format("%s - %s", response.statusCode(), response.statusText()));
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
