package net.assimilationmc.gameapi.stats;

import org.bukkit.entity.Player;

import java.util.List;

public interface GameStatsProvider {

    List<String> getLobbyScoreboardLines(Player player);

}
