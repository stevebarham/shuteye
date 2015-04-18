package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.except.TemplateException;
import net.ethx.shuteye.http.request.BaseUri;
import net.ethx.shuteye.util.Shadows;

import java.util.*;

import static java.lang.String.format;
import static net.ethx.shuteye.uri.UriTemplateOption.*;

@Shadows(UriTemplateContext.class)
public class UriTemplate {
    public static BaseUri create(final String template, final Object... vars) throws IllegalStateException, IllegalArgumentException {
        return UriTemplateContext.defaultContext().compile(template).createUri(vars);
    }

    public static BaseUri create(final String template, final Vars vars) throws IllegalStateException, IllegalArgumentException {
        return UriTemplateContext.defaultContext().compile(template).createUri(vars);
    }

    public static String process(final String template, final Object... vars) throws IllegalStateException, IllegalArgumentException {
        return UriTemplateContext.defaultContext().compile(template).process(vars);
    }

    public static String process(final String template, final Vars vars) throws IllegalStateException, IllegalArgumentException {
        return UriTemplateContext.defaultContext().compile(template).process(vars);
    }

    public static UriTemplate compile(final String template) throws IllegalStateException, IllegalArgumentException {
        return UriTemplateContext.defaultContext().compile(template);
    }

    private final UriTemplateContext context;
    private final List<Emittable> emittables;
    private final List<String> variableNames;

    UriTemplate(final UriTemplateContext context, final List<Emittable> emittables) {
        this.context = context;
        this.emittables = Collections.unmodifiableList(emittables);

        final List<String> variableNamesBuilder = new ArrayList<String>();
        for (Emittable emittable : emittables) {
            variableNamesBuilder.addAll(emittable.variableNames());
        }
        this.variableNames = Collections.unmodifiableList(variableNamesBuilder);
    }

    List<String> variableNames() {
        return variableNames;
    }

    public <T> UriTemplate with(final UriTemplateOption<T> option, final T value) {
        return new UriTemplate(context.with(option, value), emittables);
    }

    public String process(final Object... vars) {
        final Map<String, Object> variables = new LinkedHashMap<String, Object>(variableNames.size());
        int i = 0;
        for (String variableName : variableNames) {
            variables.put(variableName, i < vars.length ? vars[i] : null);
            i++;
        }

        if (vars.length < variableNames.size() && !ALLOW_MISSING_VARS_IN_VARARG.get(context.options())) {
            throw new TemplateException(format("!%s: Not enough template variables provided. Required %s, found %s. Variables: %s", ALLOW_MISSING_VARS_IN_VARARG, variableNames, variableNames.subList(0, vars.length), variables));
        }

        if (vars.length > variableNames.size() && !ALLOW_EXTRA_VARS_IN_VARARG.get(context.options())) {
            final Object[] remainder = new Object[vars.length - variableNames.size()];
            System.arraycopy(vars, variableNames.size(), remainder, 0, vars.length - variableNames.size());
            throw new TemplateException(format("!%s: Too many template variables provided. Required: %s, extra variables: %s", ALLOW_EXTRA_VARS_IN_VARARG, variableNames, Arrays.toString(remainder)));
        }

        return processValidatedVariables(variables);
    }

    public String process(final Vars vars) {
        final Map<String, ?> variables = vars.args();
        if (!ALLOW_MISSING_VARS_IN_MAP.get(context.options())) {
            final Collection<String> missing = new ArrayList<String>();
            for (String variableName : variableNames) {
                if (!variables.containsKey(variableName)) {
                    missing.add(variableName);
                }
            }

            if (!missing.isEmpty()) {
                throw new TemplateException(format("!%s: Not enough template variables provided. Required: %s, provided: %s", ALLOW_MISSING_VARS_IN_MAP, variableNames, variables));
            }
        }

        if (!ALLOW_EXTRA_VARS_IN_MAP.get(context.options())) {
            final Set<String> remaining = new HashSet<String>(variables.keySet());
            for (String variableName : variableNames) {
                remaining.remove(variableName);
            }

            if (!remaining.isEmpty()) {
                throw new TemplateException(format("!%s: Too many template variables provided. Required: %s, provided: %s, extra variables: %s", ALLOW_EXTRA_VARS_IN_MAP, variableNames, variables, remaining));
            }
        }

        return processValidatedVariables(variables);
    }

    private String processValidatedVariables(final Map<String, ?> variables) {
        final StringBuilder out = new StringBuilder();

        final Vars holder = new Vars(variables);
        for (Emittable emittable : emittables) {
            emittable.emit(holder, out);
        }

        return out.toString();
    }

    public BaseUri createUri(final Object... args) {
        return new BaseUri(DefaultShuteyeContext.get(context.options()), process(args));
    }

    public BaseUri createUri(final Map<String, ?> variables) {
        return new BaseUri(DefaultShuteyeContext.get(context.options()), process(variables));
    }
}