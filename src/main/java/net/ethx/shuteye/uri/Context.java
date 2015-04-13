package net.ethx.shuteye.uri;

import java.util.Collections;
import java.util.Map;

class Context {
    private final Map<String, ?> args;

    public Context(final Map<String, ?> args) {
        this.args = Collections.unmodifiableMap(args);
    }

    public Map<String, ?> args() {
        return args;
    }
}
