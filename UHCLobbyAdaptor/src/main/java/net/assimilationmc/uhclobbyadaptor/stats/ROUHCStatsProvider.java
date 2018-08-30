package net.assimilationmc.uhclobbyadaptor.stats;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.util.UtilJson;
import net.assimilationmc.uhclobbyadaptor.UHCLobbyAdaptor;
import net.assimilationmc.uhclobbyadaptor.stats.comp.CompRank;
import net.assimilationmc.uhclobbyadaptor.stats.comp.CooldownData;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ROUHCStatsProvider extends Module {

    public static final String SQL_TABLE = "uhc_players";
    private Map<UUID, UHCPlayer> playerMap;
    private Gson gson;

    // read only stats provider
    public ROUHCStatsProvider(UHCLobbyAdaptor plugin) {
        super(plugin.getAssiPlugin(), "Read-Only UHC Stats Provider");
    }

    @Override
    protected void start() {
        playerMap = Maps.newHashMap();
        gson = new Gson();
    }

    @Override
    protected void end() {
        playerMap.clear();
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        playerMap.remove(e.getPlayer().getUniqueId());
    }

    public UHCPlayer loadPlayer(Player player) {
        if (playerMap.containsKey(player.getUniqueId())) return playerMap.get(player.getUniqueId());

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT kills, deaths, win_count, games_played, previous_games, games_won, level, xp, rank" +
                    ", comp_cooldown FROM `" + SQL_TABLE + "` WHERE uuid = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UHCPlayer uhcPlayer = new UHCPlayer(player.getUniqueId(), player.getName());
                uhcPlayer.setKills(resultSet.getInt("kills"));
                uhcPlayer.setDeaths(resultSet.getInt("deaths"));
                uhcPlayer.setWinCount(resultSet.getInt("win_count"));
                uhcPlayer.setGamesPlayed(resultSet.getInt("games_played"));
                uhcPlayer.setPreviousGamesPlayed(UtilJson.deserialize(gson, new TypeToken<Map<UHCGameSubType, Integer>>() {
                }, resultSet.getString("previous_games")));
                uhcPlayer.setGamesWon(UtilJson.deserialize(gson, new TypeToken<Map<UHCGameSubType, Integer>>() {
                }, resultSet.getString("games_won")));
                uhcPlayer.setCompRank(CompRank.valueOf(resultSet.getString("rank")));
                uhcPlayer.setLevel(resultSet.getInt("level"));
                uhcPlayer.setXp(resultSet.getInt("xp"));
                uhcPlayer.setCooldownData(new CooldownData(resultSet.getString("comp_cooldown")));
                playerMap.put(uhcPlayer.getUuid(), uhcPlayer);

                resultSet.close();
                preparedStatement.close();
                connection.close();
                return uhcPlayer;
            }

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to open connection to MySQL! (load)");
            e.printStackTrace();
        }

        return new UHCPlayer(player.getUniqueId(), player.getName());
    }

    public UHCPlayer getPlayer(Player player) {
        return loadPlayer(player);
    }

}
