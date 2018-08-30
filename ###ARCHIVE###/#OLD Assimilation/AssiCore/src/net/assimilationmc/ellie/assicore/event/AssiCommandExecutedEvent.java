package net.assimilationmc.ellie.assicore.event;

import net.assimilationmc.ellie.assicore.command.AssiCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Ellie on 8.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class AssiCommandExecutedEvent extends Event implements Cancellable {

    private static final HandlerList handlerList  = new HandlerList();

    private boolean isCancelled;
    private String cancelledReason;

    private final CommandSender sender;
    private final AssiCommand command;
    private final String[] args;

    public AssiCommandExecutedEvent(CommandSender sender, AssiCommand assiCommand, String[] args){
        this.sender = sender;
        this.command = assiCommand;
        this.args = args;
    }

    public AssiCommandExecutedEvent(CommandSender sender, AssiCommand assiCommand){
        this(sender, assiCommand, null);
    }

    public CommandSender getSender() {
        return sender;
    }

    public AssiCommand getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public String getCancelledReason() {
        return cancelledReason;
    }

    public void setCancelledReason(String cancelledReason) {
        this.cancelledReason = cancelledReason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
