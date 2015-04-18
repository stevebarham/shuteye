package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.ShuteyeContext;
import net.ethx.shuteye.options.Option;

public class UriTemplateOption<T> extends Option<T> {
    public static final UriTemplateOption<Boolean> ALLOW_EXTRA_VARS_IN_VARARG = new UriTemplateOption<Boolean>("ALLOW_EXTRA_VARS_IN_VARARG", Boolean.class, Boolean.FALSE);
    public static final UriTemplateOption<Boolean> ALLOW_MISSING_VARS_IN_VARARG = new UriTemplateOption<Boolean>("ALLOW_MISSING_VARS_IN_VARARG", Boolean.class, Boolean.FALSE);

    public static final UriTemplateOption<Boolean> ALLOW_EXTRA_VARS_IN_MAP = new UriTemplateOption<Boolean>("ALLOW_EXTRA_VARS_IN_MAP", Boolean.class, Boolean.TRUE);
    public static final UriTemplateOption<Boolean> ALLOW_MISSING_VARS_IN_MAP = new UriTemplateOption<Boolean>("ALLOW_MISSING_VARS_IN_MAP", Boolean.class, Boolean.FALSE);

    public static final UriTemplateOption<ShuteyeContext> DefaultShuteyeContext = new UriTemplateOption<ShuteyeContext>("DefaultShuteyeContext", ShuteyeContext.class, new OptionSupplier<ShuteyeContext>() {
        @Override
        public ShuteyeContext get() {
            return ShuteyeContext.defaultContext();
        }
    });

    public UriTemplateOption(final String name, final Class<T> valueClass, final OptionSupplier<T> defaultValueSupplier) {
        super(name, valueClass, defaultValueSupplier);
    }

    public UriTemplateOption(final String name, final Class<T> clazz, final T defaultValue) {
        super(name, clazz, defaultValue);
    }
}
