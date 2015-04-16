package net.ethx.shuteye.meta;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.ethx.shuteye.options.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OptionsTest {
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() throws IOException, ClassNotFoundException {
        final FastClasspathScanner scanner = new FastClasspathScanner("net.ethx.shuteye");
        scanner.scan();
        final List<String> annotated = scanner.getSubclassesOf(Option.class);

        final Collection<Object[]> ret = new ArrayList<Object[]>();
        for (String clazz : annotated) {
            ret.add(new Object[] { Class.forName(clazz) });
        }

        return ret;
    }

    private final Class<?> classUnderTest;

    public OptionsTest(final Class<?> classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    @Test
    public void optionsCorrectlyNamed() throws IllegalAccessException {
        for (Field field : classUnderTest.getDeclaredFields()) {
            if (Option.class.isAssignableFrom(field.getType())) {
                assertEquals(field.toString(), field.getName(), ((Option) field.get(null)).name());
            }
        }
    }
}
