package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.ShuteyeContext;
import net.ethx.shuteye.options.Option;

public class UriTemplateOption<T> extends Option<T> {
    public static final UriTemplateOption<Boolean> AllowExtraVariablesForVarArg = new UriTemplateOption<Boolean>("AllowExtraVariablesForVarArg", Boolean.class, Boolean.FALSE);
    public static final UriTemplateOption<Boolean> AllowMissingVariablesForVarArg = new UriTemplateOption<Boolean>("AllowMissingVariablesForVarArg", Boolean.class, Boolean.FALSE);

    public static final UriTemplateOption<Boolean> AllowExtraVariablesForMap = new UriTemplateOption<Boolean>("AllowExtraVariablesForMap", Boolean.class, Boolean.TRUE);
    public static final UriTemplateOption<Boolean> AllowMissingVariablesForMap = new UriTemplateOption<Boolean>("AllowMissingVariablesForMap", Boolean.class, Boolean.FALSE);

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
