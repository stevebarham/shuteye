package net.ethx.shuteye.uri;

import java.nio.CharBuffer;
import java.util.EnumSet;

@SuppressWarnings("UnusedDeclaration")
enum Operator {
    PLUS("+", "", ",", false, "", true),
    DOT(".", ".", ".", false, "", false),
    SLASH("/", "/", "/", false, "", false),
    SEMI(";", ";", ";", true, "", false),
    QUERY("?", "?", "&", true, "=", false),
    AMPERSAND("&", "&", "&", true, "=", false),
    HASH("#", "#", ",", false, "", true),
    NUL("", "", ",", false, "", false);

    public static final EnumSet<Operator> SYMBOLIC_OPERATORS = EnumSet.complementOf(EnumSet.of(NUL));

    private final String token;
    private final String first;
    private final String separator;
    private final boolean named;
    private final String emptyString;
    private final boolean allowReserved;

    Operator(final String token, final String first, final String separator, final boolean named, final String emptyString, final boolean allowReserved) {
        this.token = token;
        this.first = first;
        this.separator = separator;
        this.named = named;
        this.emptyString = emptyString;
        this.allowReserved = allowReserved;
    }

    public String getFirst() {
        return first;
    }

    public String getSeparator() {
        return separator;
    }

    public boolean isNamed() {
        return named;
    }

    public String getEmptyString() {
        return emptyString;
    }

    public boolean allowReserved() {
        return allowReserved;
    }

    public static Operator peek(final CharBuffer buffer) {
        final int position = buffer.position();
        final String token = String.valueOf(buffer.get());
        for (Operator operator : SYMBOLIC_OPERATORS) {
            if (operator.token.equals(token)) {
                return operator;
            }
        }

        buffer.position(position);
        return NUL;
    }
}
