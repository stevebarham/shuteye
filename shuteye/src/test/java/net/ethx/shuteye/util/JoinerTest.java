package net.ethx.shuteye.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class JoinerTest {
    @Test
    public void join() throws Exception {
        assertEquals("foo,bar", Joiner.join(Arrays.asList("foo", "bar"), ","));
        assertEquals("foo", Joiner.join(Arrays.asList("foo"), ","));
        assertEquals("foo", Joiner.join(Arrays.asList(null, "foo"), ","));
    }
}