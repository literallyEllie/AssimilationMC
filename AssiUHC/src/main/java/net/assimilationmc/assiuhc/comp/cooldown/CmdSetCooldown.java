package net.assimilationmc.assiuhc.comp.cooldown;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assiuhc.game.UHCGame;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CmdSetCooldown extends AssiCommand {

    private UHCGame game;

    public CmdSetCooldown(UHCGame game) {
        super(game.getPlugin(), "setcooldown", "Set a player's comp cooldown.", Rank.ADMIN, Lists.newArrayList(),
                "<player> <cooldown (tab complete)>");
        this.game = game;
    }

    @Override
    public void onCommand(CommandSender commandSender, String s, String[] strings) {
//        UHCPlayer player = game.getPlayerManager().getPlayer()

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {


        return super.tabComplete(sender, alias, args);
    }

}
