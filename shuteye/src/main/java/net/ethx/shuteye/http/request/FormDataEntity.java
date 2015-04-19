package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.ContentType;
import net.ethx.shuteye.util.Encodings;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

class FormDataEntity implements Entity {
    static final String LINE_FEED = "\r\n";
    static final String BOUNDARY_DELIMIT = "--";

    private final Map<String, MultipartField> fields = new LinkedHashMap<String, MultipartField>();

    @Override
    public void writeTo(final Request request, final HttpURLConnection connection) throws IOException {
        final String boundary = Long.toString(System.currentTimeMillis()) + Long.toString(System.nanoTime());

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", ContentType.MULTIPART_FORM_DATA + "; boundary=\"" + boundary + "\"");
        final OutputStream out = connection.getOutputStream();
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, Encodings.ISO8859));
        try {
            for (MultipartField field : fields.values()) {
                writer.print(BOUNDARY_DELIMIT + boundary);
                writer.print(LINE_FEED);
                writer.flush();

                field.writeTo(out);
            }

            writer.print(BOUNDARY_DELIMIT + boundary + BOUNDARY_DELIMIT);
            writer.print(LINE_FEED);
        } finally {
            writer.flush();
        }
    }

    public void put(final String field, final MultipartField value) {
        fields.put(field, value);
    }
}
