package net.ethx.shuteye.http.response.trans;

import net.ethx.shuteye.http.except.ResponseException;
import net.ethx.shuteye.http.response.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Standard base class for response transformers which applies the transformation function only when a successful response
 * (as indicated by {@link Response#isError()}) has been received.
 *
 * @param <T> Type of object returned by the transformer
 * @see ResponseException
 * @see Response#isError()
 */
public abstract class DefaultTransformer<T> implements Transformer<T> {
    @Override
    public T transform(final Response response, final InputStream stream) throws IOException, ResponseException {
        if (response.statusCode() > 400) {
            return handleFailure(response, stream);
        }
        return handle(response, stream);
    }

    /**
     * Invoked when a successful response has been received.
     * @param response Successful response
     * @return The transformed object
     * @throws IOException
     */
    protected abstract T handle(final Response response, final InputStream stream) throws IOException;

    /**
     * Invoked when an error response has been received. Default implementation throws a {@link ResponseException}
     *
     * @param response Error response
     * @return exception
     * @throws IOException
     * @throws ResponseException
     */
    protected T handleFailure(final Response response, final InputStream stream) throws IOException, ResponseException {
        throw new ResponseException(response);
    }

    /**
     * Decorates the specified transformer with the semantics of {@link DefaultTransformer}.
     *
     * @param transformer Transformer to call for successful responses
     * @param <T>         Type of object returned by the transformer
     * @return A {@link DefaultTransformer} which will apply the specified transformer when
     * a successful response is received.
     */
    public static <T> DefaultTransformer<T> onSuccess(final Transformer<T> transformer) {
        return new DefaultTransformer<T>() {
            @Override
            protected T handle(final Response response, final InputStream stream) throws IOException {
                return transformer.transform(response, stream);
            }
        };
    }
}
