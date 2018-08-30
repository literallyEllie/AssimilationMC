package net.assimilationmc.assicore.citizens;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilString;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdCitizenHandle extends AssiCommand {

    private NPCManager npcManager;

    public CmdCitizenHandle(NPCManager npcManager) {
        super(npcManager.getPlugin(), "citizenHandle", "Handle custom citizen people, make sure the Citizen already exists", Rank.ADMIN,
                Lists.newArrayList("citizensHook"), "<id>", "<info | set [action] [params...] | delete>");
        this.npcManager = npcManager;
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(C.II + "Invalid ID: " + args[0]);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "info":
                CitizenHookData infoData = npcManager.getCitizenData(id);
                if (infoData == null) {
                    sender.sendMessage(C.II + "No Citizen request Hook was found with that ID. Use " + C.V + "/" + usedLabel + " " + id + " set [action] [params...]");
                    return;
                }

                sender.sendMessage(C.C + "Data about Citizen hook " + C.V + id + C.C + ":");
                sender.sendMessage(C.C + "Message on interaction: " + C.V + infoData.getMessage().replace("§", "&"));
                sender.sendMessage(C.C + "Command on interaction: " + C.V + infoData.getCommandExec().replace("§", "&"));
                sender.sendMessage(C.C);
                return;
            case "delete":
                CitizenHookData delData = npcManager.getCitizenData(id);
                if (delData != null) {
                    npcManager.delete(delData.getId());
                    sender.sendMessage(C.II + "Deleted! (Hasn't deleted the actual citizen)");
                    return;
                }
                sender.sendMessage(C.II + "No Citizen request Hook was found with that ID.");
                return;
            case "set":
                if (args.length < getRequiredArgs() + 2) {
                    sender.sendMessage(prefix(usedLabel) + C.II + "Correct usage: " + C.V + "/" + usedLabel + " set <msg/command> <data...> " + C.C + "-"
                            + "Optional placeholders: " + C.V + "{player}");
                    return;
                }

                String action = args[2].toLowerCase();
                String data = UtilString.getFinalArg(args, 3);
                CitizenHookData citizenHookData;
                switch (action) {
                    case "msg":
                        citizenHookData = npcManager.isCitizenData(id) ? npcManager.getCitizenData(id) : new CitizenHookData(id);
                        citizenHookData.setMessage(ChatColor.translateAlternateColorCodes('&', data));
                        sender.sendMessage(C.C + "Set the message on interaction to be \"" + C.V + data + C.C + "\"");
                        npcManager.addCitizenHook(citizenHookData);
                        break;
                    case "command":
                        if (data.toLowerCase().contains("setrank") || data.toLowerCase().contains("gm") ||
                                data.toLowerCase().contains("gamemode")) {
                            sender.sendMessage(C.II + "Command request contains an illegal command to execute.");
                            return;
                        }

                        citizenHookData = npcManager.isCitizenData(id) ? npcManager.getCitizenData(id) : new CitizenHookData(id);
                        citizenHookData.setCommandExec(ChatColor.translateAlternateColorCodes('&', data));
                        sender.sendMessage(C.C + "Set the command execution on interaction to be \"" + C.V + data + C.C + "\"");
                        npcManager.addCitizenHook(citizenHookData);
                        break;
                    default:
                        sender.sendMessage(C.II + "Invalid action. Choose from either " + C.V + "msg" + C.C + " and " + C.V + "command" + C.C + ".");
                        break;
                }
                return;
            default:
                usage(sender, usedLabel);
        }

    }


}
