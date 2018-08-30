package net.assimilationmc.ellie.assiuhc.backend;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.util.CacheMap;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.games.UHCPlayer;
import org.sql2o.Connection;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SQLManager {

    private UHC uhc;
    private final String TABLE_GAME_LOGS = "assimilation_uhc_games";
    private final String TABLE_PLAYER_DATA = "assimilation_uhc_players";

    private CacheMap<String, UHCPlayer> cache;

    private net.assimilationmc.ellie.assicore.manager.SQLManager manager;

    public SQLManager(UHC uhc) {
        this.uhc = uhc;
        this.manager = ModuleManager.getModuleManager().getSQLManager();
        this.cache = new CacheMap<>(UHC.getPlugin(UHC.class), 900);

        try (Connection connection = manager.getSql2o().open()) {
            connection.createQuery("CREATE TABLE IF NOT EXISTS `" + TABLE_GAME_LOGS + "` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT UNIQUE, " +
                    "`map` VARCHAR(100) NOT NULL, " +
                    "`players` LONGTEXT, " +
                    "`start` BIGINT, " +
                    "`end` BIGINT, " +
                    "INDEX(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;").executeUpdate();

            connection.createQuery("CREATE TABLE IF NOT EXISTS `" + TABLE_PLAYER_DATA + "` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT UNIQUE, " +
                    "`uuid` VARCHAR(100) NOT NULL, " +
                    "`name` VARCHAR(100), " +
                    "`won` INT, " +
                    "`lost` INT, " +
                    "`coins` INT, " +
                    "`kits` LONGTEXT, " +
                    "`achievements` LONGTEXT, " +
                    "`cooldown_strike` INT, " +
                    "`cooldown` TINYINT, " +
                    "`cooldownEnd` BIGINT, " + //future timestmap
                    "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;").executeUpdate().close();
        }
    }

    public UHCPlayer createData(UUID uuid, String name){
        UHCPlayer a = getData(name);
        if(a != null){
            return a;
        }

        UHCPlayer uhcPlayer = new UHCPlayer(uuid, name);
        cache.put(uhcPlayer.getName(), uhcPlayer);

        try(Connection connection = manager.getSql2o().open()){
            connection.createQuery("INSERT INTO `"+TABLE_PLAYER_DATA+"` (uuid, name, won, lost, coins, kits, achievements, cooldown_strike, cooldown, cooldownEnd) " +
                            "VALUES (?, ?, 0, 0, 0, ?, ?, 0, 0, 0);").addParameter("uuid", uuid.toString()).addParameter("name", name).executeUpdate().close();
        }
        return uhcPlayer;
    }

    public UHCPlayer getData(String name){
        if(cache.get(name.toLowerCase()) != null){
            return cache.get(name.toLowerCase());
        }

        try(Connection connection = manager.getSql2o().open()){
            List<UHCPlayer> players = connection.createQuery("SELECT uuid, won, lost, coins, kits, achievements, cooldown_strike, cooldown, cooldownEnd FROM `"+TABLE_PLAYER_DATA+"` WHERE name = :name;")
            .addParameter("name", name).executeAndFetch(UHCPlayer.class);
            connection.close();

            if(!players.isEmpty()){
                UHCPlayer uhcPlayer = players.get(0);

                if(uhcPlayer.getCooldownEnd() <= System.currentTimeMillis()) {
                    uhcPlayer.setCooldownEnd(-1);
                    uhcPlayer.setCooldown(false);
                }
                cache.put(name.toLowerCase(), uhcPlayer);
                return uhcPlayer;
            }
        }

        return null;
    }

    public void pushPlayer(UHCPlayer player){
        try(Connection connection = manager.getSql2o().open()){
            connection.createQuery(
                    "UPDATE `"+TABLE_PLAYER_DATA+"` SET name = :names, won = :won, lost = :lost, coins = :coins, kits = :kits," +
                            " achievements = :achievements, cooldown_strike = :cooldownStrike, cooldown = :cooldown, cooldownEnd :cooldownEnd WHERE uuid = :uuid;")
                    .addParameter("name", player.getName()).addParameter("won", player.getWon()).addParameter("lost", player.getLost()).addParameter("coins", player.getCoins())
                    .addParameter("kits", Util.getGson().toJson(player.getKits())).addParameter("achievements", player.getAchievements())
                    .addParameter("cooldownStrike", player.getCooldownStrike()).addParameter("cooldown", player.isCooldown()).addParameter("cooldownEnd", player.getCooldownEnd())
                    .addParameter("uuid", player.getUUID().toString()).executeUpdate().close();
        }
    }

    public void pushValue(UHCPlayer player, String row, Object value){
        try(Connection connection = manager.getSql2o().open()){
            connection.createQuery("UPDATE `"+TABLE_PLAYER_DATA+"` SET "+row+" = :row WHERE uuid = :uuid;")
            .addParameter(row, value).addParameter("uuid", player.getUUID().toString()).executeUpdate().close();
        }
    }

    public int getWon(String name){
        if(getCachedPlayer(name) != null){
            return getCachedPlayer(name).getWon();
        }

        UHCPlayer player = getData(name);
        return player == null ? -1 : player.getWon();
    }

    public void incrementWon(String name){
        UHCPlayer player = getCachedPlayer(name) != null ? getCachedPlayer(name) : getData(name);
        player.addWon();
        pushValue(player, "won", player.getWon());
    }

    public int getLost(String name){
        if(getCachedPlayer(name) != null){
            return getCachedPlayer(name).getLost();
        }

        UHCPlayer player = getData(name);
        return player == null ? -1 : player.getLost();
    }

    public void incrementLost(String name){
        UHCPlayer player = getCachedPlayer(name) != null ? getCachedPlayer(name) : getData(name);
        player.addLost();
        pushValue(player, "lost", player.getLost());
    }

    public int getCoins(String name){
        if(getCachedPlayer(name) != null){
            return getCachedPlayer(name).getCoins();
        }

        UHCPlayer player = getData(name);
        return player == null ? -1 : player.getCoins();
    }

    public void incrementCoins(String name, int amount){
        UHCPlayer player = getCachedPlayer(name) != null ? getCachedPlayer(name) : getData(name);
        player.addCoins(amount);
        pushValue(player, "coins", player.getCoins());
    }

    public int getCooldownStrike(String name){
        if(getCachedPlayer(name) != null){
            return getCachedPlayer(name).getCooldownStrike();
        }

        UHCPlayer player = getData(name);
        return player == null ? -1 : player.getCooldownStrike();
    }

    public void setCooldownStrike(String name, int amount, boolean increment){
        UHCPlayer player = getCachedPlayer(name) != null ? getCachedPlayer(name) : getData(name);
        if(increment)
            player.setCooldownStrike(player.getCooldownStrike()+amount);
        else
            player.setCooldownStrike(amount);
        pushValue(player, "cooldown_strike", player.getCooldownStrike());
    }

    public boolean onCooldown(String name){
        if(getCachedPlayer(name) != null){
            return getCachedPlayer(name).isCooldown();
        }

        UHCPlayer player = getData(name);
        return player != null && player.isCooldown();
    }

    public void setOnCooldown(String name, boolean onCooldown){
        UHCPlayer player = getCachedPlayer(name) != null ? getCachedPlayer(name) : getData(name);
        player.setCooldown(onCooldown);
        pushValue(player, "cooldown", player.isCooldown());
    }

    public long getCooldownEnd(String name){
        if(getCachedPlayer(name) != null){
            return getCachedPlayer(name).getCooldownEnd();
        }

        UHCPlayer player = getData(name);
        return player == null ? -1 : player.getCooldownEnd();
    }

    public void setCooldownEnd(String name, long end){
        UHCPlayer player = getCachedPlayer(name) != null ? getCachedPlayer(name) : getData(name);
        player.setCooldownEnd(end);
        pushValue(player, "cooldownEnd", player.getCooldownEnd());
    }

    public UHCPlayer getCachedPlayer(String player){
        return cache.get(player.toLowerCase());
    }

    public CacheMap<String, UHCPlayer> getCache() {
        return cache;
    }

}
