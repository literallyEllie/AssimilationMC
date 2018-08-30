package net.assimilationmc.gameapi.team;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GameTeam {

    private String name;
    private ChatColor color;
    private boolean autoAdd, hidden;
    private List<UUID> players;

    public GameTeam(String name, ChatColor chatColor, boolean autoAdd) {
        this.name = name;
        this.color = chatColor;
        this.autoAdd = autoAdd;
        this.players = Lists.newArrayList();
    }

    public GameTeam(String name, ChatColor chatColor) {
        this(name, chatColor, false);
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public boolean isAutoAdd() {
        return autoAdd;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public boolean contains(UUID uuid) {
        return players.contains(uuid);
    }

    public boolean contains(Player player) {
        return contains(player.getUniqueId());
    }

    public void add(UUID uuid) {
        if (players.contains(uuid)) return;
        players.add(uuid);

        if (this.hidden) {
            Player player = UtilPlayer.get(uuid);
            if (player == null) return;
            Bukkit.getOnlinePlayers().forEach(o -> o.hidePlayer(player));

            for (UUID playerUuid : players) {
                if (playerUuid == uuid) continue;
                Player spectator = UtilPlayer.get(playerUuid);
                if (spectator == null) continue;
                player.showPlayer(spectator);
            }
        }

    }

    public void add(Player player) {
        add(player.getUniqueId());
    }

    public void remove(UUID uuid) {
        if (!players.contains(uuid)) return;
        players.remove(uuid);

        if (this.hidden) {
            Player player = UtilPlayer.get(uuid);
            if (player == null) return;
            Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(player));
            for (UUID playerUuid : players) {
                Player spectator = UtilPlayer.get(playerUuid);
                if (spectator == null) continue;
                player.hidePlayer(spectator);
            }
        }
    }

    public void remove(Player player) {
        remove(player.getUniqueId());
    }

    public void message(String message) {
        players.forEach(uuid -> {
            Player p = UtilPlayer.get(uuid);
            if (p != null) p.sendMessage(message);
        });
    }

}
