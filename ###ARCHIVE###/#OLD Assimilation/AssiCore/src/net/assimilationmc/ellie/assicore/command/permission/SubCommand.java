package net.assimilationmc.ellie.assicore.command.permission;


import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.manager.PermissionManager;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public abstract class SubCommand {

    private String label;
    private String basePerm;
    private String usage;
    private String description;

    public SubCommand(String label, String basePerm, String usage, String description){
        this.label = label;
        this.basePerm = basePerm;
        this.usage = "permission "+usage;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getBasePerm() {
        return basePerm;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String correctUsage(){
        return String.format(MessageLib.CORRECT_USAGE, this.usage, this.description);
    }

    protected boolean hasPermission(CommandSender commandSender){
        if(basePerm.isEmpty()) return true;
        return commandSender.hasPermission(basePerm);
    }

    public PCommandManager getManager(){
        return ModuleManager.getModuleManager().getPermissionManager().getPCommandManager();
    }

    public PermissionManager getPermissionManager(){
        return ModuleManager.getModuleManager().getPermissionManager();
    }

    public void execute(CommandSender sender, String[] args){
        if(!basePerm.isEmpty()){
            if(!sender.hasPermission(this.basePerm)){
                Util.mINFO_noP(sender, MessageLib.NO_PERMISSION);
                return;
            }
        }
        this.onCommand(sender, args);
    }

    public void sendPMessage(CommandSender sender, String message) {
        sender.sendMessage(Util.color(String.format(ColorChart.PREFIX, "9") + StringUtils.capitalize(label) + " &8| " + ColorChart.R + message));
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Util.color(ColorChart.R + message));
    }

    public abstract void onCommand(CommandSender sender, String[] args);

}
