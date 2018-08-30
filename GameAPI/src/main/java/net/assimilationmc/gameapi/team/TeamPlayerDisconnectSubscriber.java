package net.assimilationmc.gameapi.team;

import org.bukkit.entity.Player;

public interface TeamPlayerDisconnectSubscriber {

    void onDisconnect(Player player, GameTeam gameTeam);

}
