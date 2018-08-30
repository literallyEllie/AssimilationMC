package com.assimilation.ellie.assibperms.command;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibungee.util.MessageLib;
import com.assimilation.ellie.assibungee.util.Util;
import net.md_5.bungee.api.CommandSender;

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
        return AssiBPerms.getAssiBPerms().getCommandManager();
    }

    public void execute(CommandSender sender, String[] args){
        if(!basePerm.isEmpty()){
            if(!sender.hasPermission(this.basePerm)){
                Util.mWARN(sender, MessageLib.NO_PERMISSION);
                return;
            }
        }
        this.onCommand(sender, args);

    }

    public abstract void onCommand(CommandSender sender, String[] args);

}
