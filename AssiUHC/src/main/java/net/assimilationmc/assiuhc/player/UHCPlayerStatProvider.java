package net.assimilationmc.assiuhc.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilJson;
import net.assimilationmc.assiuhc.comp.CompRank;
import net.assimilationmc.assiuhc.comp.cooldown.CooldownData;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.spectate.PlayerGameDeathEvent;
import net.assimilationmc.gameapi.stats.GameStatsProvider;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class UHCPlayerStatProvider extends GameModule implements GameStatsProvider {

    private static final String SQL_TABLE = "uhc_players";
    private Map<UUID, UHCPlayer> onlinePlayers;
    private Gson gson;
    private Set<UUID> lobbyJoins;

    public UHCPlayerStatProvider(UHCGame game) {
        super(game, "UHC-Player Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
        this.onlinePlayers = Maps.newHashMap();
        this.gson = new Gson();
        this.lobbyJoins = Sets.newHashSet();

        try (Connection connection = getAssiGame().getPlugin().getSqlManager().getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + SQL_TABLE + "` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "`uuid` VARCHAR(100) NOT NULL UNIQUE, " +
                    "`name` VARCHAR(100) NOT NULL, " +
                    "`kills` INT NOT NULL, " +
                    "`deaths` INT NOT NULL, " +
                    "`win_count` INT NOT NULL, " +
                    "`games_played` INT NOT NULL, " +
                    "`previous_games` TEXT, " +
                    "`games_won` TEXT, " +
                    "`rank` TEXT, " +
                    "`level` INT NOT NULL, " +
                    "`xp` INT NOT NULL, " +
                    "`comp_cooldown` TEXT NULL, " +
                    "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1").execute();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to create opening statement to SQL!");
            e.printStackTrace();
        }

    }

    @Override
    public void end() {
        Bukkit.getOnlinePlayers().forEach(this::unloadPlayer);

        onlinePlayers.clear();
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());

        if (getAssiGame().getGamePhase() == GamePhase.LOBBY) {
            lobbyJoins.add(e.getPlayer().getUniqueId());
        }

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {

        //if (getAssiGame().getGamePhase() == GamePhase.IN_GAME && lobbyJoins.contains(e.getPlayer().getUniqueId())) {
          //  final UHCPlayer player = getPlayer(e.getPlayer());
            //player.addGamePlayed(((UHCGame) getAssiGame()).getGameSubType());
           // lobbyJoins.remove(player.getUuid());
        //}

        unloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        if (e.getTo() == GamePhase.END) {

        }
    }

    @EventHandler
    public void on(final PlayerGameDeathEvent e) {
        final UHCPlayer killed = getPlayer(e.getPlayer());
        final Entity eKiller = e.getKiller();

        killed.addDeath();

        if (eKiller instanceof Player) {
            UHCPlayer killer = getPlayer(eKiller.getUniqueId());
            killer.addKill();
//            ((UHCGame) getAssiGame()).getXpManager().giveXP(killed, XPRewards.KILL);
        }

    }

    public void loadPlayer(Player player) {
        if (onlinePlayers.containsKey(player.getUniqueId())) return;

        try (Connection connection = getAssiGame().getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + SQL_TABLE + "` WHERE uuid = ?");
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
                uhcPlayer.setWins(UtilJson.deserialize(gson, new TypeToken<Map<UHCGameSubType, Integer>>() {
                }, resultSet.getString("games_won")));
                uhcPlayer.setCompRank(CompRank.valueOf(resultSet.getString("rank")));
                uhcPlayer.setLevel(resultSet.getInt("level"));
                uhcPlayer.setXp(resultSet.getInt("xp"));
                uhcPlayer.setCooldownData(new CooldownData(resultSet.getString("comp_cooldown")));
                onlinePlayers.put(uhcPlayer.getUuid(), uhcPlayer);

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                createPlayer(player);
            }

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to open connection to MySQL! (load)");
            e.printStackTrace();
        }

    }

    public void unloadPlayer(Player player) {
        if (!onlinePlayers.containsKey(player.getUniqueId())) return;

        UHCPlayer uhcPlayer = onlinePlayers.get(player.getUniqueId());
        try (Connection connection = getAssiGame().getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + SQL_TABLE + "` SET " +
                    "name = ?, kills = ?, deaths = ?, win_count = ?, games_played = ?, previous_games = ?, games_won = ?, rank = ?, level = ?, xp = ?, comp_cooldown = ? WHERE uuid = ?");

            preparedStatement.setString(1, uhcPlayer.getName());
            preparedStatement.setInt(2, uhcPlayer.getKills());
            preparedStatement.setInt(3, uhcPlayer.getDeaths());
            preparedStatement.setInt(4, uhcPlayer.getWinCount());
            preparedStatement.setInt(5, uhcPlayer.getGamesPlayed());
            preparedStatement.setString(6, gson.toJson(uhcPlayer.getPreviousGamesPlayed()));
            preparedStatement.setString(7, gson.toJson(uhcPlayer.getWins()));
            preparedStatement.setString(8, uhcPlayer.getCompRank().name());
            preparedStatement.setInt(9, uhcPlayer.getLevel());
            preparedStatement.setInt(10, uhcPlayer.getXp());
            preparedStatement.setString(11, uhcPlayer.getCooldownData().toString());
            preparedStatement.setString(12, player.getUniqueId().toString());
            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to open connection to MySQL! (unload)");
            e.printStackTrace();
        } finally {
            onlinePlayers.remove(player.getUniqueId());
        }
    }

    public void createPlayer(Player player) {
        if (onlinePlayers.containsKey(player.getUniqueId())) return;
        UHCPlayer uhcPlayer = new UHCPlayer(player.getUniqueId(), player.getName());

        try (Connection connection = getAssiGame().getPlugin().getSqlManager().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + SQL_TABLE + "` " +
                    "(uuid, name, kills, deaths, win_count, games_played, previous_games, games_won, rank, level, xp, comp_cooldown) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, uhcPlayer.getUuid().toString());
            preparedStatement.setString(2, uhcPlayer.getName());
            preparedStatement.setInt(3, uhcPlayer.getKills());
            preparedStatement.setInt(4, uhcPlayer.getDeaths());
            preparedStatement.setInt(5, uhcPlayer.getWinCount());
            preparedStatement.setInt(6, uhcPlayer.getGamesPlayed());
            preparedStatement.setString(7, gson.toJson(uhcPlayer.getPreviousGamesPlayed()));
            preparedStatement.setString(8, gson.toJson(uhcPlayer.getWins()));
            preparedStatement.setString(9, uhcPlayer.getCompRank().name());
            preparedStatement.setInt(10, uhcPlayer.getLevel());
            preparedStatement.setInt(11, uhcPlayer.getXp());
            preparedStatement.setString(12, uhcPlayer.getCooldownData().toString());
            preparedStatement.execute();
            preparedStatement.close();

            onlinePlayers.put(player.getUniqueId(), uhcPlayer);
        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to open connection to MySQL! (create)");
            e.printStackTrace();
        }

    }

    public UHCPlayer getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public UHCPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @Override
    public List<String> getLobbyScoreboardLines(Player player) {
        List<String> lines = Lists.newArrayList();
        UHCPlayer uhcPlayer = getPlayer(player);
//        lines.add(C.C + "Level: " + C.V + uhcPlayer.getLevel());
        lines.add(GC.C + "All-Time Kills: " + GC.V + uhcPlayer.getKills());
        return lines;
    }

}
