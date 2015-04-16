package net.ethx.shuteye.uri;

import java.util.List;

interface Emittable {
    void emit(final Vars holder, final StringBuilder out);

    List<String> variableNames();
}
