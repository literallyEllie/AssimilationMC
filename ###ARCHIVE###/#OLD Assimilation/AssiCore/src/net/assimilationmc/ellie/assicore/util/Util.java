package net.assimilationmc.ellie.assicore.util;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Util {

    private final static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
            + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

    public final static Pattern ipPattern = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

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

    public static void handleException(String type, Throwable exception){
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
        commandSender.sendMessage(Util.color("&c&lError! &c"+message));
    }

    public static void mINFO_noP(CommandSender commandSender, String message){
        commandSender.sendMessage(color(message));
    }

    public static void kickPlayer(Player player, String reason){
        player.kickPlayer(color("&2Assi&amilation")+color("&7\n"+reason));
    }

    public static boolean isPast(long timestamp){
        return (((timestamp - System.currentTimeMillis()) / 1000) + 1) < 0;
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
                Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE};
        String[] names = new String[]{
                ("year"), ("years"), ("month"), ("months"), ("day"), ("days"), ("hour"), ("hours"), ("minute"), ("minutes"),
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

    public static long parseDuration(final String durationStr) throws IllegalArgumentException {
        final Matcher m = timePattern.matcher(durationStr);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(org.bukkit.ChatColor.RED + "Invalid duration!");
        }
        final Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds);
        }
        return c.getTimeInMillis();
    }

    public static long toTicksFromSeconds(int seconds){
        return seconds * 20;
    }

    public static long toTicksFromMinutes(int minutes){
        return minutes * 60 * 20;
    }

    public static long toTicksFromHours(int hours){
        return hours * 60 * 60 * 20;
    }

    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
        return simpleDateFormat.format(new Date());
    }

    public static int toSecondsFromMinutes(int minutes){
        return minutes * 60;

    }

    public static void mainLobby(Player player){
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        player.setHealthScale(20);
        player.setFoodLevel(20);
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 30L, 20L);
        player.setWalkSpeed(0.6F);
        player.setGameMode(GameMode.ADVENTURE);

        if(player.hasPermission(PermissionLib.LOBBY.FLY)){
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        player.setExp(0);
        player.setLevel(0);

    }

    public static void sendHotbar(Player player, String message){
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat();
        setValue(packetPlayOutChat, "a", IChatBaseComponent.ChatSerializer.a("{\"text\":\""+Util.color(message)+"\",\"color\":\"white\"}"));
        setValue(packetPlayOutChat, "b", (byte) 2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    private static void setValue(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }




}
