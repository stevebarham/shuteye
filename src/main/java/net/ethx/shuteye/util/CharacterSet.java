package net.ethx.shuteye.util;


import java.util.BitSet;

import static net.ethx.shuteye.util.Preconditions.checkArgument;

public abstract class CharacterSet {
    public abstract boolean contains(final char candidate);

    public CharacterSet union(final CharacterSet that) {
        return new Union(this, that);
    }

    public static CharacterSet of(final char... chars) {
        final BitSet characters = new BitSet();
        for (char c : chars) {
            characters.set(c);
        }
        return new Group(characters);
    }

    public static CharacterSet range(final int lower, final int upper) {
        checkArgument(lower <= upper, "Lower character of '%s' is not <= upper character of '%s'");
        return new Range(lower, upper);
    }

    static class Range extends CharacterSet {
        private final int lower;
        private final int upper;

        public Range(final int lower, final int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public boolean contains(final char candidate) {
            return lower <= candidate && candidate <= upper;
        }
    }

    static class Group extends CharacterSet {
        private final BitSet characters;

        public Group(final BitSet characters) {
            this.characters = characters;
        }

        @Override
        public boolean contains(final char candidate) {
            return characters.get(candidate);
        }
    }

    static class Union extends CharacterSet {
        private final CharacterSet left;
        private final CharacterSet right;

        public Union(final CharacterSet left, final CharacterSet right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean contains(final char candidate) {
            return left.contains(candidate) || right.contains(candidate);
        }
    }

}
