package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.request.RequestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UriTemplate {
    private final List<Emittable> emittables;

    public static RequestBuilder build(final String input, final Map<String, ?> variables) throws IllegalStateException, IllegalArgumentException {
        return parse(input).build(variables);
    }

    public static String process(final String input, final Map<String, ?> variables) throws IllegalStateException, IllegalArgumentException {
        return parse(input).process(variables);
    }

    public static UriTemplate parse(final String input) throws IllegalStateException, IllegalArgumentException {
        return new UriTemplate(UriTemplateParser.parse(input));
    }

    UriTemplate(final List<Emittable> emittables) {
        this.emittables = Collections.unmodifiableList(emittables);
    }

    public String process(final Map<String, ?> variables) {
        final StringBuilder out = new StringBuilder();

        final Context context = new Context(variables);
        for (Emittable emittable : emittables) {
            emittable.emit(context, out);
        }

        return out.toString();
    }

    public RequestBuilder build(final Map<String, ?> variables) {
        return new RequestBuilder(process(variables));
    }
}
