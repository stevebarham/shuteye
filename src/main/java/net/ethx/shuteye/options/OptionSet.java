package net.ethx.shuteye.options;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OptionSet<T> {
    private final Map<T, Object> options = new HashMap<T, Object>();

    public OptionSet() {
        this(Collections.<T, Object>emptyMap());
    }

    public OptionSet(final Map<T, Object> options) {
        this.options.putAll(options);
    }

    protected Object get(final T option) {
        return options.get(option);
    }

    public <V> OptionSet<T> with(final T option, final V value) {
        final Map<T, Object> next = new HashMap<T, Object>(options);
        next.put(option, value);
        return new OptionSet<T>(next);
    }

    @Override
    public String toString() {
        return options.toString();
    }
}

