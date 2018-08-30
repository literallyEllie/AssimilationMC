package net.assimilationmc.assicore.util;

import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class UtilServer {

    public static final int DEFAULT_BALANCE_BUCKS = 250;
    public static final int DEFAULT_BALANCE_UC = 0;

    /**
     * Broadcast a server wide message
     *
     * @param message Message to broadcast
     */
    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    /**
     * "Shorthand" event caller
     *
     * @param event Event to call
     */
    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Get the local IP
     *
     * @return Local IP or 127.0.0.1 if {@link UnknownHostException} is thrown.
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }

    /**
     * Get the external server IP
     *
     * @return the external IP provided by aws or 0.0.0.0 if an {@link IOException} is thrown.
     */
    public static String getExternalIp() {
        try {
            final URL whatismyip = new URL("http://checkip.amazonaws.com");
            final BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }
    }

    /**
     * "Shorthand" online player size
     *
     * @return Online player size
     */
    public static int getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().size();
    }

    /**
     * "Shorthand" max player size
     *
     * @return Max server players
     */
    public static int getMaxPlayers() {
        return Bukkit.getMaxPlayers();
    }

    /**
     * Cheat the system and set the max players of a server.
     *
     * @param players new max player count.
     */
    public static void setMaxPlayers(int players) {
        DedicatedPlayerList server = ((CraftServer) Bukkit.getServer()).getHandle();
        UtilReflect.setSuperValue(server, "maxPlayers", players);
    }

    public static boolean isFull() {
        return Bukkit.getMaxPlayers() == Bukkit.getOnlinePlayers().size();
    }

    /**
     * @return the allocated memory to the current JVM.
     */
    public static long getAllocatedMemory() {
        return UtilMath.bToMb(Runtime.getRuntime().totalMemory());
    }

    /**
     * @return the free memory of the current JVM.
     */
    public static long getFreeMemory() {
        return UtilMath.bToMb(Runtime.getRuntime().freeMemory());
    }

    /**
     * @return the used memory.
     */
    public static long getUsedMemory() {
        return getAllocatedMemory() - getFreeMemory();
    }

}
