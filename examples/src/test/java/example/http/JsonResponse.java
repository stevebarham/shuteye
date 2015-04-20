package example.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.http.response.Response;
import net.ethx.shuteye.http.response.ResponseTransformer;
import net.ethx.shuteye.http.response.SuccessTransformer;
import org.junit.Test;

import java.io.IOException;

public class JsonResponse {
    @Test
    public void test() {
        final HttpTemplate template = new HttpTemplate();
        final JsonNode json = template.get("https://api.github.com").as(json());

        System.out.println(json.get("current_user_url").textValue());
    }

    static ResponseTransformer<JsonNode> json() {
        return SuccessTransformer.onSuccess(new ResponseTransformer<JsonNode>() {
            @Override
            public JsonNode transform(final Response response) throws IOException {
                return new ObjectMapper().readTree(response.inputStream());
            }
        });
    }
}
