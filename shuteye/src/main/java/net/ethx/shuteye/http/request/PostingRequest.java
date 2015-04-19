package net.ethx.shuteye.http.request;

import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.util.Preconditions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;

import static net.ethx.shuteye.ShuteyeConfig.*;

/**
 * A 'POST-like' request. This is be used by other HTTP methods than POST (for example, {@link HttpTemplate#put(String, Object...)}
 * <p/>
 * Adding a stream-like entity as a field to the request will result in the request not being reusable, as the stream will have
 * been drained during the first request execution.
 */
public class PostingRequest extends Request {
    private Entity entity = null;

    /**
     * Creates a new request. You should not call this method directly, but use the creation methods from {@link HttpTemplate}
     * to create it.
     *
     * @param template HttpTemplate used by this request
     * @param method   HTTP method (POST, PUT, etc.) used by this request
     * @param url      Resolved URL that will be used by this request
     */
    public PostingRequest(final HttpTemplate template, final String method, final String url) {
        super(template, method, url);
    }

    /**
     * @see Request#header(String, String)
     */
    @Override
    public PostingRequest header(final String header, final String value) {
        return (PostingRequest) super.header(header, value);
    }

    /**
     * Adds the specified stream to the request. This will result in the request being single-use.
     *
     * @param field       Name of the field to hold the stream.
     * @param fileName    Name of the 'file', specified to the server alongside the stream
     * @param stream      Stream of data to add to the field
     * @param contentType Content type of the data
     * @return This request
     */
    public PostingRequest field(final String field, final String fileName, final InputStream stream, final ContentType contentType) {
        requireFormEntity().put(field, new StreamMultipartField(field, fileName, contentType, stream));
        return this;
    }

    /**
     * Adds the specified stream to the request, inferring the content type from the file name. This will result in the request being single-use.
     *
     * @see #field(String, String, InputStream, ContentType)
     */
    public PostingRequest field(final String field, final String fileName, final InputStream stream) {
        return field(field, fileName, stream, guessContentType(fileName));
    }

    /**
     * Adds the specified file to the request, inferring the content type from the file name. This will result in the request being single-use.
     *
     * @see #field(String, String, InputStream, ContentType)
     */
    public PostingRequest field(final String field, final File file) throws FileNotFoundException {
        return field(field, file.getName(), new FileInputStream(file));
    }

    /**
     * Adds the specified string to the request, using the default string encoding configured in the {@link HttpTemplate}.
     *
     * @see #field(String, String, Charset)
     * @see Defaults#DEFAULT_STRING_ENCODING
     */
    public PostingRequest field(final String field, final String value) {
        return field(field, value, template().config().getDefaultStringEncoding());
    }

    /**
     * Adds the specified string to the request, using the specified string encoding.
     *
     * @param field   Name of the field to hold the string
     * @param value   Value to add to the field
     * @param charset Encoding that will be used for the string
     * @return This request
     */
    public PostingRequest field(final String field, final String value, final Charset charset) {
        requireFormEntity().put(field, new StringMultipartField(field, value, charset));
        return this;
    }

    /**
     * Sets the specified file as the body of the request, inferring the content type from the file name. This will result in the request being single-use.
     *
     * @see #body(InputStream, ContentType)
     */
    public PostingRequest body(final File file) throws FileNotFoundException {
        return body(new FileInputStream(file), guessContentType(file.getName()));
    }

    /**
     * Sets the specified stream as the body of the request, using the default content type configured in the {@link HttpTemplate}.
     * This will result in the request being single-use.
     *
     * @see #body(InputStream, ContentType)
     */
    public PostingRequest body(final InputStream stream) {
        return body(stream, template().config().getDefaultStreamContentType());
    }

    /**
     * Sets the specified stream as the body of the request, using the specified content type. This will result in the request being single-use.
     *
     * @param stream      Stream of data to set as the body
     * @param contentType Content type to use for the body
     * @return This request
     */
    public PostingRequest body(final InputStream stream, final ContentType contentType) {
        requireNoEntity();
        this.entity = new StreamEntity(stream, contentType);
        return this;
    }

    /**
     * Sets the specified string as the body of the request, using the default string content type and encoding configured in the
     * {@link HttpTemplate}
     *
     * @see #body(String, ContentType, Charset)
     * @see Defaults#DEFAULT_STRING_CONTENT_TYPE
     * @see Defaults#DEFAULT_STRING_ENCODING
     */
    public PostingRequest body(final String body) {
        return body(body, template().config().getDefaultStringContentType(), template().config().getDefaultStringEncoding());
    }

    /**
     * Sets the specified string as the body of the request, using the default string content type configured in the {@link HttpTemplate}
     * and the specified encoding.
     *
     * @see #body(String, ContentType, Charset)
     * @see Defaults#DEFAULT_STRING_CONTENT_TYPE
     */
    public PostingRequest body(final String body, final Charset charset) {
        return body(body, template().config().getDefaultStringContentType(), charset);
    }

    /**
     * Sets the specified string as the body of the request, using the specified content type and the default string
     * encoding configured in the {@link HttpTemplate}
     *
     * @see #body(String, ContentType, Charset)
     * @see Defaults#DEFAULT_STRING_ENCODING
     */
    public PostingRequest body(final String body, final ContentType contentType) {
        return body(body, contentType, template().config().getDefaultStringEncoding());
    }

    /**
     * Sets the specified string as the body of the request, using the specified content type
     *
     * @param body        String to set as the body
     * @param contentType Content type of the body
     * @param charset     Encoding that will be used for the string
     * @return This request
     */
    public PostingRequest body(final String body, final ContentType contentType, final Charset charset) {
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
        return maybeContentType == null ? template().config().getDefaultStreamContentType() : ContentType.parse(maybeContentType);
    }

    @Override
    protected void writeEntity(final HttpURLConnection connection) throws IOException {
        if (entity != null) {
            entity.writeTo(this, connection);
        }
    }
}
