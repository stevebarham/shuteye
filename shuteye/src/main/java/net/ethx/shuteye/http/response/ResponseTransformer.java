package net.ethx.shuteye.http.response;

import java.io.IOException;

/**
 * Interface representing a transformer from a {@link Response} to some T. This can be applied via
 * {@link net.ethx.shuteye.http.request.Request#as(ResponseTransformer)}, or via {@link Response#as(ResponseTransformer)}
 * to extract a T from the response.
 * <p/>
 * If your transformer should only operate on successful responses, then a useful default implementation can be found in
 * {@link SuccessTransformer}, which will throw an exception for error responses, and apply the transformer for success
 * responses.
 *
 * @param <T> Type of response
 * @see Response#as(ResponseTransformer)
 * @see net.ethx.shuteye.http.request.Request#as(ResponseTransformer)
 * @see SuccessTransformer#onSuccess(ResponseTransformer)
 */
public interface ResponseTransformer<T> {
    T transform(final Response response) throws IOException;
}
