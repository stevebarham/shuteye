package net.ethx.shuteye.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.ResponseTransformer;
import net.ethx.shuteye.uri.UriTemplate;
import net.ethx.shuteye.util.Encodings;
import net.iharder.Base64;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

public class ShuteyeTest {
    public static String BASE_URI = "http://httpbin.org";

    @Test
    public void error() {
        for (int i = 300; i < 600; i += 100) {
            final Response response = UriTemplate.build(BASE_URI + "/status/{code}", singletonMap("code", i))
                                                 .get()
                                                 .execute();

            assertTrue(response.isError());
        }
    }

    @Test
    public void get() {
        final Response response = Shuteye.get(BASE_URI).execute();
        assertOK(response);
    }

    @Test
    public void redirect() {
        final Response response = Shuteye.get(BASE_URI + "/redirect/2").execute();
        assertOK(response);
    }

    @Test
    public void gzip() throws IOException {
        final JsonNode json = Shuteye.get(BASE_URI + "/gzip").as(json());
        assertEquals(true, json.get("gzipped").booleanValue());
    }

    @Test
    public void statusCode() {
        for (int i = 200; i < 210; i++) {
            assertEquals(i, UriTemplate.build(BASE_URI + "/status/{code}", singletonMap("code", i))
                                       .get()
                                       .execute().statusCode());
        }
    }

    @Test
    public void bodyTextValue() throws IOException {
        assertTrue(Shuteye.get(BASE_URI).execute().textValue().contains("<html"));
    }

    @Test
    public void requestHeaders() throws IOException {
        final UUID uuid = UUID.randomUUID();

        final Response response = Shuteye.get(BASE_URI + "/headers")
                                         .header("Foo", "bar")
                                         .header("Uuid", uuid.toString())
                                         .execute();
        assertOK(response);

        final JsonNode json = response.as(json());
        assertEquals("bar", json.get("headers").get("Foo").textValue());
        assertEquals(uuid.toString(), json.get("headers").get("Uuid").textValue());
    }

    @Test
    public void responseHeaders() throws IOException {
        final UUID uuid = UUID.randomUUID();

        final Map<String, Object> args = new HashMap<String, Object>();
        args.put("foo", "bar");
        args.put("uuid", uuid.toString());

        final Response response = UriTemplate.build(BASE_URI + "/response-headers{?args*}", singletonMap("args", args))
                                             .get()
                                             .execute();

        assertOK(response);
        assertEquals("bar", response.headers().first("foo"));
        assertEquals(uuid.toString(), response.headers().first("uuid"));
    }

    @Test
    public void fields() throws IOException {
        final UUID uuid = UUID.randomUUID();

        final JsonNode json = Shuteye.post(BASE_URI + "/post")
                                     .field("foo", "bar")
                                     .field("uuid", uuid.toString())
                                     .as(json());

        assertEquals("bar", json.get("form").get("foo").textValue());
        assertEquals(uuid.toString(), json.get("form").get("uuid").textValue());
    }

    @Test
    public void streamField() throws IOException {
        final JsonNode json = Shuteye.post(BASE_URI + "/post")
                                     .field("foo", "testfile.txt", new ByteArrayInputStream("test file content".getBytes()))
                                     .as(json());

        assertEquals("test file content", json.get("files").get("foo").textValue());
    }

    @Test
    public void bodyString() throws IOException {
        final String body = "Testing n↓w";

        assertEquals(body, Shuteye.post(BASE_URI + "/post").body(body).as(json()).get("data").textValue());
        assertEquals("Testing n?w", Shuteye.post(BASE_URI + "/post").body(body, Encodings.ISO8859).as(json()).get("data").textValue());
    }

    @Test
    public void bodyStream() throws IOException {
        final byte[] data = new byte[128];
        new Random().nextBytes(data);

        assertArrayEquals(data, Base64.decode(Shuteye.post(BASE_URI + "/post")
                                                     .body(new ByteArrayInputStream(data))
                                                     .as(json())
                                                     .get("data").textValue().substring("data:application/octet-stream;base64,".length())));
    }

    private void assertOK(final Response response) {
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.statusText());
        assertFalse(response.isError());
    }

    public static ResponseTransformer<JsonNode> json() {
        return new ResponseTransformer<JsonNode>() {
            @Override
            public JsonNode transform(final Response response) throws IOException {
                if (response.isError()) {
                    throw new IllegalStateException(response.statusCode() + " - " + response.statusText());
                }
                return new ObjectMapper().readTree(response.stream());
            }
        };
    }
}
