package net.ethx.shuteye.uri;

import com.google.common.collect.ImmutableMap;
import net.ethx.shuteye.http.except.TemplateException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriTemplateTest {
    UriTemplateCompiler context;

    @Before
    public void setup() {
        context = new UriTemplateCompiler();
    }

    @Test(expected = TemplateException.class)
    public void insufficientArgumentsVarArgs() {
        context.config().setAllowMissingVarsInVararg(false);
        context.process("{foo}{bar}{baz}", 1, 2);
    }

    @Test(expected = TemplateException.class)
    public void tooManyArgumentsVarArgs() {
        context.config().setAllowExtraVarsInVararg(false);
        context.process("{foo}{bar}{baz}", 1, 2, 3, 4);
    }

    @Test
    public void insufficientArgumentsVarArgsAllowed() {
        context.config().setAllowMissingVarsInVararg(true);
        assertEquals("12", context.process("{foo}{bar}{baz}", 1, 2));
    }

    @Test
    public void tooManyArgumentsVarArgsAllowed() {
        context.config().setAllowExtraVarsInVararg(true);
        assertEquals("123", context.process("{foo}{bar}{baz}", 1, 2, 3, 4));
    }

    @Test(expected = TemplateException.class)
    public void insufficientArgumentsMap() {
        context.config().setAllowMissingVarsInMap(false);
        context.process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2)));
    }

    @Test(expected = TemplateException.class)
    public void tooManyArgumentsMap() {
        context.config().setAllowExtraVarsInMap(false);
        context.process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2, "baz", 3, "qux", 4)));
    }

    @Test
    public void insufficientArgumentsMapAllowed() {
        context.config().setAllowMissingVarsInMap(true);
        assertEquals("12", context.process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2))));
    }

    @Test
    public void tooManyArgumentsMapAllowed() {
        context.config().setAllowExtraVarsInMap(true);
        assertEquals("123", context.process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2, "baz", 3, "qux", 4))));
    }
}
