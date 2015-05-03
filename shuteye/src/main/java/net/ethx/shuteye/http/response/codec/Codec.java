package net.ethx.shuteye.http.response.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Codec {
    String name();

    InputStream decode(final InputStream in) throws IOException;

    OutputStream encode(final OutputStream out) throws IOException;
}
