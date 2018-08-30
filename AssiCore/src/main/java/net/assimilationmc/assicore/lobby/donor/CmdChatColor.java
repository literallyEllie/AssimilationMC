package net.assimilationmc.assicore.lobby.donor;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdChatColor extends AssiCommand {

    private UIChatColor uiChatColor;

    public CmdChatColor(AssiPlugin plugin) {
        super(plugin, "chatColor", "Set your chat color", Rank.DEMONIC, Lists.newArrayList("cc"));

        this.uiChatColor = new UIChatColor(plugin);
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        uiChatColor.open((Player) sender);
    }

}
