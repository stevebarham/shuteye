package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.ShuteyeContext;
import net.ethx.shuteye.http.ShuteyeOption;
import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.ResponseTransformer;
import net.ethx.shuteye.util.Preconditions;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class Request {
    private final ShuteyeContext context;
    private final String method;
    private final String url;
    private final MutableHeaders headers = new MutableHeaders();

    public Request(final ShuteyeContext context, final String method, final String url) {
        this.context = context;
        this.method = method;
        this.url = url;

        header("Accept-Encoding", "gzip");
    }

    public ShuteyeContext context() {
        return context;
    }

    public Request header(final String header, final String value) {
        headers.add(header, value);
        return this;
    }

    public <T> T as(final ResponseTransformer<T> transformer) {
        try {
            return execute().as(transformer);
        } catch (IOException e) {
            throw new ShuteyeException(String.format("Could not convert response using %s", transformer), e);
        }
    }

    public Response execute() {
        try {
            final URL url = new URL(url());
            Preconditions.checkArgument(url.getProtocol().startsWith("http"), "Only HTTP(S) protocol supported for URI, found %s", url);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method());
            connection.setInstanceFollowRedirects("GET".equals(method()));
            connection.setConnectTimeout(ShuteyeOption.CONNECT_TIMEOUT_MILLIS.get(context.options()));
            connection.setReadTimeout(ShuteyeOption.READ_TIMEOUT_MILLIS.get(context.options()));

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

                //  todo: follow redirects
                final InputStream encoded = statusCode < 400 ? connection.getInputStream() : connection.getErrorStream();
                if (encoded != null) {
                    final InputStream source; final OutputStream dest;
                    if ("gzip".equalsIgnoreCase(encoding)) {
                        source = encoded;
                        dest = out;
                    } else if ("deflate".equalsIgnoreCase(encoding)) {
                        source = new DeflaterInputStream(encoded);
                        dest = new GZIPOutputStream(out);
                    } else {
                        source = encoded;
                        dest = new GZIPOutputStream(out);
                    }
                    try {
                        Streams.copy(source, dest);
                    } finally {
                        source.close();
                        dest.close();
                    }
                }

                return new Response(statusCode, statusText, responseHeaders, out.toByteArray());
            } finally {
                connection.disconnect();
            }
        } catch (IOException ie) {
            throw new ShuteyeException(String.format("Could not execute request to %s", url), ie);
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
