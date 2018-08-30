package net.assimilationmc.assicore.scoreboard;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.ChatColor;

import java.util.List;

public class ScoreboardPolicy {

    private final AssiPlugin plugin;

    public ScoreboardPolicy(AssiPlugin plugin) {
        this.plugin = plugin;
    }

    protected String empty(int i) {
        StringBuilder emptyLine = new StringBuilder();
        for (int j = 0; j < i; j++) {
            emptyLine.append(" ");
        }
        return emptyLine.toString();
    }

    public List<String> getSideBar(AssiPlayer player) {
        return Lists.newArrayList();
    }

    public String getPlayerTagPrefix(AssiPlayer viewerPlayer, AssiPlayer player) {
        Party viewerParty = plugin.getPartyManager().getPartyOf(viewerPlayer.getBase(), false);
        if (viewerParty != null) {
            Party party = plugin.getPartyManager().getPartyOf(player.getBase(), false);
            if (party != null && party.getLeader().equals(viewerParty.getLeader())) {
                return ChatColor.AQUA + ChatColor.BOLD.toString() + "PARTY " + ChatColor.RESET;
            }
        }
        return player.getRank().getPrefix() + (player.getRank().getPrefix().isEmpty() ? "" : " ");
    }

    public String getPlayerTagSuffix(AssiPlayer viewerPlayer, AssiPlayer player) {
        return null;
    }

    public String getPlayerTabName(AssiPlayer player) {
        return player.getDisplayName();
    }

    public String getUnderName(AssiPlayer player) {
        return null;
    }

    protected AssiPlugin getPlugin() {
        return plugin;
    }

}
