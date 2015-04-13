package net.ethx.shuteye.uri;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

@RunWith(Parameterized.class)
public class UriTemplateTest {
    public static final String SPEC_ROOT = "uritemplate-test";

    private final UriTemplateTestCase testcase;

    public UriTemplateTest(final UriTemplateTestCase testcase) {
        this.testcase = testcase;
    }

    @Test
    public void test() throws Exception {
        final boolean expectException = testcase.expansions.isEmpty();
        final String result;
        try {
            result = UriTemplate.process(testcase.template, testcase.variables);
            if (expectException) {
                Assert.fail(describe(result) + "Expansion set empty, expected exception");
            } else {
                Assert.assertTrue(describe(result) + "Expected result to be in expansions: " + testcase.expansions, testcase.expansions.contains(result));
            }
        } catch (Exception e) {
            if (!expectException) {
                Assert.fail(describe(e.getMessage()) + ": Received exception, expected result in expansions: " + testcase.expansions);
            }
        }
    }

    String describe(final String result) {
        return "Suite: " + testcase.suite + "\n" +
                "Level: " + testcase.level + "\n" +
                "Template: " + testcase.template + "\n" +
                "Result: " + result + "\n";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() throws IOException {
        final File specRoot = new File(SPEC_ROOT);
        if (!specRoot.exists() || !specRoot.isDirectory()) {
            throw new IllegalStateException("Could not find specifications in " + SPEC_ROOT);
        }

        final File[] specs = specRoot.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".json");
            }
        });
        if (specs == null || specs.length == 0) {
            throw new IllegalStateException("Found specification directory " + SPEC_ROOT + ", but no specifications found within");
        }

        final List<UriTemplateTestCase> cases = new ArrayList<UriTemplateTestCase>();
        for (File spec : specs) {
            visitSpec(cases, spec);
        }

        final List<Object[]> ret = new ArrayList<Object[]>();
        for (UriTemplateTestCase aCase : cases) {
            ret.add(new Object[]{aCase});
        }
        return ret;
    }

    static void visitSpec(final List<UriTemplateTestCase> cases, final File spec) throws IOException {
        final JsonNode root = new ObjectMapper().readTree(spec);
        final Iterator<String> fields = root.fieldNames();
        while (fields.hasNext()) {
            final String field = fields.next();
            visitSuite(cases, field, root.get(field));
        }
    }

    private static void visitSuite(final List<UriTemplateTestCase> cases, final String suite, final JsonNode root) {
        final JsonNode levelNode = root.get("level");
        final int level = levelNode == null ? 0 : levelNode.intValue();

        final Map<String, Object> variables = new HashMap<String, Object>();
        Iterator<Map.Entry<String, JsonNode>> varIterator = root.get("variables").fields();
        while (varIterator.hasNext()) {
            final Map.Entry<String, JsonNode> variable = varIterator.next();
            final String name = variable.getKey();
            final JsonNode value = variable.getValue();
            if (value.isTextual() || value.isNumber()) {
                visitTextyVariable(variables, name, value);
            } else if (value.isArray()) {
                visitArrayVariable(variables, name, value);
            } else if (value.isObject()) {
                visitObjectVariable(variables, name, value);
            } else if (!value.isNull()) {
                throw new IllegalStateException("Unknown variable declaration " + name + " : " + value);
            }
        }

        final Iterator<JsonNode> caseIterator = root.get("testcases").elements();
        while (caseIterator.hasNext()) {
            visitCase(cases, suite, level, variables, caseIterator.next());
        }
    }

    private static void visitCase(final List<UriTemplateTestCase> cases, final String suite, final int level, final Map<String, Object> variables, final JsonNode node) {
        final String template = node.get(0).textValue();

        final JsonNode expansionsNode = node.get(1);
        final Set<String> expansions;
        if (expansionsNode.isTextual()) {
            expansions = Collections.singleton(expansionsNode.textValue());
        } else if (expansionsNode.isArray()) {
            expansions = new HashSet<String>();
            for (int i = 0; i < expansionsNode.size(); i++) {
                expansions.add(expansionsNode.get(i).textValue());
            }
        } else if (expansionsNode.isBoolean()) {
            expansions = Collections.emptySet();
        } else {
            throw new IllegalStateException("Unknown expansion node " + expansionsNode);
        }

        cases.add(new UriTemplateTestCase(suite, level, variables, template, expansions));
    }

    private static void visitObjectVariable(final Map<String, Object> variables, final String name, final JsonNode value) {
        final Map<String, String> map = new HashMap<String, String>();

        final Iterator<Map.Entry<String, JsonNode>> iterator = value.fields();
        while (iterator.hasNext()) {
            final Map.Entry<String, JsonNode> field = iterator.next();
            if (!field.getValue().isTextual()) {
                throw new IllegalStateException("Unknown key:value mapping in variable " + name + ", field " + field.getKey() + ": " + field.getValue());
            }
            map.put(field.getKey(), field.getValue().textValue());
        }

        variables.put(name, map);
    }

    private static void visitArrayVariable(final Map<String, Object> variables, final String name, final JsonNode value) {
        final List<String> array = new ArrayList<String>();
        for (int i = 0; i < value.size(); i++) {
            array.add(value.get(i).textValue());
        }
        variables.put(name, array);
    }

    private static void visitTextyVariable(final Map<String, Object> variables, final String name, final JsonNode value) {
        variables.put(name, value.asText());
    }

    static class UriTemplateTestCase {
        private final String suite;
        private final int level;
        private final Map<String, Object> variables;
        private final String template;
        private final Set<String> expansions;

        public UriTemplateTestCase(final String suite, final int level, final Map<String, Object> variables, final String template, final Set<String> expansions) {
            this.suite = suite;
            this.level = level;
            this.variables = Collections.unmodifiableMap(variables);
            this.template = template;
            this.expansions = Collections.unmodifiableSet(expansions);
        }
    }
}
