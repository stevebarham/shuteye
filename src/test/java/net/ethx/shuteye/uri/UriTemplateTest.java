package net.ethx.shuteye.uri;

import com.google.common.collect.ImmutableMap;
import net.ethx.shuteye.http.except.TemplateException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriTemplateTest {
    @Test(expected = TemplateException.class)
    public void insufficientArgumentsVarArgs() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.ALLOW_MISSING_VARS_IN_VARARG, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", 1, 2);
    }

    @Test(expected = TemplateException.class)
    public void tooManyArgumentsVarArgs() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.ALLOW_EXTRA_VARS_IN_VARARG, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", 1, 2, 3, 4);
    }

    @Test
    public void insufficientArgumentsVarArgsAllowed() {
        assertEquals("12", UriTemplateContext.defaultContext()
                                             .with(UriTemplateOption.ALLOW_MISSING_VARS_IN_VARARG, true)
                                             .process("{foo}{bar}{baz}", 1, 2));
    }

    @Test
    public void tooManyArgumentsVarArgsAllowed() {
        assertEquals("123", UriTemplateContext.defaultContext()
                                              .with(UriTemplateOption.ALLOW_EXTRA_VARS_IN_VARARG, true)
                                              .process("{foo}{bar}{baz}", 1, 2, 3, 4));
    }

    @Test(expected = TemplateException.class)
    public void insufficientArgumentsMap() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.ALLOW_MISSING_VARS_IN_MAP, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2)));
    }

    @Test(expected = TemplateException.class)
    public void tooManyArgumentsMap() {
        UriTemplateContext.defaultContext()
                          .with(UriTemplateOption.ALLOW_EXTRA_VARS_IN_MAP, Boolean.FALSE)
                          .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2, "baz", 3, "qux", 4)));
    }

    @Test
    public void insufficientArgumentsMapAllowed() {
        assertEquals("12", UriTemplateContext.defaultContext()
                                             .with(UriTemplateOption.ALLOW_MISSING_VARS_IN_MAP, true)
                                             .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2))));
    }

    @Test
    public void tooManyArgumentsMapAllowed() {
        assertEquals("123", UriTemplateContext.defaultContext()
                                              .with(UriTemplateOption.ALLOW_EXTRA_VARS_IN_MAP, true)
                                              .process("{foo}{bar}{baz}", Vars.wrap(ImmutableMap.of("foo", 1, "bar", 2, "baz", 3, "qux", 4))));
    }
}
