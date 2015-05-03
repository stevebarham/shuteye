package net.ethx.shuteye.http.request;

import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.response.BufferedResponse;
import net.ethx.shuteye.http.response.TypedResponse;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.http.response.trans.DefaultTransformer;
import net.ethx.shuteye.http.response.trans.Transformer;
import net.ethx.shuteye.http.response.trans.Transformers;
import net.ethx.shuteye.util.Preconditions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class modelling an HTTP request. You should not instantiate this class directly, but use the creation methods
 * from {@link HttpTemplate} to create it.
 * <p/>
 * Some instances of this class may be reused once created. The executing methods {@link Request#as(Transformer)} and
 * {@link Request#execute(Transformer)}} will result in a new HTTP request being executed.
 * <p/>
 * Adding a stream-like entity as a field to the request will result in the request not being reusable, as the stream will have
 * been drained during the first request execution.
 */
public class Request {
    private final HttpTemplate template;
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
        this.template = template;
        this.method = method;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new ShuteyeException("Could not parse provided URL '" + url + "'", e);
        }

        Preconditions.checkArgument(this.url.getProtocol().toLowerCase().startsWith("http"), "Only HTTP(S) protocol supported, found %s", url);

        final StringBuilder acceptsBuilder = new StringBuilder();
        final java.util.List<Codec> codecs = template.config().getCodecs();
        for (int i = 0; i < codecs.size(); i++) {
            final Codec codec = codecs.get(i);
            acceptsBuilder.append(codec.name());
            if (i != codecs.size() - 1) {
                acceptsBuilder.append(", ");
            }
        }
        header("Accept-Encoding", acceptsBuilder.toString());
    }

    /**
     * @return The {@link HttpTemplate} which produced this request
     */
    protected HttpTemplate template() {
        return template;
    }

    /**
     * Adds the specified typedResponse to the headers collection for the request.
     *
     * @param header Header name
     * @param value  Header typedResponse
     * @return This request, to support chained method calls
     */
    public Request header(final String header, final String value) {
        headers.add(header, value);
        return this;
    }

    /**
     * Executes the request, and applies the specified {@link Transformer} to the response without buffering.
     * <p/>
     * This method will <em>not</em> check for the error state of the response, prior to invoking the transformer. If you
     * want to receive a standard exception for error responses, and only apply the transformer for success responses, then
     * you should either extend {@link DefaultTransformer} or wrap your transformer via
     * {@link DefaultTransformer#onSuccess(Transformer)}.
     * <p/>
     * This method does not buffer any response received from the server; as such it is appropriate for use in transforming
     * large responses via streaming.
     *
     * @param transformer Transformer to receive the response
     * @param <T>         Type of object returned by the transformer
     * @return The transformed object
     * @see Request#execute(Transformer)
     */
    public <T> T as(final Transformer<T> transformer) {
        return new RequestProcessor<T>(this, transformer).execute();
    }

    /**
     * Executes the request, and applies the specified {@link Transformer} to the response without buffering.
     * <p/>
     * This method will <em>not</em> check for the error state of the response, prior to invoking the transformer. If you
     * want to receive a standard exception for error responses, and only apply the transformer for success responses, then
     * you should either extend {@link DefaultTransformer} or wrap your transformer via
     * {@link DefaultTransformer#onSuccess(Transformer)}.
     * <p/>
     * This method does not buffer any response received from the server; as such it is appropriate for use in transforming
     * large responses via streaming.
     *
     * @param transformer Transformer to receive the response
     * @param <T>         Type of object returned by the transformer
     * @return A {@link TypedResponse} holding the transformed response
     * @see Request#as(Transformer)
     */
    public <T> TypedResponse<T> execute(final Transformer<T> transformer) {
        return as(Transformers.typedResponse(transformer));
    }

    /**
     * Executes the request, and fetches the response into a {@link BufferedResponse}. This method is appropriate when the
     * data from the response may be reused. Contrast this with {@link #as(Transformer)} or {@link #execute(Transformer)}
     * which do not buffer the response.
     * @return A {@link BufferedResponse} holding the fetched data
     */
    public BufferedResponse fetch() {
        return as(Transformers.buffered(template.config().getCodecs(), template.config().getBufferCodec()));
    }

    /**
     * Executes the request and returns the text typedResponse of the response.
     *
     * @see Transformers#string()
     */
    public String textValue() {
        return as(Transformers.string());
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
