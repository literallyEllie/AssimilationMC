package net.assimilationmc.ellie.assicore.command.friend;

import net.assimilationmc.ellie.assicore.command.AssiCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdFriend extends AssiCommand {

    public CmdFriend(){
        super("friend", "/friend", "Friend players on the network", Collections.singletonList("fr"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

    }
}
