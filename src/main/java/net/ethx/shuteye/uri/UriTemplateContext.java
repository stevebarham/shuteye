package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.request.BaseUri;
import net.ethx.shuteye.options.OptionSet;
import net.ethx.shuteye.util.Shadow;


public class UriTemplateContext {
    private final OptionSet<UriTemplateOption> options;

    public UriTemplateContext(final OptionSet<UriTemplateOption> options) {
        this.options = options;
    }

    public OptionSet<UriTemplateOption> options() {
        return options;
    }

    public <T> UriTemplateContext with(final UriTemplateOption<T> option, final T value) {
        return new UriTemplateContext(options.with(option, value));
    }

    @Shadow
    public BaseUri create(final String template, final Object... vars) throws IllegalStateException, IllegalArgumentException {
        return compile(template).createUri(vars);
    }

    @Shadow
    public BaseUri create(final String template, final Vars vars) throws IllegalStateException, IllegalArgumentException {
        return compile(template).createUri(vars);
    }

    @Shadow
    public String process(final String template, final Object... vars) throws IllegalStateException, IllegalArgumentException {
        return compile(template).process(vars);
    }

    @Shadow
    public String process(final String template, final Vars vars) throws IllegalStateException, IllegalArgumentException {
        return compile(template).process(vars);
    }

    @Shadow
    public UriTemplate compile(final String template) throws IllegalStateException, IllegalArgumentException {
        return new UriTemplate(this, UriTemplateParser.parse(template));
    }

    private static volatile UriTemplateContext defaultContext = new UriTemplateContext(new OptionSet<UriTemplateOption>());

    public static UriTemplateContext defaultContext(final UriTemplateContext defaultContext) {
        UriTemplateContext.defaultContext = defaultContext;
        return UriTemplateContext.defaultContext;
    }

    public static UriTemplateContext defaultContext() {
        return defaultContext;
    }
}
