package net.ethx.shuteye.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public abstract class Encodings {
    public static final Charset UTF8;
    public static final Charset ISO8859;
    static {
        try {
            UTF8 = Charset.forName("UTF8");
            ISO8859 = Charset.forName("ISO-8859-1");
        } catch (UnsupportedCharsetException e) {
            throw new IllegalStateException(e);
        }
    }
}
