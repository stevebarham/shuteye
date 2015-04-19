package net.ethx.shuteye.uri;

import java.util.Collections;
import java.util.Map;

public class Vars {
    private final Map<String, ?> args;

    public Vars(final Map<String, ?> args) {
        this.args = Collections.unmodifiableMap(args);
    }

    Map<String, ?> args() {
        return args;
    }

    @Override
    public String toString() {
        return args.toString();
    }

    public static Vars wrap(final Map<String, ?> args) {
        return new Vars(args);
    }
}
