package net.assimilationmc.gameapi.chat;

import net.assimilationmc.assicore.chat.ChatMessage;
import net.assimilationmc.assicore.chat.ChatPolicy;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.team.GameTeamManager;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;

public class GameChatPolicy extends ChatPolicy {

    private final AssiGame game;

    public GameChatPolicy(AssiGame assiGame) {
        this.game = assiGame;
    }

    @Override
    public ChatMessage handleChat(ChatMessage message) {
        AssiPlayer player = message.getSender();
        GameTeam gameTeam = game.getTeamManager().getTeam(player.getBase());

        if (gameTeam != null) {

            if (gameTeam.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
                message.setFormat(gameTeam.getColor() + "[SPECTATORS] {name} " + C.SS + GC.C + "{message}");
                if (game.getGamePhase() != GamePhase.END) {
                    message.setCancelled(true);
                    this.game.getSpectateManager().handleChat(message);
                }

                return message;
            }

            if (gameTeam.getName().equals(GameTeamManager.DEFAULT_PLAYER_TEAM)) {
                message.setFormat(gameTeam.getColor().toString() + "{name} " + C.SS + GC.V + "{message}");
            } else
                message.setFormat(ChatColor.BOLD + gameTeam.getColor().toString() + gameTeam.getName() + ChatColor.RESET +
                        GC.C + " {name} " + C.SS + GC.C + "{message}");

        } else {
            D.d("calling super.");
            return super.handleChat(message);
        }
        return message;
    }

}
