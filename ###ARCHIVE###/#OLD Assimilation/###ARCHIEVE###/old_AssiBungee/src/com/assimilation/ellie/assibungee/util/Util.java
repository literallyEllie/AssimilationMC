package com.assimilation.ellie.assibungee.util;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Util {

    private static Gson gson = new Gson();

    public static String prefix(){
        return color("&2Assi&amilation &7| &f");
    }

    public static String color(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void mINFO(CommandSender commandSender, String message){
        commandSender.sendMessage(TextComponent.fromLegacyText(color(prefix()+message)));
    }

    public static void mWARN(CommandSender commandSender, String message){
        commandSender.sendMessage(TextComponent.fromLegacyText(color(prefix()+"&c"+message)));
    }

    public static void mINFO_noP(CommandSender commandSender, String message){
        commandSender.sendMessage(TextComponent.fromLegacyText(color(message)));
    }

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

    public static Gson getGson() {
        return gson;
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
            if (accuracy > 2){
                break;
            }
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0)
            {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        if (sb.length() == 0){
            return "now";
        }
        return sb.toString().trim();
    }

    public static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future){
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))){
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

    public static List<String> filter(Set<String> list, String query){
        return list.stream().filter(s -> s.equalsIgnoreCase(query)).collect(Collectors.toList());
    }

    public static List<String> filterCapped(Set<String> list, String query, int cap){
        return list.stream().filter(s -> s.equalsIgnoreCase(query)).limit(cap).collect(Collectors.toList());
    }

}
