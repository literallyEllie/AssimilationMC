package com.assimilation.ellie.assicore.manager;

import com.assimilation.ellie.assicore.api.AssiCore;
import com.assimilation.ellie.assicore.command.*;
import com.assimilation.ellie.assicore.command.friend.CmdFriend;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CommandManager implements IManager {

    private CommandMap commandMap;

    private HashMap<String, AssiCommand> commands;

    @Override
    public String getModuleID() {
        return "commands";
    }

    @Override
    public boolean load() {
        this.getCommandMap();

        this.commands = new HashMap<>();

        this.registerCommand(new CmdFriend());
        this.registerCommand(new CmdHelp());
        this.registerCommand(new CmdVanish());
        this.registerCommand(new CmdAssi());
        this.registerCommand(new CmdTP());
        this.registerCommand(new CmdMemory());
        this.registerCommand(new CmdBroadcast());

        this.registerCommand(new CmdGamemode());
        this.registerCommand(new CmdGamemode.CmdSurvival());
        this.registerCommand(new CmdGamemode.CmdCreative());
        this.registerCommand(new CmdGamemode.CmdAdventure());
        this.registerCommand(new CmdGamemode.CadSpectator());
        this.registerCommand(new CmdClearInventory());
        this.registerCommand(new CmdFly());
        this.registerCommand(new CmdFeed());
        this.registerCommand(new CmdHeal());

        this.registerCommand(new CmdSpawn());
        this.registerCommand(new CmdSpawn.CmdSetSpawn());

        return true;
    }

    @Override
    public boolean unload() {
        this.commands.values().forEach(commandBase -> unregisterCommand(commandBase));
        this.commands.clear();
        this.commands = null;
        this.commandMap = null;
        return true;
    }

    public AssiCommand getCommand(String id){
        return commands.get(id.toLowerCase());
    }

    public void registerCommand(AssiCommand assiCommand) {
        commandMap.register("", assiCommand);
        if (this.getCommand(assiCommand.getLabel()) == null) {
            this.commands.put(assiCommand.getLabel().toLowerCase(), assiCommand);
        }
    }

    public void unregisterCommand(AssiCommand commandBase){
        try{
            final Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> commandMaps = (Map<String, Command>) field.get(commandMap);
            commandMaps.remove(commandBase.getLabel());
            commandBase.getAliases().forEach(s -> commandMaps.remove(s));
            field.set(commandMap, commandMaps);
        }catch(Exception e){
            AssiCore.getCore().logE("Failed to unregister command "+commandBase.getName());
        }

    }

    private CommandMap getCommandMap(){
        try{
            final Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        }catch(Exception e){
            AssiCore.getCore().logE("Failed to get command map! Quick sub-command warps will not be registered!");
            e.printStackTrace();
        }
        return null;
    }

}
