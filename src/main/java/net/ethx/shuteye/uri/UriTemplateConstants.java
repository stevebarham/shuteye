package net.ethx.shuteye.uri;

import net.ethx.shuteye.util.CharacterSet;

abstract class UriTemplateConstants {
    static abstract class CharacterSets {
        public static final CharacterSet ALPHA = CharacterSet.range('a', 'z').union(CharacterSet.range('A', 'Z'));
        public static final CharacterSet DIGIT = CharacterSet.range('0', '9');
        public static final CharacterSet VARCHAR = ALPHA.union(DIGIT).union(CharacterSet.of('_'));
        public static final CharacterSet UNRESERVED = ALPHA.union(DIGIT).union(CharacterSet.of('-', '.', '_', '~'));
        public static final CharacterSet HEXDIG = DIGIT.union(CharacterSet.range('A', 'F')).union(CharacterSet.range('a', 'f'));
        public static final CharacterSet GEN_DELIMS = CharacterSet.of(':', '/', '?', '#', '[', ']', '@');
        public static final CharacterSet SUB_DELIMS = CharacterSet.of('!', '$', '&', '’', '(', ')', '*', '+', ',', ';', '=');
        public static final CharacterSet RESERVED = GEN_DELIMS.union(SUB_DELIMS);
    }

}
