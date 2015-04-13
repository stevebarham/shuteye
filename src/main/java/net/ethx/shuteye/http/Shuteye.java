package net.ethx.shuteye.http;

import net.ethx.shuteye.http.request.GetRequest;
import net.ethx.shuteye.http.request.PostRequest;
import net.ethx.shuteye.http.request.RequestBuilder;

public abstract class Shuteye {
    public static GetRequest get(final String uri) {
        return new RequestBuilder(uri).get();
    }

    public static GetRequest head(final String uri) {
        return new RequestBuilder(uri).head();
    }

    public static PostRequest post(final String uri) {
        return new RequestBuilder(uri).post();
    }

    public static PostRequest put(final String uri) {
        return new RequestBuilder(uri).put();
    }

    public static PostRequest patch(final String uri) {
        return new RequestBuilder(uri).patch();
    }

    public static PostRequest delete(final String uri) {
        return new RequestBuilder(uri).delete();
    }

    public static PostRequest options(final String uri) {
        return new RequestBuilder(uri).options();
    }

    public static PostRequest request(final String method, final String uri) {
        return new RequestBuilder(uri).request(method);
    }
}
