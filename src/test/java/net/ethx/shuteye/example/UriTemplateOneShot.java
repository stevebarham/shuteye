package net.ethx.shuteye.example;

import net.ethx.shuteye.uri.UriTemplate;
import net.ethx.shuteye.uri.Vars;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UriTemplateOneShot {
    @Test
    public void test() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("user", "stevebarham");
        vars.put("repo", "shuteye");
        vars.put("function", "commits");

        String uri = UriTemplate.process("https://api.github.com/repos{/user,repo,function}", Vars.wrap(vars));
        String uri2 = UriTemplate.process("https://api.github.com/repos{/user,repo,function}", "stevebarham", "shuteye", "commits");

        assertEquals(uri, "https://api.github.com/repos/stevebarham/shuteye/commits");
        assertEquals(uri, uri2);
    }
}
