package example.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.trans.Transformer;
import net.ethx.shuteye.http.response.trans.DefaultTransformer;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class HttpPost {
    @Test
    public void test() {
        HttpTemplate template = new HttpTemplate();

        final JsonNode json = template.post("https://httpbin.org/post")
                                      .field("name", "Shuteye")
                                      .field("document", new File("src/test/resources/sample.txt"))
                                      .as(json());

        assertEquals("Shuteye", json.get("form").get("name").textValue());
        assertEquals("Hello, world!", json.get("files").get("document").textValue());
    }

    static Transformer<JsonNode> json() {
        return new DefaultTransformer<JsonNode>() {
            @Override
            protected JsonNode handle(final Response response, final InputStream stream) throws IOException {
                return new ObjectMapper().readTree(stream);
            }
        };
    }
}
