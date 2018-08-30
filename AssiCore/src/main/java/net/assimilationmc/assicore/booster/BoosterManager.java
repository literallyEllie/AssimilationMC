package net.assimilationmc.assicore.booster;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.booster.command.CmdBooster;
import net.assimilationmc.assicore.booster.command.CmdBoosterAdmin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.assicore.util.UtilTime;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BoosterManager extends Module {

    private Map<BoosterType, Booster> boosters;
    private ActiveBooster activeBooster;
    private int boosterTask;

    public BoosterManager(AssiPlugin plugin) {
        super(plugin, "Booster Manager");
    }

    @Override
    protected void start() {
        this.boosters = Maps.newHashMap();
        boosterTask = 0;

        registerBooster(new Booster("15_DOUBLE_BUCKS", "x2 Bucks 15 min",
                "All Buck rewards will be doubled for 15 minutes.", BoosterType.BUCKS, TimeUnit.MINUTES.toMillis(15), 15) {
            @Override
            public int processBucks(int in) {
                return in * 2;
            }
        });

        registerBooster(new Booster("30_DOUBLE_BUCKS", "x2 Bucks 30 min",
                "All Buck rewards will be doubled for 30 minutes.", BoosterType.BUCKS, TimeUnit.MINUTES.toMillis(30), 25) {
            @Override
            public int processBucks(int in) {
                return in * 2;
            }
        });

        registerBooster(new Booster("15_DOUBLE_UC", "x2 Ultra Coins 15 min",
                "All Ultra Coin rewards will be doubled for 15 minutes.", BoosterType.ULTRA_COIN, TimeUnit.MINUTES.toMillis(15), 30) {
            @Override
            public int processBucks(int in) {
                return in * 2;
            }
        });

        registerBooster(new Booster("30_DOUBLE_UC", "x2 Ultra Coins 30 min",
                "All Ultra Coin rewards will be doubled for 30 minutes.", BoosterType.ULTRA_COIN, TimeUnit.MINUTES.toMillis(30), 45) {
            @Override
            public int processUC(int in) {
                return in * 2;
            }
        });

        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(),
                () -> getPlugin().getCommandManager().registerCommand(new CmdBooster(this), new CmdBoosterAdmin(getPlugin())), 60);

    }

    @Override
    protected void end() {

        if (boosterTask != 0) {
            getPlugin().getServer().getScheduler().cancelTask(boosterTask);
        }

        boosters.clear();
    }

    public void registerBooster(Booster booster) {
        boosters.put(booster.getBoosterType(), booster);
    }

    public void unregisterBooster(BoosterType boosterType) {
        boosters.remove(boosterType);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerJoinEvent e) {
        if (activeBooster == null) return;

        e.getPlayer().sendMessage(C.C);
        e.getPlayer().spigot().sendMessage(new ComponentBuilder(C.SS)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Activator: ").color(net.md_5.bungee.api.ChatColor.GRAY)
                        .append(activeBooster.getStarter() + "\n").color(net.md_5.bungee.api.ChatColor.BLUE)
                        .append("Description: ").color(net.md_5.bungee.api.ChatColor.GRAY)
                        .append(activeBooster.getBooster().getDescription() + "\n").color(net.md_5.bungee.api.ChatColor.BLUE)
                        .append("Expires in: ").color(net.md_5.bungee.api.ChatColor.GRAY)
                        .append(UtilTime.formatTimeStamp(activeBooster.getRemaining()) + "\n\n").color(net.md_5.bungee.api.ChatColor.BLUE)
                        .append("Click to show your appreciation by tipping ").color(net.md_5.bungee.api.ChatColor.AQUA).append(activeBooster.getStarter())
                        .color(net.md_5.bungee.api.ChatColor.AQUA).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/boosters"))

                .append("The booster").color(net.md_5.bungee.api.ChatColor.AQUA).append(activeBooster.getBooster().getPretty())
                .append(" is currently active!").color(net.md_5.bungee.api.ChatColor.AQUA).append(" (Hover over to see more info)")
                .italic(true).color(net.md_5.bungee.api.ChatColor.GRAY).create());
        e.getPlayer().sendMessage(C.C);

    }

    public void startBooster(AssiPlayer starter, Booster booster) {
        if (activeBooster != null) {
            starter.sendMessage(C.SS + "There is already a booster started!");
            return;
        }

        UtilServer.broadcast("");
        UtilServer.broadcast(C.SS + starter.getDisplayName() + ChatColor.AQUA + " has activated a " + booster.getPretty() + ChatColor.AQUA + " booster!");
        UtilServer.broadcast(C.C + "It will last " + C.V + TimeUnit.MILLISECONDS.toMinutes(booster.getLength()) + C.C + " minutes.");
        UtilServer.broadcast("");

        activeBooster = new ActiveBooster(starter.getName(), booster);

        boosterTask = getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), this::stopBooster, booster.getLength()).getTaskId();

        starter.removeBooster(booster.getId());
    }

    public void stopBooster() {
        if (activeBooster == null) return;

        UtilServer.broadcast(C.SS + "The booster " + C.V + activeBooster.getBooster().getPretty() + C.C + " has ended!");
        activeBooster = null;

        getPlugin().getServer().getScheduler().cancelTask(boosterTask);
        boosterTask = 0;
    }

    public Map<BoosterType, Booster> getBoosters() {
        return boosters;
    }

    public Booster getBooster(String id) {
        for (Map.Entry<BoosterType, Booster> boosterTypeBoosterEntry : boosters.entrySet()) {
            if (boosterTypeBoosterEntry.getValue().getId().equalsIgnoreCase(id))
                return boosterTypeBoosterEntry.getValue();
        }

        return null;
    }

    public ActiveBooster getActiveBooster() {
        return activeBooster;
    }

}
