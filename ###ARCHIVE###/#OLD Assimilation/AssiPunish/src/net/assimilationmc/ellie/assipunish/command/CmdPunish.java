package net.assimilationmc.ellie.assipunish.command;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.command.AssiCommand;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assipunish.AssiPunish;
import net.assimilationmc.ellie.assipunish.punish.Punishment;
import net.assimilationmc.ellie.assipunish.punish.PunishmentOffence;
import net.assimilationmc.ellie.assipunish.ui.PunishMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdPunish extends AssiCommand {

    public CmdPunish(){
        super("punish", "assipunish.punish", "punish <user> [params...]", "Punish a player, reasons are only for custom punishments");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length < 1){
            sendMessage(sender, correctUsage());
            return;
        }

        if(sender instanceof ConsoleCommandSender){

            if(args.length < 2) {
                Util.mINFO(sender, Util.prefix() + "punish <user> <punishment> [reason]");
                return;
            }

            Punishment punishment;
            try{
                punishment = Punishment.valueOf(args[1]);
            }catch(IllegalArgumentException e){
                Util.mINFO(sender, "&cInvalid punishment. Valid options");
                Util.mINFO_noP(sender, Joiner.on(", ").join(Punishment.values()));
                return;
            }

            PunishmentOffence punishmentOffence = PunishmentOffence.getPunishInfo(punishment, -1);

            if(punishmentOffence == null){
                sendPMessage(sender, "Punishment offence not found.");
                return;
            }
            return;
        }

        if(isIP(args[0]) && !sender.hasPermission(AssiPunish.getAssiPunish().ipFilterPermission)){
            sendPMessage(sender, "You cannot view IPs due to your rank.");
            return;
        }

        Player player = (Player) sender;
        if(args.length == 1) new PunishMenu(AssiPunish.getAssiPunish(), player, args[0], null); else new PunishMenu(
                AssiPunish.getAssiPunish(), player, args[0], Util.getFinalArg(args, 1));
    }

    private boolean isIP(String a){
        return a.matches(Util.ipPattern.pattern());
    }

}
