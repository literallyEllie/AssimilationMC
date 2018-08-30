package net.assimilationmc.ellie.assiuhc.games;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Ellie on 22/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCPlayer {

    private UUID uuid;
    private String name;
    private int won;
    private int lost;
    private int coins;
    private Set<String> kits;
    private Set<String> achievements;

    private int cooldownStrike;
    private boolean isCooldown;
    private long cooldownEnd;

    public UHCPlayer(){
    }

    public UHCPlayer(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.won = -1;
        this.lost = -1;
        this.coins = -1;
        this.kits = new HashSet<>();
        this.achievements = new HashSet<>();
        this.cooldownStrike = -1;
        this.isCooldown = false;
        this.cooldownEnd = -1L;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public void addWon(){
        this.won = won + 1;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public void addLost(){
        this.lost = lost + 1;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int coins){
        this.coins = coins + coins;
    }

    public Set<String> getKits() {
        return kits;
    }

    public void setKits(Set<String> kits) {
        this.kits = kits;
    }

    public Set<String> getAchievements() {
        return achievements;
    }

    public void setAchievements(Set<String> achievements) {
        this.achievements = achievements;
    }

    public int getCooldownStrike() {
        return cooldownStrike;
    }

    public void setCooldownStrike(int cooldownStrike) {
        this.cooldownStrike = cooldownStrike;
    }

    public boolean isCooldown() {
        return isCooldown;
    }

    public void setCooldown(boolean cooldown) {
        isCooldown = cooldown;
    }

    public long getCooldownEnd() {
        return cooldownEnd;
    }

    public void setCooldownEnd(long cooldownEnd) {
        this.cooldownEnd = cooldownEnd;
    }

}
