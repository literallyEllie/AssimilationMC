package net.assimilationmc.assicore.util;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class UtilTime {

    private static PrettyTime prettyTime = new PrettyTime();

    /**
     * Evaluates if a time has passed
     *
     * @param from     Reference timestamp
     * @param required Required time to have passed from now and then
     * @return If the now minus the from value is greater than the required time.
     */
    public static boolean elapsed(long from, long required) {
        return now() - from > required;
    }

    /**
     * "Shorthand" current millis getter
     *
     * @return {@link System#currentTimeMillis()}
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Evaluates if the month is the required month.
     *
     * @param month Month required
     * @return If this month is the required month.
     */
    public static boolean isMonth(Month month) {
        return Calendar.getInstance().get(Calendar.MONTH) == month.ordinal();
    }

    /**
     * Evaluates if the day is the required day.
     *
     * @param day day required
     * @return If this day is the required day.
     */
    public static boolean isDay(int day) {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == day;
    }

    /**
     * Get the timestamp print.
     *
     * @param pattern The pattern to format the time to.
     * @return The formatted timestamp string.
     */
    public static String formatNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * @return the default timestamp.
     */
    public static String getTime() {
        return formatNow("HH:mm:ss");
    }

    /**
     * Format a timestamp into a pretty format.
     *
     * @param timestamp The timestamp to format. WHATEVER YOU DO, DO NOT HAVE THIS TIMESTAMP BE A RESULT OF AN EQUATION INVOLVING {@link System#currentTimeMillis()}
     * @return the prettied format. (It will return "5 decades ago" if you did it wrong)
     */
    public static String formatTimeStamp(long timestamp) {
        return prettyTime.format(new Date(timestamp));
    }

    public static String formatMinutes(int min, boolean fullApprev) {
        int mins = 0, secs = 0;

        for (int i = 0; i < min; i++) {
            secs++;
            if (secs == 60) {
                mins++;
                i = 0;
                min -= 60;
                secs = 0;
            }
        }

        if (fullApprev) {

        }
        return (mins > 0 ? mins + "m " : "") +  secs + "s";
    }

}
