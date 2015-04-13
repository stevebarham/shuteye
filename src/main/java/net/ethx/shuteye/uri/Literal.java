package net.ethx.shuteye.uri;

import static java.lang.String.format;

class Literal implements Emittable {
    private final String literal;

    public Literal(final String literal) {
        this.literal = literal;
    }

    @Override
    public void emit(final Context context, final StringBuilder out) {
        out.append(literal);
    }

    @Override
    public String toString() {
        return format("Literal [literal=\"%s\"]", literal);
    }
}
