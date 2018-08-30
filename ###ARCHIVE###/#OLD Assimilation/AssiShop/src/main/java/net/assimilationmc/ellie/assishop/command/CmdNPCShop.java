package net.assimilationmc.ellie.assishop.command;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assicore.command.AssiCommand;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.MessageLib;
import net.assimilationmc.ellie.assishop.AssiShop;
import net.assimilationmc.ellie.assishop.NPCShop;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ellie on 13.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class CmdNPCShop extends AssiCommand implements Listener {

    private final HashMap<UUID, NPCShop> tempShops = new HashMap<>();

    // npcshop <create | delete <id> | edit <id> <movehere | display | action>>
    public CmdNPCShop() {
        super("NPCShop", "assimilation.cmd.npcshop", "npcshop <create | list | delete <id> | edit <id> <movehere | display | action>>", "Shop manager for the NPCs");
        setPlayerOnly(true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length < 1) {
            sendPMessage(player, correctUsage());
            return;
        }

        final String method = args[0].toLowerCase();

        switch (method) {
            case "create":
                NPCShop person = get(player.getUniqueId());
                if (person != null) {
                    sendPMessage(sender, "You are already editing, type '" + ColorChart.VARIABLE + ColorChart.R + "' to change your mind.");
                    return;
                }
                person = new NPCShop();

                tempShops.put(player.getUniqueId(), person);
                person.setLocation(new SerializableLocation(player.getLocation()).toString());
                sendPMessage(player, "NPC location set to your location, type 'display=...' or 'action=...' to continue. And 'done' when you're finished.");
                return;
            case "list":
                List<Integer> ids = new ArrayList<>();
                AssiShop.getAssiShop().getSqlStorage().getShops().values().forEach(npcShop -> ids.add(npcShop.getId()));
                sendPMessage(sender, "Shops: "+ ColorChart.VARIABLE + Joiner.on(ColorChart.R + ", "+ColorChart.VARIABLE).join(ids));
                return;
            case "delete":
                if (args.length != 2) {
                    break;
                }
                final int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sendMessage(player, MessageLib.INVALID_NUMBER);
                    return;
                }
                AssiShop.getAssiShop().getSqlStorage().delShop(id);
                sendPMessage(player, "Shop deleted.");
                return;
            case "edit":
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sendMessage(player, MessageLib.INVALID_NUMBER);
                    return;
                }
                NPCShop shop = AssiShop.getAssiShop().getSqlStorage().getShop(id);
                if (shop == null) {
                    sendPMessage(player, "Shop doesn't exist.");
                    return;
                }
                if (shop.isEdit()) {
                    sendPMessage(player, "This shop is already being edited.");
                    return;
                }

                switch (args[2].toLowerCase()) {
                    case "movehere":
                        shop.setLocation(new SerializableLocation(player.getLocation()).toString());
                        AssiShop.getAssiShop().getSqlStorage().update(shop);
                        sendPMessage(player, "Location moved to your current position.");
                        return;
                    case "display":
                        break;
                    case "action":
                        break;
                    default:
                        sendPMessage(player, "Invalid method.");
                        return;
                }
                shop.setEdit(true, method);
                tempShops.put(player.getUniqueId(), shop);
                sendPMessage(player, "Type what you want the new value to be, then type 'done' when you're finished.");
                return;
        }
        sendMessage(sender, correctUsage());

    }

    public NPCShop get(UUID uuid) {
        return tempShops.get(uuid);
    }

    public HashMap<UUID, NPCShop> getTempShops() {
        return tempShops;
    }

    public boolean is(UUID uuid) {
        return tempShops.containsKey(uuid);
    }

    public void done(UUID uuid) {
        NPCShop shop = get(uuid);
        if (shop != null) {
            if (shop.isEdit()) {
                shop.setEdit(false, null);
                AssiShop.getAssiShop().getSqlStorage().update(shop);
                sendPMessage(Bukkit.getPlayer(uuid), "Updated shop.");
            } else AssiShop.getAssiShop().getSqlStorage().createShop(get(uuid));
        }

        tempShops.remove(uuid);
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String rawMessage = e.getMessage();

        if (!is(player.getUniqueId())) return;

        String action = rawMessage.split("=")[0];

        if (action.equalsIgnoreCase("done")) {
            e.setCancelled(true);
            done(player.getUniqueId());
            sendPMessage(player, "Shop created.");
            return;
        }

        if (!(action.equalsIgnoreCase("display") || action.equalsIgnoreCase("action"))) {
            sendPMessage(player, "Invalid method.");
            e.setCancelled(true);
            return;
        }

        String message = rawMessage.substring((action + "=").length());

        if (action.equalsIgnoreCase("display")) {
            get(player.getUniqueId()).setDisplay(message);
            sendMessage(player, "Set display to "+message);
            e.setCancelled(true);
        } else if (action.equalsIgnoreCase("action")) {
            get(player.getUniqueId()).setAction(message);
            sendMessage(player, "Set action to "+message);
            e.setCancelled(true);
        }

    }


}
