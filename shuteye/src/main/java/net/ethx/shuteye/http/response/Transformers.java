package net.ethx.shuteye.http.response;

import net.ethx.shuteye.util.Streams;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Transformers {
    private static final SuccessTransformer<String> DETECT_CHARSET_TEXT_VALUE = new AutoDetectCharsetTextValue();

    /**
     * @return a {@link SuccessTransformer} which will either throw an exception, or return an appropriate string value
     * from the response. The encoding of the string will be detected from the response.
     */
    public static SuccessTransformer<String> string() {
        return DETECT_CHARSET_TEXT_VALUE;
    }

    /**
     * @param charset Specific charset to use to extract a string value from the response.
     *
     * @return a {@link SuccessTransformer} which will return an appropriate string value from successful responses. The
     * encoding of the string is specified by the charset parameter.
     */
    public static SuccessTransformer<String> string(final Charset charset) {
        return new FixedCharsetTextValue(charset);
    }

    /**
     * @return a {@link SuccessTransformer} which does not return any value when invoked from a successful response, but
     * which throws a suitable exception for HTTP errors.
     */
    public static SuccessTransformer<Void> requiringSuccess() {
        return new RequiringSuccessTransformer();
    }

    private static class RequiringSuccessTransformer extends SuccessTransformer<Void> {
        @Override
        protected Void handle(final Response response) throws IOException {
            return null;
        }
    }

    private static class AutoDetectCharsetTextValue extends SuccessTransformer<String> {
        private final Pattern charset = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

        @Override
        protected String handle(final Response response) throws IOException {
            final String contentType = response.headers().first("Content-Type");
            if (contentType != null) {
                final Matcher matcher = charset.matcher(contentType);
                if (matcher.find()) {
                    return Streams.toString(response.inputStream(), Charset.forName(matcher.group(1)));
                }
            }
            return Streams.toString(response.inputStream(), Charset.defaultCharset());
        }
    }

    private static class FixedCharsetTextValue extends SuccessTransformer<String> {
        private final Charset charset;

        public FixedCharsetTextValue(final Charset charset) {
            this.charset = charset;
        }

        @Override
        protected String handle(final Response response) throws IOException {
            return Streams.toString(response.inputStream(), charset);
        }

        @Override
        public String toString() {
            return String.format("%s [charset=%s]", getClass().getSimpleName(), charset);
        }
    }
}
