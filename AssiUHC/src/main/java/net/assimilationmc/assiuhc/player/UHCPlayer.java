package net.assimilationmc.assiuhc.player;

import com.google.common.collect.Maps;
import net.assimilationmc.assiuhc.comp.CompRank;
import net.assimilationmc.assiuhc.comp.cooldown.CooldownData;
import net.assimilationmc.assiuhc.game.UHCGameSubType;
import net.assimilationmc.assiuhc.reward.XPManager;

import java.util.Map;
import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private String name;
    private int kills, deaths, winCount, gamesPlayed;
    private Map<UHCGameSubType, Integer> previousGamesPlayed;
    private Map<UHCGameSubType, Integer> wins;
    private CompRank compRank;
    private int level, xp;
    private CooldownData cooldownData;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.previousGamesPlayed = Maps.newHashMap();
        this.wins = Maps.newHashMap();
        defaultValues();
    }

    public void defaultValues() {
        this.kills = deaths = winCount = gamesPlayed = 0;
        this.level = XPManager.DEFAULT_LEVEL;
        this.xp = XPManager.DEFAULT_XP;
        this.compRank = CompRank.UNRANKED;
        this.cooldownData = new CooldownData();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        setKills(kills + 1);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addDeath() {
        setDeaths(this.deaths + 1);
    }

    public Map<UHCGameSubType, Integer> getWins() {
        return wins;
    }

    public void setWins(Map<UHCGameSubType, Integer> wins) {
        this.wins = wins;
    }

    public void addGameWon(UHCGameSubType subType) {
        wins.put(subType, wins.getOrDefault(subType, 0) + 1);
        setWinCount(winCount + 1);
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    private void addGamePlayed() {
        setGamesPlayed(this.gamesPlayed + 1);
    }

    public Map<UHCGameSubType, Integer> getPreviousGamesPlayed() {
        return previousGamesPlayed;
    }

    public void setPreviousGamesPlayed(Map<UHCGameSubType, Integer> previousGamesPlayed) {
        this.previousGamesPlayed = previousGamesPlayed;
    }

    public void addGamePlayed(UHCGameSubType uhcGameSubType) {
        if (previousGamesPlayed.containsKey(uhcGameSubType)) {
            int placed = previousGamesPlayed.get(uhcGameSubType);
            previousGamesPlayed.replace(uhcGameSubType, placed + 1);
            addGamePlayed();
            return;
        }
        previousGamesPlayed.put(uhcGameSubType, 1);
        addGamePlayed();
    }

    public CompRank getCompRank() {
        return compRank;
    }

    public void setCompRank(CompRank compRank) {
        this.compRank = compRank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addLevel() {
        setLevel(level + 1);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXP(int xp) {
        setXp(this.xp + xp);
    }

    public CooldownData getCooldownData() {
        return cooldownData;
    }

    public void setCooldownData(CooldownData cooldownData) {
        this.cooldownData = cooldownData;
    }

}
