package net.assimilationmc.assibungee.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class UtilTime {

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

}
