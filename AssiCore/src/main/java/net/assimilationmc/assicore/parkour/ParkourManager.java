package net.assimilationmc.assicore.parkour;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.achievement.AchievementCategory;
import net.assimilationmc.assicore.achievement.achievements.gameplay.AchievePkImprove;
import net.assimilationmc.assicore.cosmetic.cosmetics.particle.CosmeticDiamonds;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.leaderboard.LeaderboardEntity;
import net.assimilationmc.assicore.leaderboard.LeaderboardManager;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.BuckRewards;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilTime;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.inventivetalent.particle.ParticleEffect;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ParkourManager extends Module {

    private boolean enabled;
    private String prefix;
    private Map<UUID, ParkourPlayer> parkourPlayerMap;

    private List<Location> checkpoints;
    private int fellY;

    private String table;

    private Map<String, Long> bestTimes;

    public ParkourManager(AssiPlugin plugin) {
        super(plugin, "Parkour Manager");
    }

    @Override
    protected void start() {
        this.checkpoints = Lists.newLinkedList();
        this.parkourPlayerMap = Maps.newHashMap();

        this.prefix = ChatColor.GREEN + ChatColor.BOLD.toString() + "Parkour" + C.SS;
        this.enabled = loadParkour();

        if (enabled) {

            getPlugin().getCommandManager().registerCommand(new CmdParkour(this));

            this.table = "parkour_stats";

            try (Connection connection = getPlugin().getSqlManager().getConnection()) {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
                        "`uuid` VARCHAR(100) NOT NULL UNIQUE," +
                        "`name` VARCHAR(100) NOT NULL, " +
                        "`personal_best` BIGINT," +
                        "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8;").execute();
            } catch (SQLException e) {
                log(Level.SEVERE, "Failed to do opening statement to SQL.");
                e.printStackTrace();
            }


            this.bestTimes = Maps.newLinkedHashMap();

            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> on(new UpdateEvent(UpdateType.TEN_MIN)), 60L);
        }

    }

    @Override
    protected void end() {
        parkourPlayerMap.clear();
        checkpoints.clear();
        if (enabled) bestTimes.clear();
    }

    public void startParkour(Player player) {
        final ParkourPlayer parkourPlayer = getPlayer(player);
        if (parkourPlayer.isRunning()) {
            finishParkour(player, parkourPlayer, false);
            return;
        }

        player.setFlying(false);
        player.setAllowFlight(false);

        player.getInventory().clear();

        player.teleport(checkpoints.get(0));
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "GO GO GO!");
        player.playSound(player.getLocation(), Sound.CLICK, 5f, 3f);
        parkourPlayer.start();
    }

    public void restart(ParkourPlayer player) {
        if (!player.isRunning()) {
            return;
        }

        player.getPlayer().setFlying(false);
        player.getPlayer().setAllowFlight(false);
        player.getPlayer().getInventory().clear();

        player.getPlayer().teleport(checkpoints.get(0));
        player.getPlayer().sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "GO GO GO!");
        player.getPlayer().sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "You can do it this time!!");
        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 5f, 3f);
        player.reset();
    }

    public void finishParkour(Player bPlayer, ParkourPlayer player, boolean finishedCourse) {
        if (!player.isRunning()) {
            return;
        }

        player.finish();

        final AssiPlayer assiPlayer = getPlugin().getPlayerManager().getPlayer(bPlayer);

        if (finishedCourse) {
            bPlayer.sendMessage(ChatColor.GREEN + "Wow, to be honest, I didn't think you would actually finish it. Good job.");

            if (player.getPersonalBest() == 0 || player.getStopwatch().elapsed(TimeUnit.MILLISECONDS) < player.getPersonalBest()) {

                if (player.getPersonalBest() != 0 && getPlugin().getAchievementManager().getAchievements(AchievementCategory.ECONOMIC).get("PK_IMPROVE") != null) {
                    AchievePkImprove achievePkImprove = (AchievePkImprove) getPlugin().getAchievementManager().getAchievement("PK_IMPROVE");
                    achievePkImprove.onBeat(assiPlayer);
                }

                bPlayer.sendMessage("");
                bPlayer.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "NEW PERSONAL BEST");
                bPlayer.sendMessage("");
                player.setPersonalBest(player.getStopwatch().elapsed(TimeUnit.MILLISECONDS));

                bPlayer.playSound(bPlayer.getLocation(), Sound.LEVEL_UP, 5f, 3f);
            }

            bPlayer.sendMessage(prefix + ChatColor.GOLD + "You have finished in " + C.V + player.getStopwatch().toString() + ChatColor.GOLD + "!");
            getPlugin().getRewardManager().giveBucks(assiPlayer, BuckRewards.PK_FINISH);

            updateData(player);

        } else {

            bPlayer.sendMessage(prefix + ChatColor.RED + "You are no longer doing Parkour. " + C.C + "Better luck next time!");

        }

        final boolean higherThanOrEqualTo = assiPlayer.getRank().isHigherThanOrEqualTo(Rank.DEMONIC);
        bPlayer.setAllowFlight(higherThanOrEqualTo);
        bPlayer.setFlying(higherThanOrEqualTo);
        getPlugin().getJoinItemManager().give(assiPlayer);

    }

    public void addCheckpoint(Location location) {
        checkpoints.add(location);
    }

    private boolean loadParkour() {
        File parkourFile = new File(getPlugin().getDataFolder(), "parkour.yml");
        if (!parkourFile.exists()) {
            log("");
            log(Level.SEVERE, "parkour.yml does not exist!");
            log("");
            return false;
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(parkourFile);

        for (String chkId : yamlConfiguration.getConfigurationSection("checkpoints").getKeys(false)) {
            String strWorld = yamlConfiguration.getString("checkpoints." + chkId + ".world");
            World world = Bukkit.getWorld(strWorld);
            if (world == null) {
                log(Level.WARNING, "Checkpoint " + chkId + " world `" + strWorld + "` does not exist!");
                continue;
            }

            double x = yamlConfiguration.getDouble("checkpoints." + chkId + ".x");
            double y = yamlConfiguration.getDouble("checkpoints." + chkId + ".y");
            double z = yamlConfiguration.getDouble("checkpoints." + chkId + ".z");
            float yaw = (float) yamlConfiguration.getDouble("checkpoints." + chkId + ".yaw");
            float pitch = (float) yamlConfiguration.getDouble("checkpoints." + chkId + ".pitch");

            checkpoints.add(new Location(world, x, y, z, yaw, pitch));
        }

        this.fellY = yamlConfiguration.getInt("fellY");

        return true;
    }

    public ParkourPlayer getPlayer(Player player) {
        ParkourPlayer parkourPlayer = parkourPlayerMap.get(player.getUniqueId());
        if (parkourPlayer != null)
            return parkourPlayer;

        parkourPlayer = new ParkourPlayer(player);

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT personal_best FROM `" + table + "` WHERE uuid = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                long personalBest = resultSet.getLong("personal_best");

                parkourPlayer.setPersonalBest(personalBest);
            }

            preparedStatement.close();

        } catch (SQLException e) {
            log(Level.SEVERE, "Failed to get player stats from DB " + player.getUniqueId());
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an internal error trying to get your stats from the database.");
            return null;
        }

        parkourPlayerMap.put(player.getUniqueId(), parkourPlayer);

        return parkourPlayer;
    }

    private void updateData(ParkourPlayer player) {

        try (Connection connection = getPlugin().getSqlManager().getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `" + table + "` (uuid, name, personal_best) VALUES " +
                    "(?, ?, ?) ON DUPLICATE KEY UPDATE name = ?, personal_best = ?");
            preparedStatement.setString(1, player.getPlayer().getUniqueId().toString());
            preparedStatement.setString(2, player.getPlayer().getName());
            preparedStatement.setLong(3, player.getPersonalBest());
            preparedStatement.setString(4, player.getPlayer().getName());
            preparedStatement.setLong(5, player.getPersonalBest());

            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            log("Failed to update data of " + player.getPlayer().getUniqueId());
            e.printStackTrace();
        }

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        if (!enabled) return;

        final ParkourPlayer parkourPlayer = parkourPlayerMap.get(e.getPlayer().getUniqueId());
        if (parkourPlayer != null) {
            updateData(parkourPlayer);
            parkourPlayer.finish();
            parkourPlayerMap.remove(e.getPlayer().getUniqueId());
        }

    }

    @EventHandler
    public void on(final PlayerMoveEvent e) {
        if (!enabled) return;
        final Player player = e.getPlayer();
        ParkourPlayer parkourPlayer = parkourPlayerMap.get(player.getUniqueId());
        if (parkourPlayer == null || !parkourPlayer.isRunning()) return;

        if (player.isFlying()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.sendMessage(C.II + ChatColor.BOLD + "No flying!");
            player.teleport(checkpoints.get(parkourPlayer.getLastCheckpoint()));
            return;
        }

        if (player.getLocation().getY() <= fellY) {
            player.teleport(checkpoints.get(parkourPlayer.getLastCheckpoint()));
            player.sendMessage(prefix + C.II + "You have been returned to your last checkpoint.");
            return;
        }

    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() == UpdateType.TEN_MIN) {

            List<LeaderboardEntity> leaderboardEntities = getPlugin().getLeaderboardManager().getLeaderboardsOfType("PK");
            if (leaderboardEntities.isEmpty()) return;

            bestTimes.clear();

            try (Connection connection = getPlugin().getSqlManager().getConnection()) {
                final PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, personal_best FROM `" +
                        table + "` WHERE NOT personal_best = 0 ORDER BY personal_best ASC LIMIT 3");

                final ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    bestTimes.put(resultSet.getString("name"), resultSet.getLong("personal_best"));
                }

                resultSet.close();
                preparedStatement.close();

                int index = 0;
                for (Map.Entry<String, Long> stringLongEntry : bestTimes.entrySet()) {
                    if (index > leaderboardEntities.size()) break;
                    final String name = stringLongEntry.getKey();
                    final Long time = stringLongEntry.getValue();

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
                        sign.setLine(2, ChatColor.RED.toString() + ChatColor.BOLD + "Time");
                        sign.setLine(3, C.V + TimeUnit.MILLISECONDS.toSeconds(time) + "s");

                        sign.update(true);

                    }

                    index++;
                }

            } catch (SQLException ex) {
                log(Level.WARNING, "Error trying to update Parkour leaderboard NPCs!");
                ex.printStackTrace();
            }

            return;
        }

    }

    public Map<UUID, ParkourPlayer> getParkourPlayerMap() {
        return parkourPlayerMap;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, Long> getBestTimes() {
        return bestTimes;
    }

}
