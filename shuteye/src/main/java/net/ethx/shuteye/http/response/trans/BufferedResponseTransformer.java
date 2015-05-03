package net.ethx.shuteye.http.response.trans;

import net.ethx.shuteye.http.response.BufferedResponse;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.util.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

class BufferedResponseTransformer implements Transformer<BufferedResponse> {
    private final Codec bufferCodec;
    private final List<Codec> codecs;

    public BufferedResponseTransformer(final Codec bufferCodec, final List<Codec> codecs) {
        this.bufferCodec = bufferCodec;
        this.codecs = codecs;
    }

    @Override
    public BufferedResponse transform(final Response response, final InputStream stream) throws IOException {
        if (stream == null) {
            return new BufferedResponse(response, null);
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream source = null;
        OutputStream dest = null;

        final String encoding = response.headers().first("Content-Encoding");
        if (bufferCodec.name().equals(encoding)) {
            source = stream;
            dest = out;
        } else {
            for (Iterator<Codec> iterator = codecs.iterator(); source == null && iterator.hasNext(); ) {
                final Codec codec = iterator.next();
                if (codec.name().equals(encoding)) {
                    source = codec.decode(stream);
                    dest = bufferCodec.encode(out);
                }
            }

            if (source == null) {
                source = stream;
                dest = bufferCodec.encode(out);
            }
        }

        try {
            try {
                Streams.copy(source, dest);
            } finally {
                dest.close();
            }
            return new BufferedResponse(response, bufferCodec, out.toByteArray());
        } finally {
            source.close();
        }
    }
}
