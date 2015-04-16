package net.ethx.shuteye.http.response;

import net.ethx.shuteye.util.Streams;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Transformers {
    private static final BaseResponseTransformer<String> DETECT_CHARSET_TEXT_VALUE = new AutoDetectCharsetTextValue();

    public static BaseResponseTransformer<String> string() {
        return DETECT_CHARSET_TEXT_VALUE;
    }

    public static BaseResponseTransformer<String> string(final Charset charset) {
        return new FixedCharsetTextValue(charset);
    }

    private static class AutoDetectCharsetTextValue extends BaseResponseTransformer<String> {
        private final Pattern charset = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

        @Override
        protected String handle(final Response response) throws IOException {
            final String contentType = response.headers().first("Content-Type");
            if (contentType != null) {
                final Matcher matcher = charset.matcher(contentType);
                if (matcher.find()) {
                    return Streams.toString(response.stream(), Charset.forName(matcher.group(1)));
                }
            }
            return Streams.toString(response.stream(), Charset.defaultCharset());
        }
    }

    private static class FixedCharsetTextValue extends BaseResponseTransformer<String> {
        private final Charset charset;

        public FixedCharsetTextValue(final Charset charset) {
            this.charset = charset;
        }

        @Override
        protected String handle(final Response response) throws IOException {
            return Streams.toString(response.stream(), charset);
        }

        @Override
        public String toString() {
            return String.format("%s [charset=%s]", getClass().getSimpleName(), charset);
        }
    }
}
