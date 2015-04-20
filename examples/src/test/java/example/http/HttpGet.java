package example.http;

import net.ethx.shuteye.HttpTemplate;
import org.junit.Test;

public class HttpGet {
    @Test
    public void simpleGet() {
        HttpTemplate template = new HttpTemplate();
        String html = template.get("http://www.google.co.uk").textValue();

        System.out.println(html);
    }
}
