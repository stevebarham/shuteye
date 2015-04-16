package net.ethx.shuteye.http;

import net.ethx.shuteye.options.Option;
import net.ethx.shuteye.util.Encodings;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ShuteyeOption<T> extends Option<T> {
    public static final ShuteyeOption<Integer> CONNECT_TIMEOUT_MILLIS = new ShuteyeOption<Integer>("CONNECT_TIMEOUT_MILLIS", Integer.class, (int) TimeUnit.SECONDS.toMillis(10));
    public static final ShuteyeOption<Integer> READ_TIMEOUT_MILLIS = new ShuteyeOption<Integer>("READ_TIMEOUT_MILLIS", Integer.class, (int) TimeUnit.MINUTES.toMillis(1));
    public static final ShuteyeOption<ContentType> DEFAULT_STRING_CONTENT_TYPE = new ShuteyeOption<ContentType>("DEFAULT_STRING_CONTENT_TYPE", ContentType.class, ContentType.TEXT_PLAIN);
    public static final ShuteyeOption<Charset> DEFAULT_STRING_ENCODING = new ShuteyeOption<Charset>("DEFAULT_STRING_ENCODING", Charset.class, Encodings.UTF8);
    public static final ShuteyeOption<ContentType> DEFAULT_STREAM_CONTENT_TYPE = new ShuteyeOption<ContentType>("DEFAULT_STREAM_CONTENT_TYPE", ContentType.class, ContentType.APPLICATION_OCTET_STREAM);

    public ShuteyeOption(final String name, final Class<T> valueClass, final T defaultValue) {
        super(name, valueClass, defaultValue);
    }
}
