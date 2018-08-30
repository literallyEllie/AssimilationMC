package net.assimilationmc.ellie.assiuhc.command;

import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assiuhc.command.admin.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Ellie on 20/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CommandManager {

    private LinkedHashMap<String, SubCommand> commands;
    private HashMap<String, String> aliases;

    public CommandManager(){
        this.commands = new LinkedHashMap<>();
        this.aliases = new HashMap<>();

        ModuleManager.getModuleManager().getCommandManager().registerCommand(new CmdUHC());

        registerCommand(new CmdJoin());
        registerCommand(new CmdLeave());
        registerCommand(new CmdList());
        registerCommand(new CmdRejoin());
        registerCommand(new CmdForceStart());
        registerCommand(new CmdCreate());
        registerCommand(new CmdSetOption());
        registerCommand(new CmdSetLobby());
        registerCommand(new CmdSave());
        registerCommand(new CmdInfo());
        registerCommand(new CmdMaps());
        registerCommand(new CmdSetBuilders());
        registerCommand(new CmdToggleMap());
        registerCommand(new CmdSetMaterial());
        registerCommand(new CmdSpawns());
    }

    private void registerCommand(SubCommand subCommand) {
        commands.put(subCommand.getLabel().toLowerCase(), subCommand);
        for (String s : subCommand.getAliases()) {
            aliases.put(s, subCommand.getLabel().toLowerCase());
        }
    }

    public void finish(){
        commands.clear();
        aliases.clear();
    }

    public SubCommand getCommand(String arg){
        if(commands.get(arg.toLowerCase()) != null){
            return commands.get(arg.toLowerCase());
        }

        return commands.get(aliases.get(arg.toLowerCase()));
    }

    public LinkedHashMap<String, SubCommand> getCommands() {
        return commands;
    }

}
