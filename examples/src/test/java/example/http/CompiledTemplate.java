package example.http;

import net.ethx.shuteye.HttpTemplate;
import net.ethx.shuteye.uri.UriTemplate;
import org.junit.Test;

import static net.ethx.shuteye.http.response.trans.Transformers.string;

/**
 * Demonstrates reuse of a compiled {@link net.ethx.shuteye.uri.UriTemplate} with {@link net.ethx.shuteye.HttpTemplate}
 */
public class CompiledTemplate {
    @Test
    public void test() {
        final HttpTemplate http = new HttpTemplate();
        final UriTemplate uri = http.compile("https://api.github.com/repos{/user,repo,function}");

        System.out.println(http.get(uri, "stevebarham", "jcodemodel", "commits").as(string()));
        System.out.println(http.get(uri, "phax", "jcodemodel", "commits").as(string()));
    }
}
