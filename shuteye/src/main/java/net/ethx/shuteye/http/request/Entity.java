package net.ethx.shuteye.http.request;

import java.io.IOException;
import java.net.HttpURLConnection;

interface Entity {
    void writeTo(final Request request, final HttpURLConnection connection) throws IOException;
}
