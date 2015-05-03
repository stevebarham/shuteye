package net.ethx.shuteye.http.response;

import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.http.response.codec.IdentityCodec;
import net.ethx.shuteye.http.response.trans.DefaultTransformer;
import net.ethx.shuteye.http.response.trans.Transformer;
import net.ethx.shuteye.http.response.trans.Transformers;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * A {@link Response}, with an associated body. This class is appropriate where you wish to repeatedly use the response
 * from the server.
 * <p/>
 * The body will be compressed, to reduce the overhead of buffering the body. The compression {@link Codec} which is used
 * is configured by {@link net.ethx.shuteye.ShuteyeConfig#setBufferCodec(Codec)}, and defaults to {@link net.ethx.shuteye.ShuteyeConfig.Defaults#BUFFER_CODEC}.
 */
public class BufferedResponse extends Response {
    private final Codec codec;
    private final byte[] body;

    public BufferedResponse(final Response response, final byte[] body) {
        this(response, new IdentityCodec(), body);
    }

    public BufferedResponse(final Response response, final Codec codec, final byte[] body) {
        super(response.statusCode(), response.statusText(), response.headers());
        this.codec = codec;
        this.body = body == null ? null : Arrays.copyOf(body, body.length);
    }

    /*
     * @return true if any data has been extracted from the HTTP response
     */
    public boolean hasData() {
        return body != null;
    }

    /**
     * @return The result of applying {@link Transformers#string()} to this response.
     */
    public String textValue() {
        return as(Transformers.string());
    }

    /**
     * Applies the specified transformer to the response, and returns the output typedResponse.
     * <p/>
     * This method will <em>not</em> check for the error state of the response, prior to invoking the transformer. If you
     * want to receive a standard exception for error responses, and only apply the transformer for success responses, then
     * you should either extend {@link DefaultTransformer} or wrap your transformer via
     * {@link DefaultTransformer#onSuccess(Transformer)}.
     *
     * @param transformer Transformer to receive the response
     * @param <T>         Type of object returned by the transformer
     * @return The transformed object
     */
    public <T> T as(final Transformer<T> transformer) {
        try {
            return transformer.transform(this, inputStream());
        } catch (IOException e) {
            throw new ShuteyeException(String.format("Could not convert response using %s", transformer), e);
        }
    }

    /**
     * @return A new stream over the response. The stream is automatically decoded .
     * @throws IOException          If the stream could not be created
     * @throws NullPointerException If no data was extracted from the response
     */
    public InputStream inputStream() throws IOException {
        return body == null ? Streams.empty() : codec.decode(new ByteArrayInputStream(body));
    }
}
