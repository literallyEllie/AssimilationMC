package net.assimilationmc.assicore.patch.uuidpool;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.patch.AssiPatch;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PatchUUIDPool implements AssiPatch, Listener {

    private AssiPlugin assiPlugin;
    private UUIDFetcher uuidFetcher;

    public PatchUUIDPool(AssiPlugin plugin) {
        this.assiPlugin = plugin;
        plugin.registerListener(this);
    }

    @Override
    public void load() {
        this.uuidFetcher = new UUIDFetcher();
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void on(final PlayerLoginEvent e) {
        final Player player = e.getPlayer();

        assiPlugin.getServer().getScheduler().runTaskAsynchronously(assiPlugin, () -> {
            if (!check(player)) {
                e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.DARK_GREEN + "Assi" + ChatColor.GREEN + "miliationMC\n" + C.II + "\n" +
                        "You have been disconnected as your UUID was evaluated to be invalid.\n" +
                        "If you believe this is an error, please message a member of staff at " + C.V + Domain.DISCORD);
            }
        });


    }

    private boolean check(Player player) {
        final String s = uuidFetcher.fetchUUID(player.getName());
        return s.equals(player.getUniqueId().toString().replace("-", ""));
    }


}
