package net.ethx.shuteye.uri;

import com.google.common.collect.ImmutableMap;
import net.ethx.shuteye.http.except.TemplateException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriTemplateTest {
    @Test(expected = TemplateException.class)
    public void insufficientArgumentsVarArgs() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.AllowMissingVariablesForVarArg, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", 1, 2);
    }

    @Test(expected = TemplateException.class)
    public void tooManyArgumentsVarArgs() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.AllowExtraVariablesForVarArg, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", 1, 2, 3, 4);
    }

    @Test
    public void insufficientArgumentsVarArgsAllowed() {
        assertEquals("12", UriTemplateContext.defaultContext()
                                             .with(UriTemplateOption.AllowMissingVariablesForVarArg, true)
                                             .process("{foo}{bar}{baz}", 1, 2));
    }

    @Test
    public void tooManyArgumentsVarArgsAllowed() {
        assertEquals("123", UriTemplateContext.defaultContext()
                                              .with(UriTemplateOption.AllowExtraVariablesForVarArg, true)
                                              .process("{foo}{bar}{baz}", 1, 2, 3, 4));
    }

    @Test(expected = TemplateException.class)
    public void insufficientArgumentsMap() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.AllowMissingVariablesForMap, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2)));
    }

    @Test(expected = TemplateException.class)
    public void tooManyArgumentsMap() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.AllowExtraVariablesForMap, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2, "baz", 3, "qux", 4)));
    }

    @Test
    public void insufficientArgumentsMapAllowed() {
        assertEquals("12", UriTemplateContext.defaultContext()
                                             .with(UriTemplateOption.AllowMissingVariablesForMap, true)
                                             .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2))));
    }

    @Test
    public void tooManyArgumentsMapAllowed() {
        assertEquals("123", UriTemplateContext.defaultContext()
                                              .with(UriTemplateOption.AllowExtraVariablesForMap, true)
                                              .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2, "baz", 3, "qux", 4))));
    }
}
