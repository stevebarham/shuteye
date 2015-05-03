package net.ethx.shuteye.http.response.trans;

import net.ethx.shuteye.http.response.BufferedResponse;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.TypedResponse;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.util.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Transformers {
    private static final DefaultTransformer<String> DETECT_CHARSET_TEXT_VALUE = new AutoDetectCharsetTextValue();

    /**
     * @return a {@link DefaultTransformer} which will either throw an exception, or return an appropriate string typedResponse
     * from the response. The encoding of the string will be detected from the response.
     */
    public static DefaultTransformer<String> string() {
        return DETECT_CHARSET_TEXT_VALUE;
    }

    /**
     * @param charset Specific charset to use to extract a string typedResponse from the response.
     * @return a {@link DefaultTransformer} which will return an appropriate string typedResponse from successful responses. The
     * encoding of the string is specified by the charset parameter.
     */
    public static DefaultTransformer<String> string(final Charset charset) {
        return new FixedCharsetTextValue(charset);
    }

    /**
     * @return a {@link DefaultTransformer} which does not return any typedResponse when invoked from a successful response, but
     * which throws a suitable exception for HTTP errors.
     */
    public static DefaultTransformer<Void> requiringSuccess() {
        return new RequiringSuccessTransformer();
    }

    /**
     * @param codecs Codecs which can be unpacked by the transformer.
     * @param bufferCodec Codec to use to buffer the response data in memory.
     * @return A {@link Transformer} which will fully fetch and buffer the response data in memory before returning.
     */
    public static Transformer<BufferedResponse> buffered(final List<Codec> codecs, final Codec bufferCodec) {
        return new BufferedResponseTransformer(bufferCodec, codecs);
    }

    /**
     * @param transformer Transformer to produce a typedResponse
     * @param <T> Type of the typedResponse
     * @return A {@link TypedResponse} holding the transformed typedResponse
     */
    public static <T> Transformer<TypedResponse<T>> typedResponse(final Transformer<T> transformer) {
        return new ValueResponseTransformer<T>(transformer);
    }

    private static class RequiringSuccessTransformer extends DefaultTransformer<Void> {
        @Override
        protected Void handle(final Response response, final InputStream stream) throws IOException {
            return null;
        }
    }

    private static class AutoDetectCharsetTextValue extends DefaultTransformer<String> {
        private final Pattern charset = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

        @Override
        protected String handle(final Response response, final InputStream stream) throws IOException {
            final String contentType = response.headers().first("Content-Type");
            if (contentType != null) {
                final Matcher matcher = charset.matcher(contentType);
                if (matcher.find()) {
                    return Streams.toString(stream, Charset.forName(matcher.group(1)));
                }
            }
            return Streams.toString(stream, Charset.defaultCharset());
        }
    }

    private static class FixedCharsetTextValue extends DefaultTransformer<String> {
        private final Charset charset;

        public FixedCharsetTextValue(final Charset charset) {
            this.charset = charset;
        }

        @Override
        protected String handle(final Response response, final InputStream stream) throws IOException {
            return Streams.toString(stream, charset);
        }

        @Override
        public String toString() {
            return String.format("%s [charset=%s]", getClass().getSimpleName(), charset);
        }
    }

    private static class ValueResponseTransformer<T> implements Transformer<TypedResponse<T>> {
        private final Transformer<T> transformer;

        public ValueResponseTransformer(final Transformer<T> transformer) {
            this.transformer = transformer;
        }

        @Override
        public TypedResponse<T> transform(final Response response, final InputStream stream) throws IOException {
            return new TypedResponse<T>(response, transformer.transform(response, stream));
        }
    }
}
