package net.ethx.shuteye.util;

public abstract class Preconditions {
    public static <T> T checkNotNull(final T instance, final String message, final Object... args) {
        if (instance == null) {
            throw new NullPointerException(String.format(message, args));
        }
        return instance;
    }

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
