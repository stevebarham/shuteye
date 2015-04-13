package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.util.Encodings;
import net.ethx.shuteye.util.Streams;

import java.io.*;

class StreamMultipartField implements MultipartField {
    private final String name;
    private final String fileName;
    private final ContentType contentType;
    private final InputStream stream;

    public StreamMultipartField(final String name, final String fileName, final ContentType contentType, final InputStream stream) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.stream = stream;
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
        final PrintWriter preamble = new PrintWriter(new OutputStreamWriter(out, Encodings.ISO8859));
        try {
            preamble.print("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"" + FormDataEntity.LINE_FEED);
            preamble.print("Content-Type: " + contentType + FormDataEntity.LINE_FEED);
            preamble.print("Content-Transfer-Encoding: binary" + FormDataEntity.LINE_FEED);
            preamble.print(FormDataEntity.LINE_FEED);
            preamble.flush();

            Streams.copy(stream, out);

            preamble.print(FormDataEntity.LINE_FEED);
        } finally {
            preamble.flush();
        }
    }
}
