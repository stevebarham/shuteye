package net.ethx.shuteye.http;

import net.ethx.shuteye.http.request.GetRequest;
import net.ethx.shuteye.http.request.PostRequest;
import net.ethx.shuteye.http.request.BaseUri;
import net.ethx.shuteye.util.Shadows;

@Shadows(BaseUri.class)
public abstract class Shuteye {
    public static GetRequest get(final String uri) {
        return ShuteyeContext.defaultContext().get(uri);
    }

    public static GetRequest head(final String uri) {
        return ShuteyeContext.defaultContext().head(uri);
    }

    public static PostRequest post(final String uri) {
        return ShuteyeContext.defaultContext().post(uri);
    }

    public static PostRequest put(final String uri) {
        return ShuteyeContext.defaultContext().put(uri);
    }

    public static PostRequest patch(final String uri) {
        return ShuteyeContext.defaultContext().patch(uri);
    }

    public static PostRequest delete(final String uri) {
        return ShuteyeContext.defaultContext().delete(uri);
    }

    public static PostRequest options(final String uri) {
        return ShuteyeContext.defaultContext().options(uri);
    }

    public static PostRequest request(final String method, final String uri) {
        return ShuteyeContext.defaultContext().request(method, uri);
    }
}
