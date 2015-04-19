package net.ethx.shuteye.uri;

import net.ethx.shuteye.ShuteyeConfig;


public class UriTemplateCompiler {
    private final ShuteyeConfig config;

    public UriTemplateCompiler() {
        this(ShuteyeConfig.defaults());
    }

    public UriTemplateCompiler(final ShuteyeConfig config) {
        this.config = config;
    }

    public ShuteyeConfig config() {
        return config;
    }

    public String process(final String template, final Object... vars) throws IllegalStateException, IllegalArgumentException {
        return compile(template).expand(vars);
    }

    public String process(final String template, final Vars vars) throws IllegalStateException, IllegalArgumentException {
        return compile(template).expand(vars);
    }

    public UriTemplate compile(final String template) throws IllegalStateException, IllegalArgumentException {
        return new UriTemplate(this, UriTemplateParser.parse(template));
    }
}
