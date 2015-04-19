package net.ethx.shuteye.http.response;

import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.request.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Class modelling an HTTP response. This class buffers the data returned by the server in a gzipped byte array. This means
 * that methods like {@link #textValue()} and {@link #stream()} may be called repeatedly.
 */
public class Response {
    private final int statusCode;
    private final String statusText;
    private final Headers headers;
    private final byte[] compressedBody;

    /**
     * Creates a new request. You should not call this method directly, but use the results of {@link Request#execute()}
     * to generate it. This constructor is exposed to support unit-testing of {@link ResponseTransformer} implementations.
     *
     * @param statusCode     Status code received from the server
     * @param statusText     Status text received from the server
     * @param headers        Headers parsed from the response
     * @param compressedBody Body of the response, gzipped.
     */
    public Response(final int statusCode, final String statusText, final Headers headers, final byte[] compressedBody) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.compressedBody = Arrays.copyOf(compressedBody, compressedBody.length);
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

    /**
     * @return A new stream over the response. This stream does not need to be decompressed when the server returns deflate
     * or gzip encoded data, as these are handled transparently.
     * @throws IOException If the stream could not be created
     */
    public InputStream stream() throws IOException {
        return new GZIPInputStream(new ByteArrayInputStream(compressedBody));
    }

    /**
     * @return The result of applying {@link Transformers#string()} to this response.
     */
    public String textValue() {
        return as(Transformers.string());
    }

    /**
     * Applies the specified transformer to the response, and returns the output value.
     * <p/>
     * This method will <em>not</em> check for the error state of the response, prior to invoking the transformer. If you
     * want to receive a standard exception for error responses, and only apply the transformer for success responses, then
     * you should either extend {@link net.ethx.shuteye.http.response.SuccessTransformer} or wrap your transformer via
     * {@link net.ethx.shuteye.http.response.SuccessTransformer#onSuccess(ResponseTransformer)}.
     *
     * @param transformer Transformer to receive the response
     * @param <T>         Type of object returned by the transformer
     * @return The transformed object
     */
    public <T> T as(final ResponseTransformer<T> transformer) {
        try {
            return transformer.transform(this);
        } catch (IOException e) {
            throw new ShuteyeException(String.format("Could not convert response using %s", transformer), e);
        }
    }
}
