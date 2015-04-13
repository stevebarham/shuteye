package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.ResponseTransformer;
import net.ethx.shuteye.util.Preconditions;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

public abstract class Request {
    private final String method;
    private final String url;
    private final MutableHeaders headers = new MutableHeaders();

    public Request(final String method, final String url) {
        this.method = method;
        this.url = url;

        header("Accept-Encoding", "gzip");
    }

    public Request header(final String header, final String value) {
        headers.add(header, value);
        return this;
    }

    public <T> T as(final ResponseTransformer<T> transformer) {
        try {
            return execute().as(transformer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Response execute() {
        try {
            final URL url = new URL(url());
            Preconditions.checkArgument(url.getProtocol().startsWith("http"), "Only HTTP(S) protocol supported for URI, found %s", url);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method());
            connection.setInstanceFollowRedirects("GET".equals(method()));

            for (String header : headers.headerNames()) {
                for (String value : headers.all(header)) {
                    connection.addRequestProperty(header, value);
                }
            }

            writeEntity(connection);

            try {
                final int statusCode = connection.getResponseCode();
                final String statusText = connection.getResponseMessage();
                final Headers responseHeaders = new Headers(connection.getHeaderFields());

                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final String encoding = responseHeaders.first("Content-Encoding");
                final InputStream encoded = statusCode < 300 ? connection.getInputStream() : connection.getErrorStream();
                if (encoded != null) {
                    if ("gzip".equalsIgnoreCase(encoding)) {
                        Streams.copy(encoded, out);
                    } else {
                        final GZIPOutputStream gz = new GZIPOutputStream(out);
                        try {
                            Streams.copy(encoded, gz);
                        } finally {
                            gz.close();
                        }
                    }
                }

                return new Response(statusCode, statusText, responseHeaders, out.toByteArray());
            } finally {
                connection.disconnect();
            }
        } catch (IOException ie) {
            throw new IllegalStateException("Could not execute request", ie);
        }
    }

    protected abstract void writeEntity(final HttpURLConnection connection) throws IOException;

    public String url() {
        return url;
    }

    public String method() {
        return method;
    }

    public Headers headers() {
        return headers;
    }
}
