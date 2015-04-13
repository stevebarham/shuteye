package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.request.GetRequest;
import net.ethx.shuteye.http.request.PostRequest;

public class RequestBuilder {
    private final String uri;

    public RequestBuilder(final String uri) {
        this.uri = uri;
    }

    public GetRequest get() {
        return new GetRequest("GET", uri);
    }

    public GetRequest head() {
        return new GetRequest("HEAD", uri);
    }

    public PostRequest post() {
        return new PostRequest("POST", uri);
    }

    public PostRequest put() {
        return new PostRequest("PUT", uri);
    }

    public PostRequest patch() {
        return new PostRequest("PATCH", uri);
    }

    public PostRequest delete() {
        return new PostRequest("DELETE", uri);
    }

    public PostRequest options() {
        return new PostRequest("OPTIONS", uri);
    }

    public PostRequest request(final String method) {
        return new PostRequest(method, uri);
    }

    @Override
    public String toString() {
        return uri;
    }
}
