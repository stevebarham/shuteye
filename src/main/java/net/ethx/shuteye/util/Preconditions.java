package net.ethx.shuteye.util;

public abstract class Preconditions {
    public static void checkArgument(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void checkState(final boolean condition, final String message, final Object... args) {
        if (!condition) {
            throw new IllegalStateException(String.format(message, args));
        }
    }
}
