package net.assimilationmc.assicore.util;

public class UtilString {

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

}
