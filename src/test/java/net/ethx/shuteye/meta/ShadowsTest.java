package net.ethx.shuteye.meta;

import com.google.common.base.Joiner;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.ethx.shuteye.util.Shadow;
import net.ethx.shuteye.util.Shadows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ShadowsTest {
    private static final int NOT_SHADOWABLE = Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.NATIVE;
    private static final int SHADOWABLE = Modifier.PUBLIC;
    private static final int SHADOWING = Modifier.PUBLIC | Modifier.STATIC;

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() throws IOException, ClassNotFoundException {
        final FastClasspathScanner scanner = new FastClasspathScanner("net.ethx.shuteye");
        scanner.scan();
        final List<String> annotated = scanner.getClassesWithAnnotation(Shadows.class);

        final Collection<Object[]> ret = new ArrayList<Object[]>();
        for (String clazz : annotated) {
            ret.add(new Object[] { Class.forName(clazz) });
        }

        return ret;
    }

    private final Class<?> classUnderTest;

    public ShadowsTest(final Class classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    @Test
    public void ensureAllMethodsShadowed() {
        final Shadows shadows = checkNotNull(classUnderTest.getAnnotation(Shadows.class));
        final Class<?> instance = checkNotNull(shadows.value());

        final List<String> failures = new ArrayList<String>();

        final List<Method> match = new ArrayList<Method>();
        for (Method method : instance.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Shadow.class)) {
                if ((method.getModifiers() & NOT_SHADOWABLE) != 0) {
                    failures.add(format("NOT_SHADOWABLE modifier failure on shadowable method: %s", method));
                } else if ((method.getModifiers() & SHADOWABLE) != SHADOWABLE) {
                    failures.add(format("SHADOWABLE modifier failure on shadowable method: %s", method));
                } else {
                    match.add(method);
                }
            }
        }

        for (Method shadowable : match) {
            boolean matched = false;
            for (Method candidate : classUnderTest.getDeclaredMethods()) {
                if (candidate.getName().equals(shadowable.getName())) {
                    if (Arrays.equals(shadowable.getParameterTypes(), candidate.getParameterTypes())) {
                        //  method match at this point - sanity check
                        if ((candidate.getModifiers() & SHADOWING) != SHADOWING) {
                            failures.add(format("Incorrect modifiers on shadowing method. Shadowable: %s, candidate: %s", shadowable, candidate));
                        } else if (!shadowable.getReturnType().equals(candidate.getReturnType())) {
                            failures.add(format("Mismatched return type on shadowing method. Shadowable: %s, candidate: %s", shadowable, candidate));
                        } else if (!Arrays.equals(shadowable.getTypeParameters(), candidate.getTypeParameters())) {
                            failures.add(format("Mismatched type parameters. Shadowable: %s, candidate: %s", shadowable, candidate));
                        } else {
                            matched |= true;
                        }
                    }
                }
            }

            if (!matched) {
                failures.add(format("No shadowing method found. Shadowable: %s", shadowable));
            }
        }

        final String report = Joiner.on("\n").join(failures);
        assertEquals("", report);
    }
}
