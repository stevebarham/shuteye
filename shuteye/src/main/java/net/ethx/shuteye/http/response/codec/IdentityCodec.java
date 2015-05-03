package net.ethx.shuteye.http.response.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IdentityCodec implements Codec {
    @Override
    public String name() {
        return "identity";
    }

    @Override
    public InputStream decode(final InputStream in) throws IOException {
        return in;
    }

    @Override
    public OutputStream encode(final OutputStream out) throws IOException {
        return out;
    }
}
