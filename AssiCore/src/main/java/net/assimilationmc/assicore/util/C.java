package net.assimilationmc.assicore.util;

import org.bukkit.ChatColor;

public class C {

    /**
     * For not-necessarily important values that never change regardless of the environment.
     */
    public static final String C = ChatColor.GRAY.toString();

    /**
     * For not-necessarily important values that change depending on the environment.
     */
    public static final String V = ChatColor.BLUE.toString();

    /**
     * For values in a string that can be regarded as important information.
     */
    public static final String II = ChatColor.RED.toString();

    /**
     * For command names that might show up as a prefix to a string.
     */
    public static final String CN = ChatColor.GREEN.toString();

    /**
     * Data separator between a subject and the body of the request.
     */
    public static final String SS = ChatColor.DARK_GRAY + "» ";

    /**
     * The color for Bucks.
     */
    public static final String BUCKS = ChatColor.GREEN.toString();

    /**
     * The color for Ultra Coins
     */
    public static final String UC = ChatColor.GOLD.toString();

}
