package net.assimilationmc.ellie.assicore.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UtilMath {

    static DecimalFormat twoDPlaces = new DecimalFormat("#,###.##");

    public static String formatDouble(final double value) {
        twoDPlaces.setRoundingMode(RoundingMode.HALF_UP);
        return twoDPlaces.format(value);
    }

    public static double percentage(double part, double total) {
        return percentage(part, total, 1);
    }

    public static double percentage(double part, double total, int degree) {
        return part != 0.0D && total != 0.0D ? trim(part / total * 100.0D, degree) : 0.0D;
    }

    public static double trim(double d) {
        return trim(d, 1);
    }

    public static double trim(double d, int degree) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            d = 0.0D;
        }

        String format = "#.#";

        for(int i = 1; i < degree; ++i) {
            format = format + "#";
        }

        try {
            return Double.valueOf((new DecimalFormat(format)).format(d));
        } catch (NumberFormatException var5) {
            return d;
        }
    }

    public static boolean isInteger(String a) {
        try {
            Integer.parseInt(a);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isDouble(String a) {
        try {
            Double.parseDouble(a);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isEven(int num) {
        return (num & 1) == 0;
    }

    public static boolean isOdd(int num) {
        return (num & 1) == 1;
    }


}
