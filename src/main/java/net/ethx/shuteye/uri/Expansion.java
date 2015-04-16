package net.ethx.shuteye.uri;

import net.ethx.shuteye.util.Encodings;
import net.ethx.shuteye.util.Joiner;
import net.ethx.shuteye.util.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

class Expansion {
    private static final int NO_PREFIX = Integer.MAX_VALUE;

    private final Operator operator;
    private final String variable;
    private final boolean explode;
    private final int prefix;

    public static Expansion expansion(final Operator operator, final String variable) {
        return new Expansion(operator, variable, false, NO_PREFIX);
    }

    public static Expansion explode(final Operator operator, final String variable) {
        return new Expansion(operator, variable, true, NO_PREFIX);
    }

    public static Expansion prefix(final Operator operator, final String variable, final int prefixLength) {
        return new Expansion(operator, variable, false, prefixLength);
    }

    private Expansion(final Operator operator, final String variable, final boolean explode, final int prefix) {
        this.operator = operator;
        this.variable = variable;
        this.explode = explode;
        this.prefix = prefix;
    }

    String variable() {
        return variable;
    }

    public String expand(final Vars holder) {
        final Object value = holder.args().get(variable);
        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            return expandList((List) value);
        } else if (value instanceof Map) {
            return expandMap((Map) value);
        } else {
            return expandObject(value);
        }
    }

    private String expandObject(Object value) {
        final String stringified = value.toString();
        final String prefixed = stringified.substring(0, Math.min(prefix, stringified.length()));

        final StringBuilder ret = new StringBuilder();
        if (operator.isNamed()) {
            ret.append(variable);
            if (prefixed.isEmpty()) {
                ret.append(operator.getEmptyString());
            } else {
                ret.append('=');
            }
        }

        emitEscapedString(prefixed, ret);
        return ret.toString();
    }

    private String expandMap(final Map<?, ?> value) {
        Preconditions.checkArgument(prefix == NO_PREFIX, "Prefix value is not applicable to map expansion %s", this);
        if (value.isEmpty()) {
            return null;
        }

        final Set<? extends Map.Entry<?, ?>> entries = value.entrySet();
        final StringBuilder ret = new StringBuilder();
        if (explode) {
            if (operator.isNamed()) {
                int emitted = 0;
                for (Map.Entry<?, ?> entry : entries) {
                    if (entry.getValue() != null) {
                        if (emitted > 0) {
                            ret.append(operator.getSeparator());
                        }
                        emitEscapedString(entry.getKey().toString(), ret);
                        if (entry.getValue().toString().isEmpty()) {
                            ret.append(operator.getEmptyString());
                        } else {
                            ret.append('=');
                            emitEscapedString(entry.getValue().toString(), ret);
                        }

                        emitted++;
                    }
                }
            } else {
                int emitted = 0;
                for (Map.Entry<?, ?> entry : entries) {
                    if (entry.getValue() != null) {
                        if (emitted > 0) {
                            ret.append(operator.getSeparator());
                        }
                        emitEscapedString(entry.getKey().toString(), ret);
                        ret.append('=');
                        emitEscapedString(entry.getValue().toString(), ret);
                        emitted++;
                    }
                }
            }
        } else {
            emitNamePrefix(ret);

            int emitted = 0;
            for (Map.Entry<?, ?> entry : entries) {
                if (entry.getValue() != null) {
                    if (emitted > 0) {
                        ret.append(',');
                    }
                    emitEscapedString(entry.getKey().toString(), ret);
                    ret.append(',');
                    emitEscapedString(entry.getValue().toString(), ret);
                    emitted++;
                }
            }
        }

        return ret.length() == 0 ? null : ret.toString();
    }

    private String expandList(final List value) {
        Preconditions.checkArgument(prefix == NO_PREFIX, "Prefix value is not applicable to list expansions %s", this);
        if (value.isEmpty()) {
            return null;
        }

        final StringBuilder ret = new StringBuilder();
        if (explode) {
            int emitted = 0;
            for (Object o : value) {
                if (o != null) {
                    if (emitted > 0) {
                        ret.append(operator.getSeparator());
                    }

                    emitNamePrefix(ret);
                    emitEscapedString(o.toString(), ret);
                    emitted++;
                }
            }
        } else {
            emitNamePrefix(ret);
            ret.append(Joiner.join(value, ","));
        }
        return ret.length() == 0 ? null : ret.toString();
    }

    private void emitNamePrefix(final StringBuilder ret) {
        if (operator.isNamed()) {
            ret.append(variable);
            ret.append('=');
        }
    }

    protected void emitEscapedString(final String value, final StringBuilder out) {
        final byte[] bs = value.getBytes(Encodings.UTF8);
        for (byte b : bs) {
            final char c = (char) b;
            if (UriTemplateConstants.CharacterSets.UNRESERVED.contains(c) || (UriTemplateConstants.CharacterSets.RESERVED.contains(c) && operator.allowReserved())) {
                out.append(c);
            } else {
                out.append('%');
                out.append(Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16)));
                out.append(Character.toUpperCase(Character.forDigit(b & 0xF, 16)));
            }
        }
    }

    @Override
    public String toString() {
        return format("Expansion [operator=%s, variable=%s, explode=%s, prefix=%s]", operator, variable, explode, prefix);
    }
}
