package example.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ethx.shuteye.HttpTemplate;
import org.junit.Test;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.ethx.shuteye.http.response.SuccessTransformer.onSuccess;

public class LambdaHttpGet {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() {
        HttpTemplate template = new HttpTemplate();

        //  grab the top stories array
        JsonNode topStories = template.get("https://hacker-news.firebaseio.com/v0/topstories.json")
                                      .as(onSuccess(r -> mapper.readTree(r.inputStream())));

        //  fetch each top story, and list its details
        stream(topStories).limit(20)
                .parallel()
                .map(JsonNode::intValue)
                .map(id -> template.get("https://hacker-news.firebaseio.com/v0/item/{id}.json", id)
                                   .as(onSuccess(r -> mapper.readTree(r.inputStream()))))
                .forEach(node -> System.out.printf("%s: %s%n", node.get("by").textValue(), node.get("title").textValue()));
    }

    //  adapt iterable to stream
    <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED), false);
    }
}
