package net.assimilationmc.uhclobbyadaptor.stats;

import com.google.common.collect.Maps;
import net.assimilationmc.uhclobbyadaptor.stats.comp.CompRank;
import net.assimilationmc.uhclobbyadaptor.stats.comp.CooldownData;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;

import java.util.Map;
import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private String name;
    private int kills, deaths, winCount, gamesPlayed;
    private Map<UHCGameSubType, Integer> previousGamesPlayed, gamesWon;
    private CompRank rank;
    private int level, xp;

    private CooldownData cooldownData;

    public UHCPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.previousGamesPlayed = Maps.newHashMap();
        this.gamesWon = Maps.newHashMap();
        defaultValues();
    }

    public void defaultValues() {
        this.kills = deaths = winCount = gamesPlayed = 0;
        this.level = 0;
        this.xp = 0;
        this.rank = CompRank.UNRANKED;
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

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public Map<UHCGameSubType, Integer> getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(Map<UHCGameSubType, Integer> gamesWon) {
        this.gamesWon = gamesWon;
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

    public Map<UHCGameSubType, Integer> getPreviousGamesPlayed() {
        return previousGamesPlayed;
    }

    public void setPreviousGamesPlayed(Map<UHCGameSubType, Integer> previousGamesPlayed) {
        this.previousGamesPlayed = previousGamesPlayed;
    }

    public CompRank getCompRank() {
        return this.rank;
    }

    public void setCompRank(CompRank rank) {
        this.rank = rank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public CooldownData getCooldownData() {
        return cooldownData;
    }

    public void setCooldownData(CooldownData cooldownData) {
        this.cooldownData = cooldownData;
    }
}
