package net.ethx.shuteye.http.request;

import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.ResponseTransformer;
import net.ethx.shuteye.util.Preconditions;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class modelling an HTTP request. You should not instantiate this class directly, but use the creation methods
 * from {@link HttpTemplate} to create it.
 * <p/>
 * Some instances of this class may be reused once created. The executing methods {@link Request#as(ResponseTransformer)} and
 * {@link Request#execute()} will result in a new HTTP request being executed.
 * <p/>
 * Adding a stream-like entity as a field to the request will result in the request not being reusable, as the stream will have
 * been drained during the first request execution.
 */
public class Request {
    private final HttpTemplate context;
    private final String method;
    private final URL url;
    private final MutableHeaders headers = new MutableHeaders();

    /**
     * Creates a new request. You should not call this method directly, but use the creation methods from {@link HttpTemplate}
     * to create it.
     *
     * @param template HttpTemplate used by this request
     * @param method   HTTP method (GET, POST, etc.) used by this request
     * @param url      Resolved URL that will be used by this request
     */
    public Request(final HttpTemplate template, final String method, final String url) {
        this.context = template;
        this.method = method;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new ShuteyeException("Could not parse provided URL '" + url + "'", e);
        }

        Preconditions.checkArgument(this.url.getProtocol().toLowerCase().startsWith("http"), "Only HTTP(S) protocol supported, found %s", url);
        header("Accept-Encoding", "gzip");
    }

    protected HttpTemplate template() {
        return context;
    }

    /**
     * Adds the specified value to the headers collection for the request.
     *
     * @param header Header name
     * @param value  Header value
     * @return This request, to support chained method calls
     */
    public Request header(final String header, final String value) {
        headers.add(header, value);
        return this;
    }

    /**
     * Executes the request via {@link #execute()}, and applies the specified {@link ResponseTransformer} to the response.
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
        return execute().as(transformer);
    }

    /**
     * Executes the request.
     *
     * @return a {@link Response} object with the result of executing this request.
     */
    public Response execute() {
        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method());
            connection.setInstanceFollowRedirects("GET".equals(method()));
            connection.setConnectTimeout(context.config().getConnectTimeoutMillis());
            connection.setReadTimeout(context.config().getReadTimeoutMillis());

            for (String header : headers.headerNames()) {
                for (String value : headers.all(header)) {
                    connection.addRequestProperty(header, value);
                }
            }

            writeEntity(connection);

            try {
                final int statusCode = connection.getResponseCode();
                final String statusText = connection.getResponseMessage();
                final Headers responseHeaders = new Headers(connection.getHeaderFields());

                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final String encoding = responseHeaders.first("Content-Encoding");

                //  todo: follow redirects
                final InputStream encoded = statusCode < 400 ? connection.getInputStream() : connection.getErrorStream();
                if (encoded != null) {
                    final InputStream source;
                    final OutputStream dest;
                    if ("gzip".equalsIgnoreCase(encoding)) {
                        source = encoded;
                        dest = out;
                    } else if ("deflate".equalsIgnoreCase(encoding)) {
                        source = new DeflaterInputStream(encoded);
                        dest = new GZIPOutputStream(out);
                    } else {
                        source = encoded;
                        dest = new GZIPOutputStream(out);
                    }
                    try {
                        Streams.copy(source, dest);
                    } finally {
                        source.close();
                        dest.close();
                    }
                }

                return new Response(statusCode, statusText, responseHeaders, out.toByteArray());
            } finally {
                connection.disconnect();
            }
        } catch (IOException ie) {
            throw new ShuteyeException(String.format("Could not execute request to %s", url), ie);
        }
    }

    protected void writeEntity(final HttpURLConnection connection) throws IOException {
    }

    /**
     * @return the resolved URL agsinst which this request will be made
     */
    public URL url() {
        return url;
    }

    /**
     * @return the HTTP method (GET, POST, etc.) that this request will use
     */
    public String method() {
        return method;
    }

    /**
     * @return The headers that will be sent alongside this request
     */
    public Headers headers() {
        return headers;
    }
}
