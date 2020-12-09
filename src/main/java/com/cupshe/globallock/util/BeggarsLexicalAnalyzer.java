package com.cupshe.globallock.util;

/**
 * BeggarsLexicalAnalyzer
 * <p>It is only used for analysis variables, not handling keywords, complex character types.
 *
 * @author zxy
 */
class BeggarsLexicalAnalyzer {

    static Kvs getResult(String key) {
        if (key == null) {
            return Kvs.EMPTY;
        }

        Kvs result = new Kvs();
        char c;

        for (int i = 0, length = key.length(); i < length; i++) {
            c = key.charAt(i);
            if (isLetter(c)) {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && isLetterOrDigit(c = key.charAt(i))) {
                    sbr.append(c);
                }

                result.add(new Kv(SimpleFiniteState.VARIABLE, sbr.toString()));
                i--;
            } else if (c == '\'') {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && (c = key.charAt(i)) != '\'') {
                    sbr.append(c);
                }

                result.add(new Kv(SimpleFiniteState.VARCHAR, sbr.append(c).toString()));
            } else if (c == '"') {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && (c = key.charAt(i)) != '"') {
                    sbr.append(c);
                }

                result.add(new Kv(SimpleFiniteState.VARCHAR, sbr.append(c).toString()));
            } else if (Character.isDigit(c)) {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && Character.isDigit(c = key.charAt(i))) {
                    sbr.append(c);
                }

                // number separation and decimals are not handled
                result.add(new Kv(SimpleFiniteState.DIGIT, sbr.toString()));
                i--;
            } else if (c == '.') {
                StringBuilder sbr = new StringBuilder(1 << 2).append(c);
                while (++i < length && Character.isWhitespace(c = key.charAt(i))) {
                    sbr.append(c);
                }

                sbr.append(c);
                if (c == '(') {
                    i = append(i, length, key, sbr, ')');
                } else if (c == '[') {
                    i = append(i, length, key, sbr, ']');
                } else if (c == '{') {
                    i = append(i, length, key, sbr, '}');
                } else {
                    if (++i < length && isLetter(c = key.charAt(i))) {
                        sbr.append(c);
                        while (++i < length && isLetterOrDigit(c = key.charAt(i))) {
                            sbr.append(c);
                        }
                    }
                }

                result.add(new Kv(SimpleFiniteState.OTHER, sbr.append(c).toString()));
            } else if (Character.isWhitespace(c)) {
                result.add(new Kv(SimpleFiniteState.OTHER, String.valueOf(c)));
            } else {
                result.add(new Kv(SimpleFiniteState.OTHER, String.valueOf(c)));
            }
        }

        return result;
    }

    private static boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_' || c == '$';
    }

    private static boolean isLetterOrDigit(char c) {
        return isLetter(c) || Character.isDigit(c);
    }

    private static int append(int i, int length, String key, StringBuilder sbr, char sp) {
        char c;
        while (++i < length && (c = key.charAt(i)) != sp) {
            sbr.append(c);
        }

        return i;
    }

    /**
     * SimpleFiniteState
     */
    enum SimpleFiniteState {
        VARIABLE, // variable  e.g. name
        VARCHAR,  // varchar   e.g. 'name' or "name"
        DIGIT,    // numbers   e.g. 10
        OTHER;    // others    e.g. empty/clause/delimiter/operator...

        boolean isVariable() {
            return VARIABLE.equals(this);
        }
    }
}
