package net.assimilationmc.ellie.assicore.task.fakeenjin;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinStatus {

    @SerializedName("java_version")
    private String java_version;
    @SerializedName("mc_version")
    private String mc_version;
    private List<String> plugins;
    @SerializedName("hasranks")
    private Boolean hasranks;
    @SerializedName("pluginversion")
    private String pluginversion;
    private List<String> worlds;
    private List<String> groups;
    @SerializedName("maxplayers")
    private Integer maxplayers;
    private Integer players;
    @SerializedName("playerlist")
    private List<EnjinPlayerInfo> playerlist;
    @SerializedName("playergroups")
    private Map<String, EnjinPlayerGroupInfo> playergroups;
    private Double tps;
    @SerializedName("executed_commands")
    private List<EnjinExecutedCommand> executed_commands;
    @SerializedName("votifier")
    private Map<String, List<Object[]>> votes;
    private String stats;

    public String toString() {
        return "Status(java_version=" + getJava_version() + ", mc_version=" + getMc_version() + ", plugins=" + getPlugins() + ", hasranks=" + isHasranks() + ", " +
                "pluginversion=" + getPluginversion() + ", worlds=" + getWorlds() + ", groups=" + getGroups() + ", " +
                "maxplayers=" + getMaxplayers() + ", players=" + getPlayers() + ", playerlist=" + getPlayerlist() + ", " +
                "playergroups=" + getPlayerGroups() + ", tps=" + getTps() + ", executed_commands=" + getExecuted_commands() + ", votifier=" + getVotes() + ", stats=" + getStats() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EnjinStatus)) {
            return false;
        }
        EnjinStatus other = (EnjinStatus) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$javaVersion = getJava_version();
        Object other$javaVersion = other.getJava_version();
        if (this$javaVersion == null ? other$javaVersion != null : !this$javaVersion.equals(other$javaVersion)) {
            return false;
        }
        Object this$mcVersion = getMc_version();
        Object other$mcVersion = other.getMc_version();
        if (this$mcVersion == null ? other$mcVersion != null : !this$mcVersion.equals(other$mcVersion)) {
            return false;
        }
        Object this$plugins = getPlugins();
        Object other$plugins = other.getPlugins();
        if (this$plugins == null ? other$plugins != null : !this$plugins.equals(other$plugins)) {
            return false;
        }
        Object this$ranksEnabled = isHasranks();
        Object other$ranksEnabled = other.isHasranks();
        if (!this$ranksEnabled.equals(other$ranksEnabled)) {
            return false;
        }
        Object this$pluginVersion = getPluginversion();
        Object other$pluginVersion = other.getPluginversion();
        if (this$pluginVersion == null ? other$pluginVersion != null : !this$pluginVersion.equals(other$pluginVersion)) {
            return false;
        }
        Object this$worlds = getWorlds();
        Object other$worlds = other.getWorlds();
        if (this$worlds == null ? other$worlds != null : !this$worlds.equals(other$worlds)) {
            return false;
        }
        Object this$groups = getGroups();
        Object other$groups = other.getGroups();
        if (this$groups == null ? other$groups != null : !this$groups.equals(other$groups)) {
            return false;
        }
        Object this$maxPlayers = getMaxplayers();
        Object other$maxPlayers = other.getMaxplayers();
        if (this$maxPlayers == null ? other$maxPlayers != null : !this$maxPlayers.equals(other$maxPlayers)) {
            return false;
        }
        Object this$players = getPlayers();
        Object other$players = other.getPlayers();
        if (this$players == null ? other$players != null : !this$players.equals(other$players)) {
            return false;
        }
        Object this$playersList = getPlayerlist();
        Object other$playersList = other.getPlayerlist();
        if (this$playersList == null ? other$playersList != null : !this$playersList.equals(other$playersList)) {
            return false;
        }
        Object this$playerGroups = getPlayerGroups();
        Object other$playerGroups = other.getPlayerGroups();
        if (this$playerGroups == null ? other$playerGroups != null : !this$playerGroups.equals(other$playerGroups)) {
            return false;
        }
        Object this$tps = getTps();
        Object other$tps = other.getTps();
        if (this$tps == null ? other$tps != null : !this$tps.equals(other$tps)) {
            return false;
        }
        Object this$executedCommands = getExecuted_commands();
        Object other$executedCommands = other.getExecuted_commands();
        if (this$executedCommands == null ? other$executedCommands != null : !this$executedCommands.equals(other$executedCommands)) {
            return false;
        }
        Object this$votes = getVotes();
        Object other$votes = other.getVotes();
        if (this$votes == null ? other$votes != null : !this$votes.equals(other$votes)) {
            return false;
        }
        Object this$stats = getStats();
        Object other$stats = other.getStats();
        return this$stats == null ? other$stats == null : this$stats.equals(other$stats);
    }

    protected boolean canEqual(Object other) {
        return other instanceof EnjinStatus;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $javaVersion = getJava_version();
        result = result * 59 + ($javaVersion == null ? 43 : $javaVersion.hashCode());
        Object $mcVersion = getMc_version();
        result = result * 59 + ($mcVersion == null ? 43 : $mcVersion.hashCode());
        Object $plugins = getPlugins();
        result = result * 59 + ($plugins == null ? 43 : $plugins.hashCode());
        Object $ranksEnabled = isHasranks();
        result = result * 59 + $ranksEnabled.hashCode();
        Object $pluginVersion = getPluginversion();
        result = result * 59 + ($pluginVersion == null ? 43 : $pluginVersion.hashCode());
        Object $worlds = getWorlds();
        result = result * 59 + ($worlds == null ? 43 : $worlds.hashCode());
        Object $groups = getGroups();
        result = result * 59 + ($groups == null ? 43 : $groups.hashCode());
        Object $maxPlayers = getMaxplayers();
        result = result * 59 + ($maxPlayers == null ? 43 : $maxPlayers.hashCode());
        Object $players = getPlayers();
        result = result * 59 + ($players == null ? 43 : $players.hashCode());
        Object $playersList = getPlayerlist();
        result = result * 59 + ($playersList == null ? 43 : $playersList.hashCode());
        Object $playerGroups = getPlayerGroups();
        result = result * 59 + ($playerGroups == null ? 43 : $playerGroups.hashCode());
        Object $tps = getTps();
        result = result * 59 + ($tps == null ? 43 : $tps.hashCode());
        Object $executedCommands = getExecuted_commands();
        result = result * 59 + ($executedCommands == null ? 43 : $executedCommands.hashCode());
        Object $votes = getVotes();
        result = result * 59 + ($votes == null ? 43 : $votes.hashCode());
        Object $stats = getStats();
        result = result * 59 + ($stats == null ? 43 : $stats.hashCode());
        return result;
    }

    public EnjinStatus(String javaVersion, String mcVersion, List<String> plugins, Boolean ranksEnabled, String pluginVersion, List<String> worlds,
                       List<String> groups, Integer maxPlayers, Integer players,
                       List<EnjinPlayerInfo> playersList, Map<String, EnjinPlayerGroupInfo> playerGroups, Double tps,
                       List<EnjinExecutedCommand> executedCommands, Map<String, List<Object[]>> votes, String stats) {
        this.java_version = javaVersion;
        this.mc_version = mcVersion;
        this.plugins = plugins;
        this.hasranks = ranksEnabled;
        this.pluginversion = pluginVersion;
        this.worlds = worlds;
        this.groups = groups;
        this.maxplayers = maxPlayers;
        this.players = players;
        this.playerlist = playersList;
        this.playergroups = playerGroups;
        this.tps = tps;
        this.executed_commands = executedCommands;
        this.votes = votes;
        this.stats = stats;
    }

    public String getJava_version() {
        return this.java_version;
    }

    public String getMc_version() {
        return this.mc_version;
    }

    public List<String> getPlugins() {
        return this.plugins;
    }

    public boolean isHasranks() {
        return this.hasranks;
    }

    public String getPluginversion() {
        return this.pluginversion;
    }

    public List<String> getWorlds() {
        return this.worlds;
    }

    public List<String> getGroups() {
        return this.groups;
    }

    public Integer getMaxplayers() {
        return this.maxplayers;
    }

    public Integer getPlayers() {
        return this.players;
    }

    public List<EnjinPlayerInfo> getPlayerlist() {
        return this.playerlist;
    }

    public Map<String, EnjinPlayerGroupInfo> getPlayerGroups() {
        return this.playergroups;
    }

    public Double getTps() {
        return this.tps;
    }

    public List<EnjinExecutedCommand> getExecuted_commands() {
        return this.executed_commands;
    }

    public Map<String, List<Object[]>> getVotes() {
        return this.votes;
    }

    public String getStats() {
        return this.stats;
    }
}

