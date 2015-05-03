package net.ethx.shuteye.http;

import java.util.*;

/**
 * Class representing a set of headers which are either sent to a remote server in an HTTP request, or
 * received from a remote server in an HTTP response.
 * <p/>
 * This class is not safe for concurrent access via multiple threads.
 */
public class Headers {
    private final Map<String, List<String>> headers;
    private final Map<String, String> normalizedKeys = new HashMap<String, String>();

    public Headers(final Map<String, List<String>> headers) {
        this.headers = new LinkedHashMap<String, List<String>>(headers);
        for (String header : headers.keySet()) {
            if (header != null) {
                this.normalizedKeys.put(header.toLowerCase(), header);
            }
        }
    }

    /**
     * @param header Header name to retrieve
     * @return The first typedResponse for the specified header in the collection, or null if no such values exist.
     */
    public String first(final String header) {
        final List<String> values = headers.get(normalizedKey(header));
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    /**
     * @param header Header name to retrieve
     * @return All configured values for the specified header in the collection, or an empty list if no such values exist.
     */
    public List<String> all(final String header) {
        final List<String> values = headers.get(normalizedKey(header));
        return values == null ? Collections.<String>emptyList() : Collections.unmodifiableList(values);
    }

    /**
     * @return Header names in this collection
     */
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

    private String normalizedKey(final String header) {
        final String key = normalizedKeys.get(header.toLowerCase());
        return key == null ? header : key;
    }
}
