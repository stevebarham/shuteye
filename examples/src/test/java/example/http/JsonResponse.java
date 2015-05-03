package example.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.trans.Transformer;
import net.ethx.shuteye.http.response.trans.DefaultTransformer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class JsonResponse {
    @Test
    public void test() {
        final HttpTemplate template = new HttpTemplate();
        final JsonNode json = template.get("https://api.github.com")
                                      .as(json());

        System.out.println(json.get("current_user_url").textValue());
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
