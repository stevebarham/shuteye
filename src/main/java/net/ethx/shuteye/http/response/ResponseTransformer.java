package net.ethx.shuteye.http.response;

import java.io.IOException;

public interface ResponseTransformer<T> {
    public T transform(final Response response) throws IOException;
}
