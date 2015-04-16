package net.ethx.shuteye.http.response;

import net.ethx.shuteye.http.except.ResponseException;

import java.io.IOException;

public abstract class BaseResponseTransformer<T> implements ResponseTransformer<T> {
    @Override
    public T transform(final Response response) throws IOException, ResponseException {
        if (response.statusCode() > 400) {
            return handleFailure(response);
        }
        return handle(response);
    }

    protected abstract T handle(final Response response) throws IOException;

    protected T handleFailure(final Response response) throws IOException {
        throw new ResponseException(response);
    }
}
