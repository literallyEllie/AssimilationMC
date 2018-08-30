package com.assimilation.ellie.assicore.util;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Util {

    public static String prefix(){
        return color("&2Assi&amilation &7| &f");
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private static Gson gson = new Gson();

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

    public static void handleException(String type, Exception exception){
        exception.printStackTrace();
        AssiCore.getCore().logE("Error occurred whilst handling "+type+".");
    }

    public static boolean isCommand(String message){
        return message.length() > 0 && message.charAt(0) == 47;
    }


    public static void mINFO(CommandSender commandSender, String message){
        commandSender.sendMessage(color(prefix()+message));
    }

    public static void mWARN(CommandSender commandSender, String message){
        commandSender.sendMessage(color(prefix()+"&c"+message));
    }

    public static void mINFO_noP(CommandSender commandSender, String message){
        commandSender.sendMessage(color(message));
    }

    public static Gson getGson() {
        return gson;
    }

    public static String getDuration(final long futureTimestamp) {
        int seconds = (int) ((futureTimestamp - System.currentTimeMillis()) / 1000) + 1;
        Preconditions.checkArgument(seconds > 0, "The timestamp passed in parameter must be superior to the current timestamp!");

        final List<String> item = new ArrayList<>();

        int months = 0;
        while (seconds >= 2678400) {
            months++;
            seconds -= 2678400;
        }
        if (months > 0) {
            item.add(months + " months");
        }

        int days = 0;
        while (seconds >= 86400) {
            days++;
            seconds -= 86400;
        }
        if (days > 0) {
            item.add(days + " days");
        }

        int hours = 0;
        while (seconds >= 3600) {
            hours++;
            seconds -= 3600;
        }
        if (hours > 0) {
            item.add(hours + " hours");
        }

        int mins = 0;
        while (seconds >= 60) {
            mins++;
            seconds -= 60;
        }
        if (mins > 0) {
            item.add(mins + " minutes");
        }

        if (seconds > 0) {
            item.add(seconds + " seconds");
        }

        return Joiner.on(", ").join(item);
    }

    public static String formatDateDiff(long date){
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        Calendar now = new GregorianCalendar();
        return formatDateDiff(now, c);
    }

    public static String formatDateDiff(Calendar fromDate, Calendar toDate){
        boolean future = false;
        if (toDate.equals(fromDate)){
            return "now";
        }
        if (toDate.after(fromDate)){
            future = true;
        }
        StringBuilder sb = new StringBuilder();
        int[] types = new int[]{
                Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
        String[] names = new String[]{
                ("year"), ("years"), ("month"), ("months"), ("day"), ("days"), ("hour"), ("hours"), ("minute"), ("minutes"), ("second"), ("seconds")
        };
        int accuracy = 0;
        for (int i = 0; i < types.length; i++){
            if (accuracy > 2)
                break;

            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0){
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        if (sb.length() == 0){
            return "now";
        }
        return sb.toString().trim();
    }

    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
        {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

}
