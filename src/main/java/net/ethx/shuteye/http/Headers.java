package net.ethx.shuteye.http;

import java.util.*;

public class Headers {
    private final Map<String, List<String>> headers;

    public Headers(final Map<String, List<String>> headers) {
        this.headers = new LinkedHashMap<String, List<String>>(headers);
    }

    public String first(final String header) {
        final List<String> values = headers.get(header);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    public List<String> all(final String header) {
        final List<String> values = headers.get(header);
        return values == null ? Collections.<String>emptyList() : Collections.unmodifiableList(values);
    }

    public Set<String> headerNames() {
        return Collections.unmodifiableSet(headers.keySet());
    }

    protected void add(final String header, final String value) {
        List<String> values = headers.get(header);
        if (values == null) {
            headers.put(header, values = new ArrayList<String>());
        }
        values.add(value);
    }
}
