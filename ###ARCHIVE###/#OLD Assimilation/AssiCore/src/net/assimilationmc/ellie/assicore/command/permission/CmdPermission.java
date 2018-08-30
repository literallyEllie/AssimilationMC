package net.assimilationmc.ellie.assicore.command.permission;

import net.assimilationmc.ellie.assicore.command.AssiCommand;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 21/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdPermission extends AssiCommand {

    private PCommandManager commandManager;

    public CmdPermission(PCommandManager commandManager){
        super("permission", PermissionLib.CMD.PERM.PERMISSION, "permission help", "Permission management");
        this.commandManager = commandManager;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length == 0){
            Util.mINFO_noP(sender, String.format(MessageLib.CORRECT_USAGE, "permission help", "Permission Management"));
        }

        else{
            if(!commandManager.isCommand(args[0])){
                Util.mWARN(sender, "Invalid sub-command");
                return;
            }
            commandManager.getCommand(args[0]).execute(sender, args);
        }
    }

}
