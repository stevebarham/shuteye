package net.ethx.shuteye.http;

import net.ethx.shuteye.http.request.BaseUri;
import net.ethx.shuteye.http.request.GetRequest;
import net.ethx.shuteye.http.request.PostRequest;
import net.ethx.shuteye.options.OptionSet;
import net.ethx.shuteye.util.Shadow;

public class ShuteyeContext {
    private final OptionSet<ShuteyeOption> options;

    public ShuteyeContext(final OptionSet<ShuteyeOption> options) {
        this.options = options;
    }

    public OptionSet<ShuteyeOption> options() {
        return options;
    }

    public <T> ShuteyeContext with(final ShuteyeOption<T> option, final T value) {
        return new ShuteyeContext(options.with(option, value));
    }

    @Shadow
    public GetRequest get(final String uri) {
        return new BaseUri(this, uri).get();
    }

    @Shadow
    public GetRequest head(final String uri) {
        return new BaseUri(this, uri).head();
    }

    @Shadow
    public PostRequest post(final String uri) {
        return new BaseUri(this, uri).post();
    }

    @Shadow
    public PostRequest put(final String uri) {
        return new BaseUri(this, uri).put();
    }

    @Shadow
    public PostRequest patch(final String uri) {
        return new BaseUri(this, uri).patch();
    }

    @Shadow
    public PostRequest delete(final String uri) {
        return new BaseUri(this, uri).delete();
    }

    @Shadow
    public PostRequest options(final String uri) {
        return new BaseUri(this, uri).options();
    }

    @Shadow
    public PostRequest request(final String method, final String uri) {
        return new BaseUri(this, uri).request(method);
    }

    private static volatile ShuteyeContext defaultContext = new ShuteyeContext(new OptionSet<ShuteyeOption>());

    public static ShuteyeContext defaultContext(final ShuteyeContext defaultContext) {
        ShuteyeContext.defaultContext = defaultContext;
        return ShuteyeContext.defaultContext;
    }

    public static ShuteyeContext defaultContext() {
        return defaultContext;
    }
}
