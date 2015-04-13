package net.ethx.shuteye.http.request;

import java.io.IOException;
import java.net.HttpURLConnection;

public class GetRequest extends Request {
    public GetRequest(final String method, final String uri) {
        super(method, uri);
    }

    @Override
    protected void writeEntity(final HttpURLConnection connection) throws IOException {}
}
