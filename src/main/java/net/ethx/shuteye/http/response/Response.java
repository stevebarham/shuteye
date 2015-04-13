package net.ethx.shuteye.http.response;

import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class Response {
    private final int statusCode;
    private final String statusText;
    private final Headers headers;
    private final byte[] compressedBody;

    public Response(final int statusCode, final String statusText, final Headers headers, final byte[] compressedBody) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.compressedBody = Arrays.copyOf(compressedBody, compressedBody.length);
    }

    public boolean isError() {
        return statusCode >= 300;
    }

    public int statusCode() {
        return statusCode;
    }

    public String statusText() {
        return statusText;
    }

    public Headers headers() {
        return headers;
    }

    public InputStream stream() throws IOException {
        return new GZIPInputStream(new ByteArrayInputStream(compressedBody));
    }

    public String textValue() throws IOException {
        //  todo: identify charset from response
        return Streams.toString(stream(), Charset.defaultCharset());
    }

    public <T> T as(final ResponseTransformer<T> transformer) throws IOException {
        return transformer.transform(this);
    }
}
