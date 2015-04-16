package net.ethx.shuteye.uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

class Expression implements Emittable {
    private final Operator operator;
    private final List<Expansion> expansions;
    private final List<String> variableNames;

    public Expression(final Operator operator, final List<Expansion> expansions) {
        this.operator = operator;
        this.expansions = Collections.unmodifiableList(expansions);

        final List<String> names = new ArrayList<String>(expansions.size());
        for (Expansion expansion : expansions) {
            names.add(expansion.variable());
        }
        this.variableNames = Collections.unmodifiableList(names);
    }

    @Override
    public void emit(final Vars holder, final StringBuilder out) {
        int expansionCount = 0;
        for (Expansion expansion : expansions) {
            final String result = expansion.expand(holder);
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
    public List<String> variableNames() {
        return variableNames;
    }

    @Override
    public String toString() {
        return format("Expression [operator=%s, expansions=%s]", operator, expansions);
    }
}
