package net.ethx.shuteye.http.response.trans;

import net.ethx.shuteye.http.response.BufferedResponse;
import net.ethx.shuteye.http.response.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface representing a transformer from a {@link Response} to some T. This can be applied via
 * {@link net.ethx.shuteye.http.request.Request#as(Transformer)}, or via {@link Response#as(Transformer)}
 * to extract a T from the response.
 * <p/>
 * If your transformer should only operate on successful responses, then a useful default implementation can be found in
 * {@link DefaultTransformer}, which will throw an exception for error responses, and apply the transformer for success
 * responses.
 *
 * @param <T> Type of response
 * @see BufferedResponse#as(Transformer)
 * @see net.ethx.shuteye.http.request.Request#as(Transformer)
 * @see DefaultTransformer#onSuccess(Transformer)
 */
public interface Transformer<T> {
    T transform(final Response response, final InputStream stream) throws IOException;
}
