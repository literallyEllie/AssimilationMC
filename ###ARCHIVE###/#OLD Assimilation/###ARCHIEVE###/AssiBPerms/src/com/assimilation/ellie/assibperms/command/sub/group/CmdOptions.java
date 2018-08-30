package com.assimilation.ellie.assibperms.command.sub.group;

import com.assimilation.ellie.assibperms.AssiBPerms;
import com.assimilation.ellie.assibperms.command.SubCommand;
import com.assimilation.ellie.assibperms.permission.AssiPermGroup;
import com.assimilation.ellie.assibperms.permission.GroupOption;
import com.assimilation.ellie.assibperms.util.CmdPermLib;
import com.assimilation.ellie.assibungee.util.Util;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdOptions extends SubCommand {

    public CmdOptions(){
        super("options", CmdPermLib.GROUP_OPTIONS, "options <group> <option> <value>", "Set an option for a group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 4){
            Util.mINFO_noP(sender, correctUsage());
            return;
        }

        AssiPermGroup group = AssiBPerms.getAssiBPerms().getGroupManager().getGroup(args[1]);

        if(group == null){
            Util.mINFO(sender, "Group doesn't exist (at group) "+args[1]);
            return;
        }

        GroupOption option;
        try {
            option = GroupOption.valueOf(args[2].toUpperCase());
        }catch (IllegalArgumentException e){
            Util.mINFO(sender, "Option doesn't exist (at option) "+args[2]);
            Util.mINFO_noP(sender, "Options: &9"+ Joiner.on(", ").join(GroupOption.values()));
            return;
        }

        String value = "null";

        if(option.getClazz() == Boolean.class){

            value = args[3];
            if(value.equalsIgnoreCase("true")){
                group.getOptions().add(option);
            }
            else
            if(value.equalsIgnoreCase("false")){
                group.getOptions().remove(option);
            }else{
                Util.mINFO(sender, "Value is invalid for type (at value) "+option.getClazz().getSimpleName());
                return;
            }
        }

        if(option.getClazz() == String.class){

            value = args[3].replace("%space%", " ");

            if(option.equals(GroupOption.PREFIX)){
                group.setPrefix(value);
            }else if(option.equals(GroupOption.SUFFIX)){
                group.setSuffix(value);
            }

        }

        Util.mINFO(sender, "Option &9"+option.toString()+" &fset to '&9"+ value.replace("&", "@")+"&f' for group &9"+group.getName()+" &fCould take up to 10 minutes to take effect");
        AssiBPerms.getAssiBPerms().getGroupManager().setUpdate(true);

    }
}
