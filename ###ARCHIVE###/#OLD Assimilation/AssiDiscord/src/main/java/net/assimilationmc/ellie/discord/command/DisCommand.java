package net.assimilationmc.ellie.discord.command;

import net.assimilationmc.ellie.discord.AssiDiscord;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.ArrayList;

/**
 * Created by Ellie on 14/04/2017 for Discord.
 * Affiliated with www.minevelop.com
 */
public abstract class DisCommand {

    private final String name, description;
    private final Permission permission;
    private final String syntax;
    private final boolean enabled;
    private final ArrayList<String> aliases;

    protected DisCommand(String name, String description, Permission permission, String syntax) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.syntax = syntax;
        this.enabled = true;
        this.aliases = new ArrayList<>();
    }

    protected AssiDiscord get(){
        return AssiDiscord.getAssiDiscord();
    }

    protected abstract void onCommand(GuildMessageReceivedEvent e, String[] args);

    public void execute(GuildMessageReceivedEvent e, String[] args){
        if(enabled){
            if(PermissionUtil.checkPermission(e.getChannel(), e.getMember(), permission)){
                try {
                    onCommand(e, args);
                }catch(Exception ex){
                    ex.printStackTrace();
                    e.getChannel().sendMessage("There was an error whilst performing the command: "+ex.getCause()+": "+ex.getMessage()+"/"+ex.getLocalizedMessage());
                }
            }else{
                e.getChannel().sendMessage("You do not have the permission "+permission.toString()+" to execute this command.").queue();
            }
        }else{
            e.getChannel().sendMessage("This command has been disabled by administrators").queue();
        }
    }

    public String correctUsage(){
        return "Correct usage: "+ get().getJsonBotConfig().getBotPrefix()+syntax;
    }

    public String getName() {
        return name;
    }

    public Permission getPermission() {
        return permission;
    }

    public ArrayList<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }
}
