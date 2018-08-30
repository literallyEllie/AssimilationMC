package com.assimilation.ellie.assicore.command.friend;

import com.assimilation.ellie.assicore.command.AssiCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdFriend extends AssiCommand {

    public CmdFriend(){
        super("friend", "/friend", "Friend players on the network", Arrays.asList("fr"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

    }
}
