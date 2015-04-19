package net.ethx.shuteye.http.request;

import net.ethx.shuteye.http.Headers;

import java.util.List;
import java.util.TreeMap;

class MutableHeaders extends Headers {
    public MutableHeaders() {
        super(new TreeMap<String, List<String>>());
    }

    @Override
    public void add(final String header, final String value) {
        super.add(header, value);
    }
}
