package net.assimilationmc.ellie.assicore.command.permission.sub.group;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.command.permission.SubCommand;
import net.assimilationmc.ellie.assicore.event.ScoreboardUpdateEvent;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.permission.AssiPermGroup;
import net.assimilationmc.ellie.assicore.permission.GroupOption;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.PermissionLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdOptions extends SubCommand {

    public CmdOptions(){
        super("options", PermissionLib.CMD.PERM.GROUP_OPTIONS, "options <group> <option> <value>", "Set an option for a group");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if(args.length != 4){
            sendPMessage(sender, correctUsage());
            return;
        }

        AssiPermGroup group = getPermissionManager().getGroup(args[1]);

        if(group == null){
            sendPMessage(sender, "Group doesn't exist (at group) "+ ColorChart.VARIABLE + args[1]+ColorChart.R +".");
            return;
        }

        GroupOption option;
        try {
            option = GroupOption.valueOf(args[2].toUpperCase());
        }catch (IllegalArgumentException e){
            sendPMessage(sender, "Option doesn't exist (at option) "+ ColorChart.VARIABLE + args[2] + ColorChart.R + ".");
            sendMessage(sender, "Options: "+ ColorChart.VARIABLE + Joiner.on(ColorChart.R+", "+ColorChart.VARIABLE).join(GroupOption.values()));
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
                sendMessage(sender, "Value is invalid for type (at value) "+ ColorChart.VARIABLE + option.getClazz().getSimpleName() + ColorChart.R+".");
                return;
            }
        }

        if(option.getClazz() == String.class){

            value = args[3];

            if(option.equals(GroupOption.PREFIX)){
                group.setPrefix(value);
            }else if(option.equals(GroupOption.SUFFIX)){
                group.setSuffix(value);
            }

            ModuleManager.getModuleManager().getPlayerManager().getLoadedPlayers().values().forEach(assiPlayer -> {
                if(assiPlayer.getPermissionsRank().equals(group.getName())){
                    Bukkit.getPluginManager().callEvent(new ScoreboardUpdateEvent(assiPlayer.getName(), ScoreboardUpdateEvent.UpdateElement.RANK));
                }
            });

        }

        sendPMessage(sender, "Option "+ColorChart.VARIABLE + option.toString()+ ColorChart.R+
                " set to '"+ ColorChart.VARIABLE +value.replace("&", "@")+ ColorChart.R + "' for group "+ ColorChart.VARIABLE + group.getName()+ ColorChart.R + ". Could take up to 10 minutes to take effect.");
        getPermissionManager().setUpdate(true);

    }
}
