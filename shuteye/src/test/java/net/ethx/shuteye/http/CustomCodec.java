package net.ethx.shuteye.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.ShuteyeConfig;
import net.ethx.shuteye.http.response.BufferedResponse;
import net.ethx.shuteye.http.response.codec.Codec;
import net.ethx.shuteye.http.response.trans.Transformers;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomCodec {
    Codec lz4;
    ShuteyeConfig config;
    HttpTemplate template;
    HttpServer server;

    @Before
    public void setup() throws IOException {
        lz4 = new Lz4Codec();

        config = new ShuteyeConfig();
        config.addCodec(lz4);

        template = new HttpTemplate(config);

        server = HttpServer.create(new InetSocketAddress("localhost", 2048), 1);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();
    }

    @After
    public void teardown() throws IOException {
        server.stop(1);
    }

    @Test
    public void bufferWithCodec() {
        final BufferedResponse buffer = template.get("http://www.google.com")
                                                .as(Transformers.buffered(config.getCodecs(), lz4));

        assertTrue(buffer.textValue().contains("html"));
    }

    @Test
    public void servingLz4Content() {
        server.createContext("/lz4", new HttpHandler() {
            @Override
            public void handle(final HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Content-Encoding", "lz4");
                exchange.sendResponseHeaders(200, 0);

                final PrintWriter writer = new PrintWriter(new OutputStreamWriter(lz4.encode(exchange.getResponseBody())));
                writer.print("Hello, LZ4 world!");
                writer.flush();
                writer.close();
            }
        });

        assertEquals("Hello, LZ4 world!", template.get("http://localhost:2048/lz4")
                                                  .textValue());
    }


    private static class Lz4Codec implements Codec {
        @Override
        public String name() {
            return "lz4";
        }

        @Override
        public InputStream decode(final InputStream in) throws IOException {
            return new LZ4BlockInputStream(in);
        }

        @Override
        public OutputStream encode(final OutputStream out) throws IOException {
            return new LZ4BlockOutputStream(out);
        }
    }
}
