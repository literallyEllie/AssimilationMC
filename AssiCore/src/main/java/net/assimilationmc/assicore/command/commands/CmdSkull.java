package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdSkull extends AssiCommand {

    private ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM);

    public CmdSkull(AssiPlugin plugin) {
        super(plugin, "skull", "Get a skull. Also supports decoded texture hashes", Rank.ADMIN, Lists.newArrayList("head", "s"), "<name/hash>");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        if (args[0].length() > 16) {
            ItemStack itemStack = ItemBuilder.getSkull(args[0]);
            ((Player) sender).getInventory().addItem(itemStack);
            sender.sendMessage(prefix(usedLabel) + "You have received the skull mapped to the textures you specified.");
            return;
        }

        itemBuilder.asPlayerHead(args[0]);
        itemBuilder.setDisplay(args[0] + "s head");

        ((Player) sender).getInventory().addItem(itemBuilder.build());
        sender.sendMessage(prefix(usedLabel) + "You have received " + C.V + args[0] + C.C + "'s head.");
    }

}
