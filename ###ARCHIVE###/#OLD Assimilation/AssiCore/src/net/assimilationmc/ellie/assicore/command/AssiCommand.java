package net.assimilationmc.ellie.assicore.command;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.event.AssiCommandExecutedEvent;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assicore.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public abstract class AssiCommand extends Command implements CommandExecutor {

    private final String label;
    private final String basePerm;
    private String usage;
    private String description;
    private CommandExecutor commandExecutor;
    private boolean playerOnly;

    public AssiCommand(String label, String basePerm, String usage, String description, List<String> aliases){
        super(label);
        this.commandExecutor = this;
        this.label = label;
        this.basePerm = basePerm;
        this.usage = usage;
        this.description = description;
        setAliases(aliases);
    }

    public AssiCommand(String label, String usage, String description, List<String> aliases){
        this(label, null, usage, description, aliases);
    }

    public AssiCommand(String label, String basePerm, String usage, String description){
        this(label, basePerm, usage, description, new ArrayList<>());
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getBasePerm() {
        return basePerm;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public List<String> getAliases() {
        return super.getAliases();
    }

    public AssiCore getCore(){
        return AssiCore.getCore();
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public void setPlayerOnly(boolean playerOnly) {
        this.playerOnly = playerOnly;
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        if(commandExecutor != null){
            return commandExecutor.onCommand(sender, this, command, args);
        }else{
            sendPMessage(sender, "&cError whilst dispatching command: command executor is null");
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(playerOnly && sender instanceof ConsoleCommandSender) {
            sendPMessage(sender, MessageLib.NO_CONSOLE);
            return true;
        }
        if(basePerm != null && !basePerm.isEmpty()){
            if(!hasPermission(sender, basePerm)){
                sendMessage(sender, MessageLib.NO_PERMISSION);
                return true;
            }
        }

        try {
            AssiCommandExecutedEvent event = new AssiCommandExecutedEvent(sender, this, args);
            Bukkit.getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                this.onCommand(sender, args);
            }else sendPMessage(sender, event.getCancelledReason() == null ? ColorChart.WARN + "You cannot execute this command right now." : event.getCancelledReason());

        }catch(Throwable e){
            Util.mINFO_noP(sender, String.format(MessageLib.COMMAND_FAIL, e));
            Util.handleException("command "+label, e);
            return false;
        }
        return true;
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    public boolean isPlayer(CommandSender sender){
        return sender instanceof Player;
    }

    public boolean hasPermission(CommandSender sender, String permission){
        return sender.hasPermission(permission);
    }

    public void sendMessage(CommandSender sender, String message){
        sender.sendMessage(Util.color(ColorChart.R + message));
    }

    public void sendPMessage(CommandSender sender, String message) {
        sender.sendMessage(Util.color(String.format(ColorChart.PREFIX, "9") + StringUtils.capitalize(label) + " &8| " + ColorChart.R + message));
    }

    public String correctUsage(){
        return String.format(MessageLib.CORRECT_USAGE, this.usage, this.description);
    }

}
