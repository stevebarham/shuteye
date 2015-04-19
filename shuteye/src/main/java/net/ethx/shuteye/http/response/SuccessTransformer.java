package net.ethx.shuteye.http.response;

import net.ethx.shuteye.http.except.ResponseException;

import java.io.IOException;

/**
 * Standard base class for response transformers which applies the transformation function
 * only when a successful response has been received.
 *
 * @param <T> Type of object returned by the transformer
 * @see ResponseException
 * @see Response#isError()
 */
public abstract class SuccessTransformer<T> implements ResponseTransformer<T> {
    @Override
    public T transform(final Response response) throws IOException, ResponseException {
        if (response.statusCode() > 400) {
            return handleFailure(response);
        }
        return handle(response);
    }

    /**
     * Invoked when a successful response has been received.
     * @param response Successful response
     * @return The transformed object
     * @throws IOException
     */
    protected abstract T handle(final Response response) throws IOException;

    /**
     * Invoked when an error response has been received. Default implementation throws a {@link ResponseException}
     *
     * @param response Error response
     * @return exception
     * @throws IOException
     * @throws ResponseException
     */
    protected T handleFailure(final Response response) throws IOException, ResponseException {
        throw new ResponseException(response);
    }

    /**
     * Decorates the specified transformer with the semantics of {@link SuccessTransformer}.
     *
     * @param transformer Transformer to call for successful responses
     * @param <T>         Type of object returned by the transformer
     * @return A {@link SuccessTransformer} which will apply the specified transformer when
     * a successful response is received.
     */
    public static <T> SuccessTransformer<T> onSuccess(final ResponseTransformer<T> transformer) {
        return new SuccessTransformer<T>() {
            @Override
            protected T handle(final Response response) throws IOException {
                return transformer.transform(response);
            }
        };
    }
}
