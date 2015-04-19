package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.except.TemplateException;

import java.util.*;

import static java.lang.String.format;

public class UriTemplate {
    private final UriTemplateCompiler compiler;
    private final List<Emittable> emittables;
    private final List<String> variableNames;

    UriTemplate(final UriTemplateCompiler compiler, final List<Emittable> emittables) {
        this.compiler = compiler;
        this.emittables = Collections.unmodifiableList(emittables);

        final List<String> variableNamesBuilder = new ArrayList<String>();
        for (Emittable emittable : emittables) {
            variableNamesBuilder.addAll(emittable.variableNames());
        }
        this.variableNames = Collections.unmodifiableList(variableNamesBuilder);
    }

    public String process(final Object... vars) {
        final Map<String, Object> variables = new LinkedHashMap<String, Object>(variableNames.size());
        int i = 0;
        for (String variableName : variableNames) {
            variables.put(variableName, i < vars.length ? vars[i] : null);
            i++;
        }

        if (vars.length < variableNames.size() && !compiler.config().isAllowMissingVarsInVararg()) {
            throw new TemplateException(format("!isAllowMissingVarsInVararg(): Not enough template variables provided. Required %s, found %s. Variables: %s", variableNames, variableNames.subList(0, vars.length), variables));
        }

        if (vars.length > variableNames.size() && !compiler.config().isAllowExtraVarsInVararg()) {
            final Object[] remainder = new Object[vars.length - variableNames.size()];
            System.arraycopy(vars, variableNames.size(), remainder, 0, vars.length - variableNames.size());
            throw new TemplateException(format("!isAllowExtraVarsInVararg(): Too many template variables provided. Required: %s, extra variables: %s", variableNames, Arrays.toString(remainder)));
        }

        return processValidatedVariables(variables);
    }

    public String process(final Vars vars) {
        final Map<String, ?> variables = vars.args();
        if (!compiler.config().isAllowMissingVarsInMap()) {
            final Collection<String> missing = new ArrayList<String>();
            for (String variableName : variableNames) {
                if (!variables.containsKey(variableName)) {
                    missing.add(variableName);
                }
            }

            if (!missing.isEmpty()) {
                throw new TemplateException(format("!isAllowMissingVarsInMap(): Not enough template variables provided. Required: %s, provided: %s", variableNames, variables));
            }
        }

        if (!compiler.config().isAllowExtraVarsInMap()) {
            final Set<String> remaining = new HashSet<String>(variables.keySet());
            for (String variableName : variableNames) {
                remaining.remove(variableName);
            }

            if (!remaining.isEmpty()) {
                throw new TemplateException(format("!isAllowExtraVarsInMap(): Too many template variables provided. Required: %s, provided: %s, extra variables: %s", variableNames, variables, remaining));
            }
        }

        return processValidatedVariables(variables);
    }

    private List<String> variableNames() {
        return variableNames;
    }

    private String processValidatedVariables(final Map<String, ?> variables) {
        final StringBuilder out = new StringBuilder();

        final Vars holder = new Vars(variables);
        for (Emittable emittable : emittables) {
            emittable.emit(holder, out);
        }

        return out.toString();
    }
}