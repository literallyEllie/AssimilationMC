package net.assimilationmc.assiuhc.border;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.util.*;
import net.assimilationmc.assicore.world.WorldData;
import net.assimilationmc.gameapi.game.AssiGame;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class GameBorder extends GameModule {

    private final int START_SIZE = 510;

    private Location center;
    private double borderDamage;

    private int time = 10;
    private int nextSize, nextTime, timeUntilNext;
    private String nextSizeDisplay;

    private int borderShrink = 1;

    private WorldBorder worldBorder;

    private int finishCounter, hitPoints;

    public GameBorder(AssiGame plugin) {
        super (plugin, "Game Border", ModuleActivePolicy.WARMUP_GAME_END);
    }

    @Override
    public void start() {

        final WorldData selectedWorld = getAssiGame().getGameMapManager().getSelectedWorld();
        if (selectedWorld.getSpawns().containsKey("CENTER")) {
            this.center = selectedWorld.getSpawns().get("CENTER").toLocation();

            worldBorder = center.getWorld().getWorldBorder();
            worldBorder.setCenter(center.getX(), center.getZ());
            worldBorder.setDamageAmount(borderDamage);
            worldBorder.setDamageBuffer(5);
            setInitSize(START_SIZE);

            // D.d("init values:");
            // D.d("timeuntil next = 180");
            // D.d("next time  = 180");
            // D.d("time = " + time);

            // getNextSize();

        } else log(Level.SEVERE, "There is no CENTER spawn set!");

    }

    @Override
    public void end() {
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() != UpdateType.SEC || worldBorder == null) return;

        if (getAssiGame().getGamePhase() == GamePhase.END || getAssiGame().getGamePhase() == GamePhase.WARMUP)
            return;

        final List<Player> livePlayers = getAssiGame().getLivePlayers();

        if (worldBorder.getSize() <= 26) {
            finishCounter++;

            if (finishCounter == 1) {

                livePlayers.forEach(player -> UtilMessage.sendFullTitle(player, ChatColor.RED + ChatColor.BOLD.toString() + "FIGHT",
                        ChatColor.RED + "The border has now reached its final size.", 20, 3 * 20, 20));

                return;
            }

            if (finishCounter % 60 == 0) {
                hitPoints += 2;

                if (finishCounter == 180) {
                    hitPoints += 4;
                }

                livePlayers.forEach(player -> player.damage(hitPoints));
            }

            return;
        }

        time--;
        if (nextTime > -1) {
            nextTime--;
        }

        // D.d("time = " + time);
        // D.d("nextTime = " + nextTime);
        // D.d("nextSize = " + nextSize);
        // D.d("borderShrink = " + borderShrink);
        // D.d("borderDamage = " + borderDamage);

        if (time > 0) {
            String displayTime = UtilTime.formatMinutes(time, false);
            Bukkit.getOnlinePlayers().forEach(o -> UtilMessage.sendHotbar(o, ChatColor.YELLOW + "Border now shrinking to " + nextSizeDisplay
                    + " - Arriving in " + GC.V + displayTime));
        }

        if (nextTime == 0) {
            nextTime = -1;
            //  borderShrink++;
            borderDamage++;

            // D.d("calling next size");
            // D.d("size " + nextSizeDisplay);
            // D.d("time = " + time);
            //  D.d("time til next = " + timeUntilNext);
            //  D.d("time til next = " + timeUntilNext);
            getNextSize();
        }

        // if (time < 11 && time > 0) {
        //  livePlayers.forEach(player -> player.playSound(player.getLocation(), Sound.CLICK, 1,1));
        // }

        if (time == 0) {
            // D.d("time is now 0");
            time = -1;
            timeUntilNext -= 55;
            // D.d("time until next " + timeUntilNext);
            nextTime = timeUntilNext;

            String nextTimeDisplay = UtilTime.formatMinutes(nextTime, false);

            livePlayers.forEach(player -> UtilMessage.sendFullTitle(player, ChatColor.RED + "The border has shrunk!",
                    ChatColor.YELLOW + "The next shrink is in " + nextTimeDisplay, 20, 2 * 20, 20));
            UtilServer.broadcast(ChatColor.RED + "Border shrunk, next shrink in " + C.V + nextTimeDisplay);
        }

        if (nextTime > 0 && time < 0) {
            Bukkit.getOnlinePlayers().forEach(o -> UtilMessage.sendHotbar(o, ChatColor.RED + ChatColor.ITALIC.toString()
                    + "Border shrink in " + UtilTime.formatMinutes(nextTime, false)));
        }

        // announce to get to zone

        for (Player livePlayer : livePlayers) {
            if (!isOutsideOfBorder(livePlayer)) {
                livePlayer.resetPlayerWeather();
                continue;
            }

            livePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
            livePlayer.getWorld().playSound(livePlayer.getLocation(), Sound.IRONGOLEM_HIT, 1, -1);

            for (int c = 0; c < new Random().nextInt(8); c++) {
                int x = livePlayer.getLocation().getBlockX() + new Random().nextInt(24) - 12;
                int z = livePlayer.getLocation().getBlockX() + new Random().nextInt(24) - 12;

                Location l = new Location(center.getWorld(), x, livePlayer.getLocation().getY(), z);
                l.getWorld().strikeLightning(l);
            }

            livePlayer.setPlayerWeather(WeatherType.DOWNFALL);
        }

    }

    private void getNextSize() {
        List<Integer> distances = Lists.newArrayList();

        final List<Player> livePlayers = getAssiGame().getLivePlayers();

        for (Player player : livePlayers) {
            if (!player.getWorld().getName().equals(getAssiGame().getGameMapManager().getSelectedWorld().getName())) continue;
            distances.add((int) player.getLocation().distance(center));
        }

        if (distances.isEmpty()) return;

        Collections.sort(distances);

        for (int i = 0; i < (livePlayers.size() / 4) * 3; i++) {
            distances.remove(i);
        }

        this.nextSize = (int) Math.ceil(distances.get(0));
        this.time = (int) (worldBorder.getSize() - nextSize / borderShrink) - 4;

        this.nextSizeDisplay = nextSize + "x" + nextSize;
        String timeDisplay = UtilTime.formatMinutes(time, false);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (livePlayers.contains(player)) {
                UtilMessage.sendFullTitle(player, ChatColor.RED + "Zone shrinking to " + nextSizeDisplay,
                        ChatColor.YELLOW + "You have " + C.V + timeDisplay + ChatColor.YELLOW + " to get there", 20, 2 * 20, 20);
            }

            player.sendMessage(ChatColor.YELLOW + "Zone shrinking to " + nextSizeDisplay);
        }

        worldBorder.setSize(nextSize, time);
        worldBorder.setDamageBuffer(worldBorder.getDamageBuffer() - 1);
        worldBorder.setDamageAmount(borderDamage);

    }

    @EventHandler
    public void on(final GamePhaseChangeEvent e) {
        if (e.getTo() == GamePhase.IN_GAME) {
            getNextSize();
        }

        if (e.getTo() == GamePhase.END) {
            worldBorder.setSize(worldBorder.getSize());
        }
    }

    public boolean isOutsideOfBorder(Player p) {
        Location loc = p.getLocation();
        double size = worldBorder.getSize() / 2;
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    public WorldBorder getWorldBorder() {
        return worldBorder;
    }

    public void setInitSize(int size) {
        worldBorder.setSize(size);

        this.time = (int) (worldBorder.getSize() - nextSize / borderShrink) - 4;

        this.timeUntilNext = time;
        this.nextTime = timeUntilNext;
    }

}
