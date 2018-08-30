package net.assimilationmc.assiuhc.chat;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.chat.ChatMessage;
import net.assimilationmc.assicore.chat.ChatPolicy;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.spectate.GameSpectateManager;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class UHCTeamChatPolicy extends ChatPolicy {

    private final AssiGame game;
    private Set<UUID> globalChat;

    public UHCTeamChatPolicy(AssiGame assiGame) {
        this.game = assiGame;
        this.globalChat = Sets.newHashSet();
    }

    @Override
    public ChatMessage handleChat(ChatMessage message) {
        AssiPlayer player = message.getSender();
        GameTeam gameTeam = this.game.getTeamManager().getTeam(player.getBase());
        if (gameTeam != null) {
            if (gameTeam.getName().equals(GameSpectateManager.SPECTATOR_TEAM_NAME)) {
                message.setFormat(gameTeam.getColor() + "[SPECTATORS] {name} " + C.SS + GC.C + "{message}");
                if (game.getGamePhase() != GamePhase.END) {
                    message.setCancelled(true);
                    this.game.getSpectateManager().handleChat(message);
                }

                return message;
            }

            if (game.getGamePhase() == GamePhase.END || game.getGamePhase() == GamePhase.LOBBY) {
                // invert
                if (globalChat.contains(player.getUuid())) {
                    gameTeam.message(C.II + ChatColor.ITALIC + "[Team Chat] " + ChatColor.RESET + C.C + player.getName() + " " + C.SS + GC.V + " " + message.getMessage());
                    message.setCancelled(true);
                    return message;
                } else
                    message.setFormat(ChatColor.BOLD + gameTeam.getColor().toString() + gameTeam.getName() + ChatColor.RESET + GC.C + " {name} " + C.SS + GC.C + "{message}");

                return message;
            }

            if (!globalChat.contains(player.getUuid())) {
                gameTeam.message(C.II + ChatColor.ITALIC + "[Team Chat] " + ChatColor.RESET + GC.C + player.getName() + " " + C.SS + GC.V + " " + message.getMessage());
                message.setCancelled(true);
                return message;
            }

            message.setFormat(ChatColor.BOLD + gameTeam.getColor().toString() + gameTeam.getName() + ChatColor.RESET + GC.C + " {name} " + C.SS + GC.C + "{message}");
            return message;
        }
        if (game.getGamePhase() == GamePhase.LOBBY)
            message.setFormat(C.C + ChatColor.ITALIC + "[Lone]" + GC.C + " {name} " + C.SS + GC.C + " {message}");
        else return super.handleChat(message);
        return message;
    }

    public Set<UUID> getGlobalChat() {
        return globalChat;
    }

    public boolean toggleChat(Player player) {
        if (globalChat.contains(player.getUniqueId())) {
            globalChat.remove(player.getUniqueId());
            return game.getGamePhase() == GamePhase.END || game.getGamePhase() == GamePhase.LOBBY;
        }
        globalChat.add(player.getUniqueId());
        return !(game.getGamePhase() == GamePhase.END || game.getGamePhase() == GamePhase.LOBBY);
    }

    public AssiGame getGame() {
        return game;
    }

}
