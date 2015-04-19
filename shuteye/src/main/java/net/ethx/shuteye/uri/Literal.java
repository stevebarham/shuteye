package net.ethx.shuteye.uri;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

class Literal implements Emittable {
    private final String literal;

    public Literal(final String literal) {
        this.literal = literal;
    }

    @Override
    public void emit(final Vars holder, final StringBuilder out) {
        out.append(literal);
    }

    @Override
    public List<String> variableNames() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return format("Literal [literal=\"%s\"]", literal);
    }
}
