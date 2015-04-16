package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ShuteyeContext;

import java.io.IOException;
import java.net.HttpURLConnection;

public class GetRequest extends Request {
    public GetRequest(final ShuteyeContext context, final String method, final String uri) {
        super(context, method, uri);
    }

    @Override
    protected void writeEntity(final HttpURLConnection connection) throws IOException {}
}
