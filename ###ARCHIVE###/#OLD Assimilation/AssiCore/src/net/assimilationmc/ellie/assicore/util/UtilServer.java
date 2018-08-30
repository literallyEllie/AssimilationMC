package net.assimilationmc.ellie.assicore.util;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

public class UtilServer {

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    public static <E extends Event> E callEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static long getFreeMemory() {
        return bytesToMegabytes(Runtime.getRuntime().freeMemory());
    }

    public static long getMemoryUsedUp() {
        return getTotalMemory() - getFreeMemory();
    }

    public static long getTotalMemoryAllocated() {
        return bytesToMegabytes(Runtime.getRuntime().maxMemory());
    }

    public static long getTotalMemory() {
        return bytesToMegabytes(Runtime.getRuntime().totalMemory());
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / 1048576L;
    }

    public static double getTps() {
        return Double.parseDouble((new DecimalFormat("0.00")).format(MinecraftServer.getServer().recentTps[0]));
    }

    public static boolean checkTps() {
        return getTps() > 18.0D;
    }

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
            return "127.0.0.1";
        }
    }

    public static String getExternalIp() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        } catch (IOException var2) {
            var2.printStackTrace();
            return "0.0.0.0";
        }
    }


}
