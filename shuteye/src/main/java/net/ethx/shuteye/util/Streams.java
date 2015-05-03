package net.ethx.shuteye.util;

import java.io.*;
import java.nio.charset.Charset;

public abstract class Streams {
    private static final int BUFFER_SIZE = 32768;

    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buf = new byte[BUFFER_SIZE];
        int idx;
        while ((idx = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, idx);
        }
        out.flush();
    }

    public static void drain(final InputStream in) {
        if (in != null) {
            try {
                final byte[] buf = new byte[BUFFER_SIZE];
                while (in.read(buf, 0, buf.length) != -1) {}
            } catch (IOException ignored) {}
        }
    }

    public static String toString(final InputStream in, final Charset charset) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);

        return new String(out.toByteArray(), charset);
    }

    public static InputStream empty() {
        return EofStream.INSTANCE;
    }

    static class EofStream extends InputStream {
        static final EofStream INSTANCE = new EofStream();

        @Override
        public int read() throws IOException {
            return -1;
        }
    }
}
