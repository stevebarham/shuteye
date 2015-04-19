package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.util.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

class StreamEntity implements Entity {
    private final InputStream stream;
    private final ContentType contentType;

    public StreamEntity(final InputStream stream, final ContentType contentType) {
        this.stream = stream;
        this.contentType = contentType;
    }

    @Override
    public void writeTo(final Request request, final HttpURLConnection connection) throws IOException {
        connection.setRequestProperty("Content-Type", contentType.toString());
        connection.setDoOutput(true);

        final OutputStream out = connection.getOutputStream();
        Streams.copy(stream, out);
        out.flush();
    }
}
