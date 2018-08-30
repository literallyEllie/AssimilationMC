package net.assimilationmc.assicore.scoreboard;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.player.AssiPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class ScoreboardManager extends Module {

    private String sidebarTitle;

    private ScoreboardPolicy scoreboardPolicy;
    private Map<UUID, AssiScore> scoreMap;
    private long lastUpdate;

    public ScoreboardManager(AssiPlugin plugin) {
        super(plugin, "Scoreboard Manager");
    }

    @Override
    protected void start() {
        this.sidebarTitle = ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "miliationMC";
        this.scoreMap = Maps.newHashMap();
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    protected void end() {
        scoreMap.values().forEach(AssiScore::unregister);
        scoreMap.clear();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerJoinEvent e) {
        AssiPlayer player = getPlugin().getPlayerManager().getOnlinePlayers().get(e.getPlayer().getUniqueId());

        if (scoreMap.containsKey(player.getUuid())) {
            scoreMap.get(player.getUuid()).update();
        } else scoreMap.put(player.getUuid(), new AssiScore(this, player));

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        if (scoreMap.containsKey(e.getPlayer().getUniqueId())) {
            scoreMap.get(e.getPlayer().getUniqueId()).unregister();
            scoreMap.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void on(final UpdateEvent e) {
        if (e.getType() == UpdateType.SEC) {
            update();
        }
    }

    public void update() {
        update(true);
    }

    public void update(boolean checkCooldown) {
        if (!checkCooldown || System.currentTimeMillis() - lastUpdate >= 1000L) {
            lastUpdate = System.currentTimeMillis();
            scoreMap.values().forEach(AssiScore::update);
        }
    }

    public void update(AssiPlayer player) {
        if (scoreMap.containsKey(player.getUuid()))
            scoreMap.get(player.getUuid()).update();
    }

    public String trimPrefix(String prefix) {
        return prefix == null ? null : prefix.substring(0, Math.min(prefix.length(), 16));
    }

    public void setScoreTitle(String title) {
        this.sidebarTitle = title;
        scoreMap.values().forEach(assiScore -> assiScore.setTitle(title));
    }

    public String getSidebarTitle() {
        return sidebarTitle;
    }

    public ScoreboardPolicy getScoreboardPolicy() {
        return scoreboardPolicy;
    }

    public void setScoreboardPolicy(ScoreboardPolicy scoreboardPolicy) {
        if (scoreboardPolicy == this.scoreboardPolicy) return;
        this.scoreboardPolicy = scoreboardPolicy;
    }
}
