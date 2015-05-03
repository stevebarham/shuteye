package net.ethx.shuteye.http.response;

import net.ethx.shuteye.http.Headers;

/**
 * Class modelling the basic components of an HTTP response.
 */
public class Response {
    private final int statusCode;
    private final String statusText;
    private final Headers headers;

    public Response(final int statusCode, final String statusText, final Headers headers) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
    }

    /**
     * @return true if the response code represents an error (>= 400)
     */
    public boolean isError() {
        return statusCode >= 400;
    }

    /**
     * @return The status code returned from the server
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * @return The status text returned from the server
     */
    public String statusText() {
        return statusText;
    }

    /**
     * @return Headers received from the server
     */
    public Headers headers() {
        return headers;
    }
}
