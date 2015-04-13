package net.ethx.shuteye.util;

public class Joiner {
    public static void joinTo(final Iterable<?> values, final String separator, final StringBuilder out) {
        for (Object value : values) {
            if (value != null) {
                out.append(value);
                out.append(separator);
            }
        }

        if (out.length() >= separator.length()) {
            out.setLength(out.length() - separator.length());
        }
    }

    public static String join(final Iterable<?> values, final String separator) {
        final StringBuilder ret = new StringBuilder();
        joinTo(values, separator, ret);
        return ret.toString();
    }
}
