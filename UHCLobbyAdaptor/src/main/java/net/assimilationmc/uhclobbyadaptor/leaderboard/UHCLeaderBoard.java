package net.assimilationmc.uhclobbyadaptor.leaderboard;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.leaderboard.LeaderboardEntity;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilJson;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import net.assimilationmc.uhclobbyadaptor.stats.ROUHCStatsProvider;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UHCLeaderBoard implements Listener {

    private UHCLobbyAdaptor uhcLobbyAdaptor;
    private Map<String, Integer> mostWins;
    private Map<String, Integer> topKills;
    private Map<String, Integer> topLevel;

    public UHCLeaderBoard(UHCLobbyAdaptor uhcLobbyAdaptor) {
        this.uhcLobbyAdaptor = uhcLobbyAdaptor;
        this.mostWins = Maps.newLinkedHashMap();
        this.topKills = Maps.newLinkedHashMap();
        this.topLevel = Maps.newLinkedHashMap();

        Bukkit.getScheduler().runTaskLater(uhcLobbyAdaptor, () -> on(new UpdateEvent(UpdateType.TEN_MIN)), 120L);

    }

    public void finish() {
        mostWins.clear();
        topKills.clear();
        topLevel.clear();
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() != UpdateType.TEN_MIN) return;

        try (Connection connection = uhcLobbyAdaptor.getAssiPlugin().getSqlManager().getConnection()) {

            List<LeaderboardEntity> uhcWins = uhcLobbyAdaptor.getAssiPlugin().getLeaderboardManager().getLeaderboardsOfType("UHC_WIN");
            if (!uhcWins.isEmpty()) {

                // Games won
                mostWins.clear();
                PreparedStatement prepWins = connection.prepareStatement("SELECT name, win_count FROM `" + ROUHCStatsProvider.SQL_TABLE + "` " +
                        "ORDER BY win_count DESC LIMIT 3");
                final ResultSet winsRs = prepWins.executeQuery();
                while (winsRs.next()) {
                    String name = winsRs.getString("name");
                    int wins = winsRs.getInt("win_count");
                    mostWins.put(name, wins);
                }

                winsRs.close();
                prepWins.close();

                updateWins(uhcWins);
            }

            List<LeaderboardEntity> uhcKills = uhcLobbyAdaptor.getAssiPlugin().getLeaderboardManager().getLeaderboardsOfType("UHC_KILL");
            if (!uhcKills.isEmpty()) {

                // Top kills
                topKills.clear();
                PreparedStatement prepKills = connection.prepareStatement("SELECT name, kills FROM `" + ROUHCStatsProvider.SQL_TABLE + "` " +
                        "ORDER BY kills DESC LIMIT 3");
                final ResultSet killsRs = prepKills.executeQuery();
                while (killsRs.next()) {
                    String name = killsRs.getString("name");
                    int kills = killsRs.getInt("kills");
                    topKills.put(name, kills);
                }

                killsRs.close();
                prepKills.close();

                updateKills(uhcKills);
            }

            List<LeaderboardEntity> uhcLevel = uhcLobbyAdaptor.getAssiPlugin().getLeaderboardManager().getLeaderboardsOfType("UHC_LEVEL");
            if (!uhcLevel.isEmpty()) {

                // Top deaths
                topLevel.clear();
                PreparedStatement prepLevel = connection.prepareStatement("SELECT name, level FROM `" + ROUHCStatsProvider.SQL_TABLE + "` " +
                        "ORDER BY level DESC LIMIT 3");
                final ResultSet levelRs = prepLevel.executeQuery();
                while (levelRs.next()) {
                    String name = levelRs.getString("name");
                    int level = levelRs.getInt("level");
                    topLevel.put(name, level);
                }

                levelRs.close();
                prepLevel.close();

                updateLevels(uhcLevel);
            }


        } catch (SQLException ex) {
            uhcLobbyAdaptor.getLogger().severe("Failed to update UHC leaderboard!");
            ex.printStackTrace();
        }

    }

    private void updateWins(List<LeaderboardEntity> leaderboardEntities) {
        int index = 0;
        for (Map.Entry<String, Integer> stringLongEntry : mostWins.entrySet()) {
            if (index > leaderboardEntities.size()) break;
            final String name = stringLongEntry.getKey();
            final int wins = stringLongEntry.getValue();

            LeaderboardEntity leaderboardEntity = leaderboardEntities.get(index);

            ChatColor playerColor = ChatColor.GOLD;
            switch (leaderboardEntity.getPlace()) {
                case 1:
                    playerColor = ChatColor.GREEN;
                    break;
                case 2:
                    playerColor = ChatColor.AQUA;
                    break;
            }

            final NPC npc = CitizensAPI.getNPCRegistry().getById(leaderboardEntity.getCitizensId());

            if (!npc.getName().equals(playerColor + name)) {
                npc.setName(playerColor + name);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc select " + npc.getId());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc skin -p " + name);
            }

            final Location updateSign = leaderboardEntity.getUpdateSign();
            if (updateSign != null && updateSign.getBlock().getType().name().contains("SIGN")) {
                Sign sign = (Sign) updateSign.getBlock().getState();

                sign.setLine(0, playerColor + ChatColor.BOLD.toString() + "#" + leaderboardEntity.getPlace());
                sign.setLine(2, ChatColor.RED.toString() + ChatColor.BOLD + "Wins");
                sign.setLine(3, C.V + wins);

                sign.update(true);
            }

            index++;
        }

    }

    private void updateKills(List<LeaderboardEntity> leaderboardEntities) {
        int index = 0;
        for (Map.Entry<String, Integer> stringLongEntry : topKills.entrySet()) {
            if (index > leaderboardEntities.size()) break;
            final String name = stringLongEntry.getKey();
            final int kills = stringLongEntry.getValue();

            LeaderboardEntity leaderboardEntity = leaderboardEntities.get(index);

            ChatColor playerColor = ChatColor.GOLD;
            switch (leaderboardEntity.getPlace()) {
                case 1:
                    playerColor = ChatColor.GREEN;
                    break;
                case 2:
                    playerColor = ChatColor.AQUA;
                    break;
            }

            final NPC npc = CitizensAPI.getNPCRegistry().getById(leaderboardEntity.getCitizensId());

            if (!npc.getName().equals(playerColor + name)) {
                npc.setName(playerColor + name);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc select " + npc.getId());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc skin -p " + name);
            }

            final Location updateSign = leaderboardEntity.getUpdateSign();
            if (updateSign != null && updateSign.getBlock().getType().name().contains("SIGN")) {
                Sign sign = (Sign) updateSign.getBlock().getState();

                sign.setLine(0, playerColor + ChatColor.BOLD.toString() + "#" + leaderboardEntity.getPlace());
                sign.setLine(2, ChatColor.RED.toString() + ChatColor.BOLD + "Kills");
                sign.setLine(3, C.V + kills);

                sign.update(true);
            }

            index++;
        }

    }

    private void updateLevels(List<LeaderboardEntity> leaderboardEntities) {
        int index = 0;
        for (Map.Entry<String, Integer> stringLongEntry : topLevel.entrySet()) {
            if (index > leaderboardEntities.size()) break;
            final String name = stringLongEntry.getKey();
            final int kills = stringLongEntry.getValue();

            LeaderboardEntity leaderboardEntity = leaderboardEntities.get(index);

            ChatColor playerColor = ChatColor.GOLD;
            switch (leaderboardEntity.getPlace()) {
                case 1:
                    playerColor = ChatColor.GREEN;
                    break;
                case 2:
                    playerColor = ChatColor.AQUA;
                    break;
            }

            final NPC npc = CitizensAPI.getNPCRegistry().getById(leaderboardEntity.getCitizensId());

            if (!npc.getName().equals(playerColor + name)) {
                npc.setName(playerColor + name);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc select " + npc.getId());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "npc skin -p " + name);
            }

            final Location updateSign = leaderboardEntity.getUpdateSign();
            if (updateSign != null && updateSign.getBlock().getType().name().contains("SIGN")) {
                Sign sign = (Sign) updateSign.getBlock().getState();

                sign.setLine(0, playerColor + ChatColor.BOLD.toString() + "#" + leaderboardEntity.getPlace());
                sign.setLine(2, ChatColor.RED.toString() + ChatColor.BOLD + "Level");
                sign.setLine(3, C.V + kills);

                sign.update(true);
            }

            index++;
        }

    }

    public Map<String, Integer> getMostWins() {
        return mostWins;
    }

    public Map<String, Integer> getTopKills() {
        return topKills;
    }

    public Map<String, Integer> getTopLevel() {
        return topLevel;
    }

}
