package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class CmdVerify extends AssiCommand {

    private Map<UUID, String> codes;
    private Map<UUID, Long> idIndex;

    public CmdVerify(AssiPlugin plugin) {
        super(plugin, "verify", "Verify your Discord account to your MC account with a code", Lists.newArrayList(), "<code>");
        requirePlayer();
        codes = Maps.newHashMap();
        idIndex = Maps.newHashMap();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        AssiPlayer player = asPlayer(sender);

        if (player.getDiscordAccount() != 0) {
            player.sendMessage(C.C + "This account is already verified.");
            return;
        }

        if (!codes.containsKey(player.getUuid())) {
            player.sendMessage(C.C + "This account is not being verified. If you started the process on another server, please start again.");
            return;
        }

        final String code = args[0];

        if (codes.get(player.getUuid()).equalsIgnoreCase(code)) {
            player.sendMessage(ChatColor.GREEN + "Account verified!");
            player.setDiscordAccount(idIndex.get(player.getUuid()));
            codes.remove(player.getUuid());
            idIndex.remove(player.getUuid());
        } else {
            player.sendMessage(C.II + "Bad code.");
        }

    }

    public Map<UUID, String> getCodes() {
        return codes;
    }

    public Map<UUID, Long> getIdIndex() {
        return idIndex;
    }

}
