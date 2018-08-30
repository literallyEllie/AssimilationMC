package net.assimilationmc.ellie.assiuhc.games.util;

import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assiuhc.game.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCTeam {

    private int id;
    private TeamManager teamManager;
    private String name;
    private String leader;
    private ItemBuilder.StackColor teamColor;
    private HashMap<String, Boolean> members;

    public UHCTeam(int id, TeamManager manager, String name, String leader){
        this.id = id;
        this.teamManager = manager;
        this.name = name;
        this.leader = leader;
        this.teamColor = ItemBuilder.StackColor.WHITE;
        this.members = new HashMap<>();
        this.members.put(leader, false);
    }

    public int getID() {
        return id;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public String getName() {
        return name;
    }

    public String getLeader() {
        return leader;
    }

    public ChatColor getChatColor() {
        return teamColor.getChatColor();
    }

    public void setTeamColor(ItemBuilder.StackColor teamColor) {
        this.teamColor = teamColor;
    }

    public ItemBuilder.StackColor getTeamColor() {
        return teamColor;
    }

    // string = name, boolean= dead
    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setDead(Player player){
        members.put(player.getName(), true);
    }

    public List<String> getAlive(){
        return members.entrySet().stream().filter(stringBooleanEntry -> !stringBooleanEntry.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

}
