package net.assimilationmc.assicore.lobby.donor;

import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class UIChatColor extends UI {

    private Map<UUID, ChatColor> colorMap;

    public UIChatColor(AssiPlugin plugin) {
        super(plugin, ChatColor.GREEN + "CHOOSE YOUR CHAT COLOR", 18);

        this.colorMap = Maps.newHashMap();

        for (ItemBuilder.StackColor stackColor : ItemBuilder.StackColor.values()) {
            if (stackColor.getChatColor() == null) continue;

            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer clicker) {
                    return new ItemBuilder(Material.WOOL).setColor(stackColor)
                            .setDisplay(stackColor.getChatColor() + StringUtils.capitalize(stackColor.name()
                                    .replace("_", " ").toLowerCase()))
                            .setLore(C.C, colorMap.containsKey(clicker.getUuid()) &&
                                    colorMap.get(clicker.getUuid()) == stackColor.getChatColor() ? C.II + "Click to reset your color." : C.C + "Click to switch to this color.")
                            .build();
                }

                @Override
                public void onAction(AssiPlayer clicker, ClickType clickType) {
                    if (setChatColor(clicker, stackColor.getChatColor())) {
                        closeInventory(clicker.getBase());
                    } else open(clicker.getBase());
                }
            });

        }

    }

    public boolean setChatColor(AssiPlayer player, ChatColor newColor) {
        final ChatColor oldColor = colorMap.get(player.getUuid());

        if (oldColor == newColor) {
            colorMap.remove(player.getUuid());
            player.setOverrideChatColor(null);
            player.sendMessage(ChatColor.YELLOW + "Your chat color has been reset.");
            return true;
        }

        player.setOverrideChatColor(newColor);
        player.sendMessage(ChatColor.YELLOW + "Your chat color has been updated to " + newColor +
                StringUtils.capitalize(newColor.name().toLowerCase().replace("_", " ")));
        colorMap.put(player.getUuid(), newColor);
        return false;
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        if (colorMap.containsKey(e.getPlayer().getUniqueId())) {
            setChatColor(getPlugin().getPlayerManager().getPlayer(e.getPlayer()), colorMap.get(e.getPlayer().getUniqueId()));
        }
    }

    public Map<UUID, ChatColor> getColorMap() {
        return colorMap;
    }

}
