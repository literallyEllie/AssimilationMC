package net.assimilationmc.assicore.internal;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class SpecialHeadListener implements Listener {

    private final Map<String, String> messages;

    public SpecialHeadListener() {
        this.messages = Maps.newHashMap();

        messages.put("twitter", C.C + "Follow us on " + ChatColor.AQUA + "Twitter" + C.C + "! Find us at " + C.V + Domain.TWITTER);
        messages.put("discord", C.C + "Join our " + ChatColor.BLUE + "Community Discord" + C.C + " at " + C.V + Domain.DISCORD);

    }

    @EventHandler
    public void on(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getClickedBlock();

        if (block != null && block.getType() == Material.SKULL && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final BlockState blockState = block.getState();
            final Skull skull = (Skull) blockState;

            if (skull.getSkullType().equals(SkullType.PLAYER) && skull.hasOwner() && messages.containsKey(skull.getOwner().toLowerCase())) {

                player.sendMessage(C.C);
                player.sendMessage(messages.get(skull.getOwner().toLowerCase()));
                player.sendMessage(C.C);

            }
        }

    }


}
