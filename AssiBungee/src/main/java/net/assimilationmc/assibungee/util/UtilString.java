package net.assimilationmc.assibungee.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UtilString {

    private static final String ALPHABET = "123456789abcdefghjkmnpqrstuvwxyz";

    /**
     * Get the final argument of a string.
     *
     * @param args  The arguments to build the string from.
     * @param start The starting index.
     * @return The built string.
     */
    public static String getFinalArg(final String[] args, final int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        final String msg = sb.toString();
        sb.setLength(0);
        return msg;
    }

    /**
     * Generate a random string at a certain length.
     *
     * @param length The length of the random string.
     * @return The random string.
     */
    public static String generateRandomString(int length) {
        return IntStream.range(0, length)
                .map(i -> ThreadLocalRandom.current().nextInt(ALPHABET.length()))
                .mapToObj(i -> ALPHABET.substring(i, i + 1))
                .collect(Collectors.joining());
    }

    /**
     * Set the first character in a string to uppercase.
     *
     * @param string The string to modify.
     * @return The capitalized string.
     */
    public static String capitalize(String string) {
        if (string != null && string.length() != 0) {
            char fChar = string.charAt(0);
            return Character.isTitleCase(fChar) ? string : (new StringBuilder(string.length()))
                    .append(Character.toTitleCase(fChar)).append(string.substring(1)).toString();
        }
        return string;

    }

}
