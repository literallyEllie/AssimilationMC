package net.assimilationmc.assiuhc.game.teamed.scatter;

import net.assimilationmc.assicore.chat.ChatMessage;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assiuhc.chat.UHCTeamChatPolicy;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ScatterChatPolicy extends UHCTeamChatPolicy {

    public ScatterChatPolicy(AssiGame assiGame) {
        super(assiGame);
    }

    @Override
    public ChatMessage handleChat(ChatMessage message) {
        final AssiPlayer sender = message.getSender();
        final Player player = sender.getBase();
        final GameTeam team = getGame().getTeamManager().getTeam(player);

        if (team == null || getGame().getGamePhase() == GamePhase.LOBBY
                || getGame().getGamePhase() == GamePhase.END || team.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
            return super.handleChat(message);
        }

        message.setCancelled(true);
        String msg = C.II + "[Local] " + ChatColor.BOLD + team.getColor().toString() + team.getName() + ChatColor.RESET + GC.C + " " + sender.getName() + " " + C.SS + GC.C + message.getMessage();

        player.sendMessage(msg);

        // Local
        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            if (!(entity instanceof Player)) continue;
            Player listener = (Player) entity;
            listener.sendMessage(msg);
        }

        getGame().getPlugin().getLogger().info("[LOCAL] " + player.getName() + ": " + msg);

        return message;
    }

}
