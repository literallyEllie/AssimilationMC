package net.assimilationmc.ellie.assicore.command.permission;

import net.assimilationmc.ellie.assicore.command.permission.sub.CmdRefresh;
import net.assimilationmc.ellie.assicore.command.permission.sub.CmdSubHelp;
import net.assimilationmc.ellie.assicore.command.permission.sub.group.*;
import net.assimilationmc.ellie.assicore.command.permission.sub.user.CmdSetGroup;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;

import java.util.LinkedHashMap;

/**
 * Created by Ellie on 13/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PCommandManager {

    private LinkedHashMap<String, SubCommand> commands;

    public PCommandManager(){
        this.commands = new LinkedHashMap<>();

        ModuleManager.getModuleManager().getCommandManager().registerCommand(new CmdPermission(this));

        this.commands.put("help", new CmdSubHelp());
        this.commands.put("list", new CmdGroupList());
        this.commands.put("creategroup", new CmdCreateGroup());
        this.commands.put("delgroup", new CmdDelGroup());
        this.commands.put("addp", new CmdAddP());
        this.commands.put("delp", new CmdDelP());
        this.commands.put("info", new CmdInfo());
        this.commands.put("options", new CmdOptions());
        this.commands.put("setgroup", new CmdSetGroup());

        this.commands.put("refresh", new CmdRefresh());


    }

    public LinkedHashMap<String, SubCommand> getCommands() {
        return commands;
    }

    public SubCommand getCommand(String command){
        return this.commands.get(command);
    }

    public boolean isCommand(String command){
        return this.commands.containsKey(command);
    }

}
