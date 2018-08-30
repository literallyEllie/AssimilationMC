package net.assimilationmc.assicore.util;

import com.google.common.base.Preconditions;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class UtilMath {

    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    /**
     * Convert a byte count to a kilobyte count.
     *
     * @param bytes The amount of bytes.
     * @return kilobyte equivalent size to the input.
     */
    public static long bToKb(long bytes) {
        return Math.round(bytes / 1024);
    }

    /**
     * Convert a byte count to a megabyte count.
     *
     * @param bytes The amount of bytes.
     * @return megabyte equivalent size to the input.
     */
    public static long bToMb(long bytes) {
        return Math.round(bytes / 1024 / 1024);
    }

    /**
     * Compares x to y.
     *
     * @param x before number.
     * @param y new number.
     * @return 0 if unchanged,
     * 1 if x is greater than y
     * 2 if y is greater than x
     */
    public static int compare(int x, int y) {
        if (x == y) return 0;
        else if (x > y) return 1;
        else return 2;
    }

    /**
     * Calculate a pretty percentage of two numbers.
     *
     * @param a     smaller number
     * @param outOf larger number
     * @return (a / outOf) * 100 then trimmed.
     * if a or outOf is 0, it will return 0
     */
    public static String prettyPercentage(int a, int outOf) {
        return trim(percentage(a, outOf) * 100);
    }

    /**
     * Calculate a calculation percentage of two numbers.
     *
     * @param a     smaller number
     * @param outOf larger number
     * @return a / outOf
     * if a or outOf is 0, it will return 0
     */
    public static float percentage(int a, int outOf) {
        if (a == 0 || outOf == 0) return 0;
        return (float) a / (float) outOf;
    }

    public static String trim(float a) {
        return decimalFormat.format(a);
    }

    /**
     * Generates a circumference represented by locations.
     *
     * @param center    the center location.
     * @param increment 360 / increment = how many locations will be stored.
     * @return an array of locations.
     */
    public static Location[] generateCircmerfence(Location center, int increment) {
        Preconditions.checkArgument(increment <= 360, "Circumference increment cannot be greater than 359!");
        Preconditions.checkArgument(increment > 1, "Circumference increment cannot be lower than 1!");

        Location[] locations = new Location[360 / increment];
        center = center.clone();
        double x, y, z;

        int i = 0;
        for (int a = 0; a < 360; a += increment) {
            x = (Math.cos(a) * 5) + center.getDirection().normalize().getX();
            y = center.getDirection().normalize().getY();
            z = (Math.sin(a) * 5) + center.getDirection().normalize().getZ();

            center.add(x, y, z);
            locations[i] = center;
            center.subtract(x, y, z);
            i++;
        }

        return locations;
    }

}
