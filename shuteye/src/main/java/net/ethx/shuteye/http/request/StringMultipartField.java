package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.util.Encodings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

class StringMultipartField implements MultipartField {
    private final String name;
    private final String value;
    private final Charset charset;

    public StringMultipartField(final String name, final String value, final Charset charset) {
        this.name = name;
        this.value = value;
        this.charset = charset;
    }

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        final PrintWriter preamble = new PrintWriter(new OutputStreamWriter(out, Encodings.ISO8859));
        try {
            preamble.print("Content-Disposition: form-data; name=\"" + name + "\"" + FormDataEntity.LINE_FEED);
            preamble.print("Content-Type: " + ContentType.TEXT_PLAIN + "; charset=" + charset.name() + FormDataEntity.LINE_FEED);
            preamble.print(FormDataEntity.LINE_FEED);
            preamble.flush();

            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, charset));
            try {
                writer.print(value);
            } finally {
                writer.flush();
            }

            preamble.print(FormDataEntity.LINE_FEED);
        } finally {
            preamble.flush();
        }
    }
}
