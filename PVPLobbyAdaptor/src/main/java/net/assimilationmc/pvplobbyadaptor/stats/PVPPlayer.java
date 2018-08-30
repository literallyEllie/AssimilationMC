package net.assimilationmc.pvplobbyadaptor.stats;

import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PVPPlayer {

    private final UUID uuid;
    private String name;

    private int level, xp, kills, deaths;

    public PVPPlayer(UUID uuid) {
        this.uuid = uuid;
        this.name = "";
        this.level = 0;
        this.kills = 0;
        this.deaths = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void addXp(int xp) {
        this.xp += xp;
        checkXp();
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
        setDeaths(deaths + 1);
    }

    public float kd() {
        if (kills == 0 || deaths == 0) {
            return 0;
        }
        return ((float) kills / (float) deaths);
    }

    private void checkXp() {
        int xpToGo = 250 * (level + 1);

        if (this.xp >= xpToGo) {
            addLevel();

            final Player player = UtilPlayer.get(uuid);
            if (player != null) {
                player.sendMessage(C.SS + ChatColor.GREEN + "You have leveled up!");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 3f, .5f);
                player.sendMessage(C.C + "XP to go: " +  (250 * (level + 1) - this.xp));
            }

            setXp(this.xp - xpToGo);
            checkXp();
        }
    }


}
