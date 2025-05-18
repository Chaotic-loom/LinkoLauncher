package com.chaotic_loom.util;

public class StringBuilderHelper {
    private static final StringBuilder sb = new StringBuilder();

    /**
     * Concatenate the given strings using the shared StringBuilder.
     *
     * @param parts Strings to be concatenated.
     * @return Concatenated result.
     */
    public static String concatenate(String... parts) {
        sb.setLength(0);

        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
        }

        return sb.toString();
    }

    /**
     * Concatenate the given objects' string representations using the shared StringBuilder.
     *
     * @param parts Objects to be concatenated.
     * @return Concatenated result.
     */
    public static String concatenate(Object... parts) {
        sb.setLength(0);

        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
        }

        return sb.toString();
    }
}
