package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.Headers;
import net.ethx.shuteye.http.except.ShuteyeException;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.http.response.trans.Transformer;
import net.ethx.shuteye.util.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

class RequestProcessor<T> {
    private final Request request;
    private final Transformer<T> transformer;

    public RequestProcessor(final Request request, final Transformer<T> transformer) {
        this.request = request;
        this.transformer = transformer;
    }

    public T execute() {
        try {
            final HttpURLConnection connection = (HttpURLConnection) request.url().openConnection();
            connection.setRequestMethod(request.method());
            connection.setInstanceFollowRedirects("GET".equals(request.method()));
            connection.setConnectTimeout(request.template().config().getConnectTimeoutMillis());
            connection.setReadTimeout(request.template().config().getReadTimeoutMillis());

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
                final Response response = new Response(statusCode, statusText, responseHeaders);

                try {
                    final InputStream input = connection.getInputStream();
                    try {
                        return execute(request, response, input);
                    } finally {
                        Streams.drain(input);
                    }
                } catch (IOException e) {
                    final InputStream error = connection.getErrorStream();
                    try {
                        return execute(request, response, connection.getErrorStream());
                    } finally {
                        Streams.drain(error);
                    }
                }
            } finally {
                connection.disconnect();
            }
        } catch (IOException ie) {
            throw new ShuteyeException(String.format("Could not buffer request to %s", request.url()), ie);
        }
    }

    protected T execute(final Request request, final Response response, final InputStream stream) throws IOException {
        InputStream decoded;
        if (stream == null) {
            decoded = Streams.empty();
        } else {
            decoded = stream;

            final String encoding = response.headers().first("Content-Encoding");
            if (encoding != null) {
                for (Codec codec : request.template().config().getCodecs()) {
                    if (encoding.equals(codec.name())) {
                        decoded = codec.decode(stream);
                        break;
                    }
                }
            }
        }

        try {
            return transformer.transform(response, decoded);
        } catch (IOException e) {
            throw new ShuteyeException(String.format("Could not convert response using %s", transformer), e);
        }
    }
}