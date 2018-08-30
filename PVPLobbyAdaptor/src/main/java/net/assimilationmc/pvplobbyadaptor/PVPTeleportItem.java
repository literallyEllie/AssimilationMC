package net.assimilationmc.pvplobbyadaptor;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.joinitems.JoinItem;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class PVPTeleportItem extends JoinItem {

    private final PVPLobbyAdaptor pvpLobbyAdaptor;
    private List<Player> queue;

    public PVPTeleportItem(PVPLobbyAdaptor pvpLobbyAdaptor) {
        super(1, new ItemBuilder(Material.IRON_SWORD)
                .setDisplay(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Warmup").
                        setLore("", ChatColor.GRAY + "An AssimilationMC style KitPvP",
                                ChatColor.GRAY + "for you to warmup your skills").build()); // TODO
        this.pvpLobbyAdaptor = pvpLobbyAdaptor;
        this.queue = Lists.newArrayList();
    }

    @Override
    public void onClick(Player player) {
        if (queue.contains(player)) {
            player.sendMessage(ChatColor.RED + "Please do not spam the teleporter, your data is being setup. " +
                    "If this is taking an unusually long time, please contact a member of staff.");
            return;
        }

        queue.add(player);
        pvpLobbyAdaptor.getServer().getScheduler().runTaskAsynchronously(pvpLobbyAdaptor, () -> pvpLobbyAdaptor.getServer().getScheduler().runTask(pvpLobbyAdaptor, () -> {
            pvpLobbyAdaptor.getPvpStatsProvider().getPlayer(player);
            player.teleport(pvpLobbyAdaptor.getSpawn());
            pvpLobbyAdaptor.getAssiPlugin().getScoreboardManager().update(pvpLobbyAdaptor.getAssiPlugin().getPlayerManager().getPlayer(player));
            queue.remove(player);
        }));

    }

}
