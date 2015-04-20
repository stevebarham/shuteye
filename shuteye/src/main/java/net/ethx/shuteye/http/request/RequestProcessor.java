package net.ethx.shuteye.http.request;

import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPOutputStream;

class RequestProcessor {
    private final HttpTemplate template;
    private final Request request;

    public RequestProcessor(final HttpTemplate template, Request request) {
        this.request = request;
        this.template = template;
    }

    public Response execute() {
        try {
            final HttpURLConnection connection = (HttpURLConnection) request.url().openConnection();
            connection.setRequestMethod(request.method());
            connection.setInstanceFollowRedirects("GET".equals(request.method()));
            connection.setConnectTimeout(template.config().getConnectTimeoutMillis());
            connection.setReadTimeout(template.config().getReadTimeoutMillis());

            for (String header : request.headers().headerNames()) {
                for (String value : request.headers().all(header)) {
                    connection.addRequestProperty(header, value);
                }
            }

            request.writeEntity(connection);

            try {
                final int statusCode = connection.getResponseCode();
                final String statusText = connection.getResponseMessage();
                final Headers responseHeaders = new Headers(connection.getHeaderFields());
                final String encoding = responseHeaders.first("Content-Encoding");

                byte[] response;
                try {
                    response = extractStream(connection.getInputStream(), encoding);
                } catch (IOException ie) {
                    response = extractStream(connection.getErrorStream(), encoding);
                }

                return new Response(statusCode, statusText, responseHeaders, response);
            } finally {
                connection.disconnect();
            }
        } catch (IOException ie) {
            throw new ShuteyeException(String.format("Could not execute request to %s", request.url()), ie);
        }
    }

    private byte[] extractStream(final InputStream stream, final String encoding) throws IOException {
        if (stream != null) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            final InputStream source;
            final OutputStream dest;
            if ("gzip".equalsIgnoreCase(encoding)) {
                source = stream;
                dest = out;
            } else if ("deflate".equalsIgnoreCase(encoding)) {
                source = new DeflaterInputStream(stream);
                dest = new GZIPOutputStream(out);
            } else {
                source = stream;
                dest = new GZIPOutputStream(out);
            }
            try {
                try {
                    Streams.copy(source, dest);
                } finally {
                    dest.close();
                }
            } finally {
                source.close();
            }

            return out.toByteArray();
        }

        return null;
    }
}
