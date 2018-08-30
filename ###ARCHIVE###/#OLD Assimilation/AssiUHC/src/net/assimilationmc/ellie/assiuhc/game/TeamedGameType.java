package net.assimilationmc.ellie.assiuhc.game;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ellie on 27/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum TeamedGameType {

    BLIND(4, 50, 2, 25, 2, 2, new ItemBuilder(Material.WEB).setDisplay("&9Blind Teams")
            .setLore("&f", UColorChart.R+"Be careful who you kill...").build(), "Blind"),
    SCATTER(8, 50, 2, 25, 4, 4, new ItemBuilder(Material.LONG_GRASS).setDisplay("&2Scatter").
            setLore("&f", UColorChart.R+"With no one to turn to, who can you trust?").build(), "Scatter"),
    CLASSICAL(4, 60, 2, 30, 2, 2, new ItemBuilder(Material.IRON_CHESTPLATE).setDisplay("&aClassical")
            .setLore("&f", UColorChart.R+"What the server is all about!").build(), "Classical"),
    DEATH_MATCH(2, 2, 2, 2, 1, 1, new ItemBuilder(Material.FIREWORK_CHARGE).setDisplay("&2Deathmatch").
    setLore("&f", UColorChart.R+"Fight to the death in a small map on a 2v2").build(), "Death match"),
    TEAMED_TEST(1, 1000, 1, 3, 1, 5, new ItemBuilder(Material.BAKED_POTATO).setDisplay("&ctesteroni").build(), "Test mode")

    ;

    private final int minPlayers;
    private final int maxPlayers;
    private final int minTeams;
    private final int maxTeams;
    private final int minTeamSize;
    private final int maxTeamSize;

    private final ItemStack itemStack;

    private final String friendly;

    TeamedGameType(int minPlayers, int maxPlayers, int minteams, int maxteams, int minteamsize, int maxteamsize,
                   ItemStack itemStack, String friendly){
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minTeams = minteams;
        this.maxTeams = maxteams;
        this.minTeamSize = minteamsize;
        this.maxTeamSize = maxteamsize;
        this.itemStack = itemStack;
        this.friendly = friendly;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinTeams() {
        return minTeams;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public int getMinTeamSize() {
        return minTeamSize;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getFriendly() {
        return friendly;
    }
}
