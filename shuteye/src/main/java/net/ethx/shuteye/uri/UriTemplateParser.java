package net.ethx.shuteye.uri;

import net.ethx.shuteye.http.except.TemplateException;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class UriTemplateParser {
    static List<Emittable> parse(final String input) {
        final List<Emittable> ret = new ArrayList<Emittable>();

        final CharBuffer buffer = CharBuffer.wrap(input);
        final StringBuilder literal = new StringBuilder();
        while (buffer.hasRemaining()) {
            final char c = buffer.get();
            if (c == '{') {
                if (literal.length() > 0) {
                    ret.add(new Literal(literal.toString()));
                    literal.setLength(0);
                }

                ret.add(parseExpression(buffer));
            } else if (c != '}') {
                literal.append(c);
            } else {
                fail(buffer, "Unexpected character");
            }
        }

        if (literal.length() > 0) {
            ret.add(new Literal(literal.toString()));
        }

        return ret;
    }

    private static Emittable parseExpression(final CharBuffer buffer) {
        final Operator operator = Operator.peek(buffer);

        final List<Expansion> expansions = new ArrayList<Expansion>();

        boolean exit = false;
        while (!exit && buffer.hasRemaining()) {
            final StringBuilder variable = new StringBuilder();
            boolean explode = false;
            boolean prefix = false;
            int prefixLength = Integer.MIN_VALUE;
            while (buffer.hasRemaining()) {
                final char c = buffer.get();
                if (c == '}') {
                    exit = true;
                    break;
                } else if (c == ',') {
                    break;
                } else if (c == ':') {
                    prefix = true;
                    prefixLength = parseLimit(buffer);
                } else if (c == '*') {
                    explode = true;
                } else if (UriTemplateConstants.CharacterSets.VARCHAR.contains(c)) {
                    variable.append(c);
                } else if (c == '%') {
                    variable.append(parsePercentEncoded(buffer));
                } else if (c == '.' && variable.length() > 0 && buffer.hasRemaining() && UriTemplateConstants.CharacterSets.VARCHAR.contains(buffer.charAt(1))) {
                    variable.append(c);
                } else {
                    fail(buffer, "Character %s is not allowed at this point", c);
                }
            }

            if (prefix && explode) {
                fail(buffer, "Exploded and prefixed expressions are not allowed");
            }

            final String variableName = variable.toString();
            if (prefix) {
                expansions.add(Expansion.prefix(operator, variableName, prefixLength));
            } else if (explode) {
                expansions.add(Expansion.explode(operator, variableName));
            } else {
                expansions.add(Expansion.expansion(operator, variableName));
            }
        }

        if (!exit) {
            fail(buffer, "Encountered end-of-input without closing expression");
        }

        return new Expression(operator, expansions);
    }

    private static String parsePercentEncoded(final CharBuffer buffer) {
        final StringBuilder ret = new StringBuilder();
        ret.append('%');

        for (int i = 0; i < 2; i++) {
            if (!buffer.hasRemaining()) {
                fail(buffer, "Expected hex-digit, found end of input");
            }
            final char c = buffer.get();
            if (UriTemplateConstants.CharacterSets.HEXDIG.contains(c)) {
                ret.append(c);
            } else {
                fail(buffer, "Expected hex-digit, found %s", c);
            }
        }

        return ret.toString();
    }

    private static int parseLimit(final CharBuffer buffer) {
        final char first = buffer.get();
        if (!Character.isDigit(first)) {
            fail(buffer, "Expected digit, found %s", first);
        }

        int acc = Character.digit(first, 10);
        while (buffer.hasRemaining() && Math.ceil(Math.log10(acc)) < 4) {
            final char next = buffer.charAt(0);
            if (Character.isDigit(next)) {
                acc *= 10;
                acc += Character.digit(next, 10);
                buffer.position(buffer.position() + 1);
            } else {
                break;
            }
        }

        return acc;
    }

    private static void fail(final CharBuffer buffer, final String message, final Object... args) {
        final char[] pointer = new char[buffer.position()];
        Arrays.fill(pointer, '-');
        pointer[pointer.length - 1] = '^';

        final CharBuffer rewound = buffer.duplicate();
        rewound.position(0);

        throw new TemplateException(String.format(message, args) + "\n" + rewound.toString() + "\n" + new String(pointer) + "\n");
    }

}
