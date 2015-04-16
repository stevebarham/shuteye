package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.http.ShuteyeContext;
import net.ethx.shuteye.util.Preconditions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;

import static net.ethx.shuteye.http.ShuteyeOption.*;

public class PostRequest extends Request {
    private Entity entity = null;

    public PostRequest(final ShuteyeContext context, final String method, final String uri) {
        super(context, method, uri);
    }

    @Override
    public PostRequest header(final String header, final String value) {
        return (PostRequest) super.header(header, value);
    }

    public PostRequest field(final String field, final String name, final InputStream stream, final ContentType contentType) {
        requireFormEntity().put(field, new StreamMultipartField(field, name, contentType, stream));
        return this;
    }

    public PostRequest field(final String field, final String name, final InputStream stream) {
        return field(field, name, stream, guessContentType(name));
    }

    public PostRequest field(final String field, final File file) throws FileNotFoundException {
        return field(field, file.getName(), new FileInputStream(file));
    }

    public PostRequest field(final String field, final String value) {
        return field(field, value, DEFAULT_STRING_ENCODING.get(context().options()));
    }

    public PostRequest field(final String field, final String value, final Charset charset) {
        requireFormEntity().put(field, new StringMultipartField(field, value, charset));
        return this;
    }

    public PostRequest body(final File file) throws FileNotFoundException {
        return body(new FileInputStream(file), guessContentType(file.getName()));
    }

    public PostRequest body(final InputStream stream) {
        return body(stream, DEFAULT_STREAM_CONTENT_TYPE.get(context().options()));
    }

    public PostRequest body(final InputStream stream, final ContentType contentType) {
        requireNoEntity();
        this.entity = new StreamEntity(stream, contentType);
        return this;
    }

    public PostRequest body(final String body) {
        return body(body, DEFAULT_STRING_CONTENT_TYPE.get(context().options()), DEFAULT_STRING_ENCODING.get(context().options()));
    }

    public PostRequest body(final String body, final Charset charset) {
        return body(body, DEFAULT_STRING_CONTENT_TYPE.get(context().options()), charset);
    }

    public PostRequest body(final String body, final ContentType contentType) {
        return body(body, contentType, DEFAULT_STRING_ENCODING.get(context().options()));
    }

    public PostRequest body(final String body, final ContentType contentType, final Charset charset) {
        requireNoEntity();
        this.entity = new StringEntity(body, contentType, charset);
        return this;
    }

    private FormDataEntity requireFormEntity() {
        if (entity == null) {
            entity = new FormDataEntity();
        }

        Preconditions.checkState(entity instanceof FormDataEntity, "Attempting to add a form field to a request, but existing body %s is not a %s", entity.getClass(), FormDataEntity.class);
        return ((FormDataEntity) entity);
    }

    private void requireNoEntity() {
        Preconditions.checkState(this.entity == null, "Body has already been set to %s", this.entity);
    }

    private ContentType guessContentType(final String fileName) {
        final String maybeContentType = URLConnection.guessContentTypeFromName(fileName);
        return maybeContentType == null ? DEFAULT_STREAM_CONTENT_TYPE.get(context().options()) : ContentType.parse(maybeContentType);
    }

    @Override
    protected void writeEntity(final HttpURLConnection connection) throws IOException {
        if (entity != null) {
            entity.writeTo(this, connection);
        }
    }
}
