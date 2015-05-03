package net.ethx.shuteye;

import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.http.response.BufferedResponse;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.http.response.codec.DeflateCodec;
import net.ethx.shuteye.http.response.codec.GzipCodec;
import net.ethx.shuteye.util.Encodings;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.ethx.shuteye.util.Preconditions.checkNotNull;

public class ShuteyeConfig {
    public abstract static class Defaults {
        public static final int CONNECT_TIMEOUT_MILLIS = 10 * 1000;
        public static final int READ_TIMEOUT_MILLIS = 60 * 1000;
        public static final ContentType DEFAULT_STRING_CONTENT_TYPE = ContentType.TEXT_PLAIN;
        public static final Charset DEFAULT_STRING_ENCODING = Encodings.UTF8;
        public static final ContentType DEFAULT_STREAM_CONTENT_TYPE = ContentType.APPLICATION_OCTET_STREAM;
        public static final boolean ALLOW_EXTRA_VARS_IN_VARARG = false;
        public static final boolean ALLOW_MISSING_VARS_IN_VARARG = false;
        public static final boolean ALLOW_EXTRA_VARS_IN_MAP = true;
        public static final boolean ALLOW_MISSING_VARS_IN_MAP = false;
        public static final List<Codec> CODECS = Collections.unmodifiableList(Arrays.asList(new GzipCodec(), new DeflateCodec()));
        public static final Codec BUFFER_CODEC = new GzipCodec();
    }

    private int connectTimeoutMillis = Defaults.CONNECT_TIMEOUT_MILLIS;
    private int readTimeoutMillis = Defaults.READ_TIMEOUT_MILLIS;
    private ContentType defaultStringContentType = Defaults.DEFAULT_STRING_CONTENT_TYPE;
    private Charset defaultStringEncoding = Defaults.DEFAULT_STRING_ENCODING;
    private ContentType defaultStreamContentType = Defaults.DEFAULT_STREAM_CONTENT_TYPE;

    private boolean allowExtraVarsInVararg = Defaults.ALLOW_EXTRA_VARS_IN_VARARG;
    private boolean allowMissingVarsInVararg = Defaults.ALLOW_MISSING_VARS_IN_VARARG;
    private boolean allowExtraVarsInMap = Defaults.ALLOW_EXTRA_VARS_IN_MAP;
    private boolean allowMissingVarsInMap = Defaults.ALLOW_MISSING_VARS_IN_MAP;

    private List<Codec> codecs = Defaults.CODECS;
    private Codec bufferCodec = Defaults.BUFFER_CODEC;

    /**
     * Gets the connect timeout in milliseconds which will be passed to {@link java.net.HttpURLConnection#setConnectTimeout(int)}
     *
     * @return Connect timeout in milliseconds
     * @see ShuteyeConfig.Defaults#CONNECT_TIMEOUT_MILLIS
     */
    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    /**
     * @see #getConnectTimeoutMillis()
     */
    public void setConnectTimeoutMillis(final int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    /**
     * Gets the read timeout in milliseconds which will be passed to {@link java.net.HttpURLConnection#setReadTimeout(int)}
     *
     * @return Read timeout in milliseconds
     * @see Defaults#READ_TIMEOUT_MILLIS
     */
    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    /**
     * @see #getReadTimeoutMillis()
     */
    public void setReadTimeoutMillis(final int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    /**
     * @return The content type which will be used for strings when no explicit content type is specified
     * @see Defaults#DEFAULT_STRING_CONTENT_TYPE
     */
    public ContentType getDefaultStringContentType() {
        return defaultStringContentType;
    }

    /**
     * @see #getDefaultStringContentType()
     */
    public void setDefaultStringContentType(final ContentType defaultStringContentType) {
        this.defaultStringContentType = checkNotNull(defaultStringContentType, "Null default string content type is not allowed");
    }

    /**
     * @return The encoding which will be used for strings when no explicit encoding is specified.
     * @see Defaults#DEFAULT_STRING_ENCODING
     */
    public Charset getDefaultStringEncoding() {
        return defaultStringEncoding;
    }

    /**
     * @see #getDefaultStringEncoding()
     */
    public void setDefaultStringEncoding(final Charset defaultStringEncoding) {
        this.defaultStringEncoding = checkNotNull(defaultStringEncoding, "Null default string encoding is not allowed");
    }

    /**
     * @return The content type which will be used for streams when no explicit content type is specified
     * @see net.ethx.shuteye.ShuteyeConfig.Defaults#DEFAULT_STREAM_CONTENT_TYPE
     */
    public ContentType getDefaultStreamContentType() {
        return defaultStreamContentType;
    }

    /**
     * @see #getDefaultStreamContentType()
     */
    public void setDefaultStreamContentType(final ContentType defaultStreamContentType) {
        this.defaultStreamContentType = checkNotNull(defaultStreamContentType, "Null default stream content type is not allowed");
    }

    /**
     * @return Whether or not {@link net.ethx.shuteye.uri.UriTemplate} and {@link HttpTemplate} allow extra variables
     * when building URIs from var-args variables.
     * @see Defaults#ALLOW_EXTRA_VARS_IN_VARARG
     */
    public boolean isAllowExtraVarsInVararg() {
        return allowExtraVarsInVararg;
    }

    /**
     * @see #isAllowExtraVarsInVararg() ()
     */
    public void setAllowExtraVarsInVararg(final boolean allowExtraVarsInVararg) {
        this.allowExtraVarsInVararg = allowExtraVarsInVararg;
    }

    /**
     * @return Whether or not {@link net.ethx.shuteye.uri.UriTemplate} and {@link HttpTemplate} allow missing variables
     * when building URIs from var-args variables.
     * @see Defaults#ALLOW_MISSING_VARS_IN_VARARG
     */
    public boolean isAllowMissingVarsInVararg() {
        return allowMissingVarsInVararg;
    }

    /**
     * @see #isAllowMissingVarsInVararg()
     */
    public void setAllowMissingVarsInVararg(final boolean allowMissingVarsInVararg) {
        this.allowMissingVarsInVararg = allowMissingVarsInVararg;
    }

    /**
     * @return Whether or not {@link net.ethx.shuteye.uri.UriTemplate} and {@link HttpTemplate} allow extra variables
     * when building URIs from maps.
     * @see Defaults#ALLOW_EXTRA_VARS_IN_MAP
     */
    public boolean isAllowExtraVarsInMap() {
        return allowExtraVarsInMap;
    }

    /**
     * @see #isAllowExtraVarsInMap()
     */
    public void setAllowExtraVarsInMap(final boolean allowExtraVarsInMap) {
        this.allowExtraVarsInMap = allowExtraVarsInMap;
    }

    /**
     * @return Whether or not {@link net.ethx.shuteye.uri.UriTemplate} and {@link HttpTemplate} allow missing variables
     * when building URIs from maps.
     * @see Defaults#ALLOW_EXTRA_VARS_IN_MAP
     */
    public boolean isAllowMissingVarsInMap() {
        return allowMissingVarsInMap;
    }

    /**
     * @see #isAllowMissingVarsInMap()
     */
    public void setAllowMissingVarsInMap(final boolean allowMissingVarsInMap) {
        this.allowMissingVarsInMap = allowMissingVarsInMap;
    }

    /**
     * @return A new config instance from defaults.
     */
    public static ShuteyeConfig defaults() {
        return new ShuteyeConfig();
    }

    @Override
    public ShuteyeConfig clone() {
        try {
            return (ShuteyeConfig) super.clone();
        } catch (CloneNotSupportedException unexpected) {
            throw new IllegalStateException("Could not clone " + getClass().getSimpleName(), unexpected);
        }
    }

    /**
     * @return The codecs which are supported for Shuteye.
     */
    public List<Codec> getCodecs() {
        return Collections.unmodifiableList(codecs);
    }

    /**
     * @see #getCodecs()
     */
    public void setCodecs(final List<Codec> codecs) {
        this.codecs = Collections.unmodifiableList(checkNotNull(codecs, "Null codecs are not allowed"));
    }

    public void addCodec(final Codec codec) {
        final List<Codec> codecs = new ArrayList<Codec>(getCodecs());
        codecs.add(checkNotNull(codec, "Null codec is not allowed"));
        this.codecs = Collections.unmodifiableList(codecs);
    }

    /**
     * @return The codec which will be used by default when creating {@link BufferedResponse}
     * from a request.
     */
    public Codec getBufferCodec() {
        return bufferCodec;
    }

    /**
     * @see #getBufferCodec()
     */
    public void setBufferCodec(final Codec bufferCodec) {
        this.bufferCodec = checkNotNull(bufferCodec, "Null buffer codec is not allowed");
    }
}
