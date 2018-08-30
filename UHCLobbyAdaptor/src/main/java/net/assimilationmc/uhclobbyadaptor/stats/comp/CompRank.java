package net.assimilationmc.uhclobbyadaptor.stats.comp;

import org.bukkit.ChatColor;

public enum CompRank {

    UNRANKED(""),
    ONE(ChatColor.RED + "★"),
    TWO(ChatColor.RED + "★★"),
    THREE(ChatColor.RED + "★★★"),
    FOUR(ChatColor.RED + "★★★★"),
    FIVE(ChatColor.RED + "★★★★★"),
    SIX(ChatColor.BLUE + "★"),
    SEVEN(ChatColor.BLUE + "★★"),
    EIGHT(ChatColor.BLUE + "★★★"),
    NINE(ChatColor.BLUE + "★★★★"),
    TEN(ChatColor.BLUE + "★★★★★"),
    ELEVEN(ChatColor.GOLD + "★"),
    TWELVE(ChatColor.GOLD + "★★"),
    THIRTEEN(ChatColor.GOLD + "★★★"),
    FOURTEEN(ChatColor.GOLD + "★★★★"),
    FIVETEEN(ChatColor.GOLD + "★★★★★");

    private String name;

    CompRank(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return name;
    }

    public boolean canCompete(CompRank compRank) {
        return compRank == this || compRank.ordinal() - 1 == this.ordinal() || compRank.ordinal() + 1 == this.ordinal();
    }

}
