package net.ethx.shuteye.http.request;

import java.io.IOException;
import java.io.OutputStream;

interface MultipartField {
    void writeTo(final OutputStream out) throws IOException;
}
