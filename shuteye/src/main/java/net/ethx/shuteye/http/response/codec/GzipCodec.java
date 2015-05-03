package net.ethx.shuteye.http.response.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCodec implements Codec {
    @Override
    public String name() {
        return "gzip";
    }

    @Override
    public InputStream decode(final InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }

    @Override
    public OutputStream encode(final OutputStream out) throws IOException {
        return new GZIPOutputStream(out);
    }
}
