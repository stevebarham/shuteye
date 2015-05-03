package net.ethx.shuteye.http.response.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class DeflateCodec implements Codec {
    @Override
    public String name() {
        return "deflate";
    }

    @Override
    public InputStream decode(final InputStream in) throws IOException {
        return new InflaterInputStream(in);
    }

    @Override
    public OutputStream encode(final OutputStream out) throws IOException {
        return new DeflaterOutputStream(out);
    }
}
