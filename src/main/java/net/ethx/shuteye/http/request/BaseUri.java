package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ShuteyeContext;
import net.ethx.shuteye.http.ShuteyeOption;
import net.ethx.shuteye.http.response.ResponseTransformer;

import java.net.URI;

public class BaseUri {
    private final ShuteyeContext context;
    private final String uri;

    public BaseUri(final ShuteyeContext context, final String uri) {
        this.context = context;
        this.uri = uri;
    }

    public <T> BaseUri with(final ShuteyeOption<T> option, final T value) {
        return new BaseUri(context.with(option, value), uri);
    }

    public <T> T get(final ResponseTransformer<T> transformer) {
        return get().as(transformer);
    }

    public GetRequest get() {
        return new GetRequest(context, "GET", uri);
    }

    public <T> T head(final ResponseTransformer<T> transformer) {
        return head().as(transformer);
    }

    public GetRequest head() {
        return new GetRequest(context, "HEAD", uri);
    }

    public <T> T post(final ResponseTransformer<T> transformer) {
        return post().as(transformer);
    }

    public PostRequest post() {
        return new PostRequest(context, "POST", uri);
    }

    public <T> T put(final ResponseTransformer<T> transformer) {
        return put().as(transformer);
    }

    public PostRequest put() {
        return new PostRequest(context, "PUT", uri);
    }

    public <T> T patch(final ResponseTransformer<T> transformer) {
        return patch().as(transformer);
    }

    public PostRequest patch() {
        return new PostRequest(context, "PATCH", uri);
    }

    public <T> T delete(final ResponseTransformer<T> transformer) {
        return delete().as(transformer);
    }

    public PostRequest delete() {
        return new PostRequest(context, "DELETE", uri);
    }

    public <T> T options(final ResponseTransformer<T> transformer) {
        return options().as(transformer);
    }

    public PostRequest options() {
        return new PostRequest(context, "OPTIONS", uri);
    }

    public <T> T request(final String method, final ResponseTransformer<T> transformer) {
        return request(method).as(transformer);
    }

    public PostRequest request(final String method) {
        return new PostRequest(context, method, uri);
    }

    @Override
    public String toString() {
        return uri;
    }

    public URI toURI() {
        return URI.create(uri);
    }
}
