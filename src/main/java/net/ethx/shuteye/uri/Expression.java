package net.ethx.shuteye.uri;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

class Expression implements Emittable {
    private final Operator operator;
    private final List<Expansion> expansions;

    public Expression(final Operator operator, final List<Expansion> expansions) {
        this.operator = operator;
        this.expansions = Collections.unmodifiableList(expansions);
    }

    @Override
    public void emit(final Context context, final StringBuilder out) {
        int expansionCount = 0;
        for (Expansion expansion : expansions) {
            final String result = expansion.expand(context);
            if (result != null) {
                if (expansionCount++ == 0) {
                    out.append(operator.getFirst());
                } else {
                    out.append(operator.getSeparator());
                }
                out.append(result);
            }
        }
    }

    @Override
    public String toString() {
        return format("Expression [operator=%s, expansions=%s]", operator, expansions);
    }
}
