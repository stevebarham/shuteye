package net.ethx.shuteye.util;

import java.io.*;
import java.nio.charset.Charset;

public class Streams {
    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buf = new byte[4096];
        int idx;
        while ((idx = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, idx);
        }
        out.flush();
    }

    public static String toString(final InputStream in, final Charset charset) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);

        return new String(out.toByteArray(), charset);
    }
}
