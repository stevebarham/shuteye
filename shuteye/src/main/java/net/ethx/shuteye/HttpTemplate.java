package net.ethx.shuteye;

import net.ethx.shuteye.http.request.PostingRequest;
import net.ethx.shuteye.http.request.Request;
import net.ethx.shuteye.uri.UriTemplate;
import net.ethx.shuteye.uri.UriTemplateCompiler;
import net.ethx.shuteye.uri.Vars;

public class HttpTemplate {
    private final ShuteyeConfig config;
    private final UriTemplateCompiler uriTemplateCompiler;

    public HttpTemplate() {
        this(ShuteyeConfig.defaults());
    }

    public HttpTemplate(final ShuteyeConfig config) {
        this.config = config;
        this.uriTemplateCompiler = new UriTemplateCompiler(config);
    }

    public UriTemplateCompiler uriTemplateCompiler() {
        return uriTemplateCompiler;
    }

    public ShuteyeConfig config() {
        return config;
    }

    public Request get(final String uri, final Object... vars) {
        return get(uriTemplateCompiler.compile(uri), vars);
    }

    public Request get(final UriTemplate template, final Object... vars) {
        return new Request(this, "GET", template.process(vars));
    }

    public Request get(final String uri, final Vars vars) {
        return get(uriTemplateCompiler.compile(uri), vars);
    }

    public Request get(final UriTemplate template, final Vars vars) {
        return new Request(this, "GET", template.process(vars));
    }

    public Request head(final String uri, final Object... vars) {
        return head(uriTemplateCompiler.compile(uri), vars);
    }

    public Request head(final UriTemplate template, final Object... vars) {
        return new Request(this, "HEAD", template.process(vars));
    }

    public Request head(final String uri, final Vars vars) {
        return head(uriTemplateCompiler.compile(uri), vars);
    }

    public Request head(final UriTemplate template, final Vars vars) {
        return new Request(this, "HEAD", template.process(vars));
    }

    public PostingRequest post(final String uri, final Object... vars) {
        return post(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest post(final UriTemplate template, final Object... vars) {
        return new PostingRequest(this, "POST", template.process(vars));
    }

    public PostingRequest post(final String uri, final Vars vars) {
        return post(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest post(final UriTemplate template, final Vars vars) {
        return new PostingRequest(this, "POST", template.process(vars));
    }

    public PostingRequest put(final String uri, final Object... vars) {
        return put(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest put(final UriTemplate template, final Object... vars) {
        return new PostingRequest(this, "PUT", template.process(vars));
    }

    public PostingRequest put(final String uri, final Vars vars) {
        return put(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest put(final UriTemplate template, final Vars vars) {
        return new PostingRequest(this, "PUT", template.process(vars));
    }

    public PostingRequest patch(final String uri, final Object... vars) {
        return patch(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest patch(final UriTemplate template, final Object... vars) {
        return new PostingRequest(this, "PATCH", template.process(vars));
    }

    public PostingRequest patch(final String uri, final Vars vars) {
        return patch(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest patch(final UriTemplate template, final Vars vars) {
        return new PostingRequest(this, "PATCH", template.process(vars));
    }

    public PostingRequest delete(final String uri, final Object... vars) {
        return delete(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest delete(final UriTemplate template, final Object... vars) {
        return new PostingRequest(this, "DELETE", template.process(vars));
    }

    public PostingRequest delete(final String uri, final Vars vars) {
        return delete(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest delete(final UriTemplate template, final Vars vars) {
        return new PostingRequest(this, "DELETE", template.process(vars));
    }

    public PostingRequest options(final String uri, final Object... vars) {
        return options(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest options(final UriTemplate template, final Object... vars) {
        return new PostingRequest(this, "OPTIONS", template.process(vars));
    }

    public PostingRequest options(final String uri, final Vars vars) {
        return options(uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest options(final UriTemplate template, final Vars vars) {
        return new PostingRequest(this, "OPTIONS", template.process(vars));
    }

    public PostingRequest request(final String method, final String uri, final Object... vars) {
        return request(method, uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest request(final String method, final String uri, final Vars vars) {
        return request(method, uriTemplateCompiler.compile(uri), vars);
    }

    public PostingRequest request(final String method, final UriTemplate template, final Object... vars) {
        return new PostingRequest(this, method, template.process(vars));
    }

    public PostingRequest request(final String method, final UriTemplate template, final Vars vars) {
        return new PostingRequest(this, method, template.process(vars));
    }

    /**
     * Compiles the specified template into a {@link UriTemplate}. If the template is going to be repeatedly used,
     * compiling will reduce the overhead for each call, as the template string will not need to be repeatedly
     * parsed.
     *
     * @param template {@link UriTemplate} string to compile
     * @throws net.ethx.shuteye.http.except.TemplateException if the template could not be compiled
     * @return A compiled {@link UriTemplate}.
     */
    public UriTemplate compile(final String template) {
        return uriTemplateCompiler.compile(template);
    }
}
