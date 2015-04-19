package net.ethx.shuteye.options;

import java.util.Map;

public class Option<T> {
    private final String name;
    private final Class<T> valueClass;
    private final OptionSupplier<T> defaultValueSupplier;

    public Option(final String name, final Class<T> valueClass, final T defaultValue) {
        this(name, valueClass, constant(defaultValue));
    }

    public Option(final String name, final Class<T> valueClass, final OptionSupplier<T> defaultValueSupplier) {
        this.name = name;
        this.valueClass = valueClass;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public String name() {
        return name;
    }

    public T defaultValue() {
        return defaultValueSupplier.get();
    }

    public Class<T> valueClass() {
        return valueClass;
    }

    @SuppressWarnings("unchecked")
    public T get(final Map<Option, Object> options) {
        final T configured = valueClass().cast(options.get(this));
        return configured == null ? defaultValue() : configured;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "." + name();
    }

    protected static <T> OptionSupplier<T> constant(final T constant) {
        return new OptionSupplier<T>() {
            @Override
            public T get() {
                return constant;
            }
        };
    }

    protected static interface OptionSupplier<T> {
        T get();
    }
}
