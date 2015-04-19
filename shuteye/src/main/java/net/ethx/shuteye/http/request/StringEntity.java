package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ContentType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

class StringEntity implements Entity {
    private final String body;
    private final ContentType contentType;
    private final Charset charset;

    public StringEntity(final String body, final ContentType contentType, final Charset charset) {
        this.body = body;
        this.contentType = contentType;
        this.charset = charset;
    }

    @Override
    public void writeTo(final Request request, final HttpURLConnection connection) throws IOException {
        connection.setRequestProperty("Content-Type", contentType + "; charset=" + charset.name());
        connection.setDoOutput(true);

        final OutputStream out = connection.getOutputStream();
        out.write(body.getBytes(charset));
        out.flush();
    }
}
