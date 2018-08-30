package net.assimilationmc.uhclobbyadaptor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilBungee;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilServer;
import net.assimilationmc.uhclobbyadaptor.items.create.GameCreationConfiguration;
import net.assimilationmc.uhclobbyadaptor.lib.GC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameCreationFactory implements Listener {

    private final AssiPlugin plugin;
    private Set<UUID> pendingCreation;
    private Map<String, UUID> waitingConnections;
    private Map<Integer, String> errorCache;

    public GameCreationFactory(AssiPlugin plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
        this.pendingCreation = Sets.newHashSet();
        this.waitingConnections = Maps.newHashMap();
        this.errorCache = Maps.newLinkedHashMap();
    }

    public void create(GameCreationConfiguration creationConfiguration) {
        final Player creator = creationConfiguration.getCreator();

        creator.sendMessage(C.II + "Game Creator" + C.SS + C.C + "Creating your game now...");
        pendingCreation.add(creator.getUniqueId());

        plugin.getWebAPIManager().sendRequest(creationConfiguration.prepare(plugin.getWebAPIManager()), requestResponse -> {

            if (requestResponse.getErrorId() != null) {
                reset(creator);

                int refId = cacheError(requestResponse.getDetailedMessage());

                switch (requestResponse.getErrorId()) {
                    case TOO_MANY_SERVERS:
                        creator.sendMessage(C.II + "Error! " + C.V + "There are currently the max amount of servers running, try again later or join another game.");
                        break;
                    case SERVER_NOT_FOUND:
                        creator.sendMessage(C.II + "Error! " + C.V + "A server with that ID was not found, to get this resolved message a member of staff.");
                        break;
                    case INVALID_GAME_TYPE:
                        creator.sendMessage(C.II + "Error! " + C.V + "The creator doesn't know about that game type yet, to get this resolved message a member of staff.");
                        break;
                    case MAP_NOT_FOUND:
                        creator.sendMessage(C.II + "Error! " + C.V + "The creator doesn't know about that map yet, to get this resolved message a member of staff.");
                        break;
                    case BAD_AUTH:
                        creator.sendMessage(C.II + "Error! " + C.V + "The creator said we couldn't do that (bad auth), to get this resolved message a member of staff.");
                        break;
                    case BAD_PAYLOAD:
                    case DATA_PARSE_FAIL:
                    case INVALID_REQUEST:
                    case BAD_END_POINT:
                        creator.sendMessage(C.II + "Error! " + C.V + "Looks like we accidentally slammed our face into the keyboard when making the request, " +
                                "to get this resolved message a member of staff.");
                        break;
                    case ACTION_FAILURE:
                        creator.sendMessage(C.II + "Error! " + C.V + "The creator had some indigestion issues when processing our request, to get this resolved message a member" +
                                " of staff ");
                        break;
                    case UNKNOWN:
                        creator.sendMessage(C.II + "Error! " + C.V + "The creator said something we didn't understand, please contact a member of staff.");
                }

                creator.sendMessage(C.II + ChatColor.BOLD + "Please mention this Reference code: " + C.V + refId);
                return;
            }

            try {
                String serverId = requestResponse.getDetailedMessage();
                waitingConnections.put(serverId, creator.getUniqueId());
                Party party = plugin.getPartyManager().getPartyOf(creator, false);
                if (party != null) {
                    plugin.getPartyManager().setTarget(party, serverId);
                }

                creator.sendMessage(C.II + "Your server (" + C.V + serverId + C.II + ") is now starting, please wait, you will be connected automatically.");

            } catch (ArrayIndexOutOfBoundsException e) {
                creator.sendMessage(C.II + "Couldn't automatically send you to the Lobby but it has been created (parsing error), please go into the Play UHC menu " +
                        "and find the server by the name indicated in (wait a couple seconds) " + C.V + requestResponse.getDetailedMessage());

            }
        });

    }

    public void onServerStart(String serverId) {
        if (waitingConnections.containsKey(serverId)) {
            UUID uuid = waitingConnections.get(serverId);
            Player player = UtilPlayer.get(uuid);
            if (player == null) {
                waitingConnections.remove(serverId);
                return;
            }
            waitingConnections.remove(serverId);
            UtilBungee.sendPlayer(plugin, player, serverId);
            reset(player);
            pendingCreation.remove(player.getUniqueId());
        }

    }

    public boolean canMake(Player player) {
        Party party = plugin.getPartyManager().getPartyOf(player, false);
        return (party == null || party.getLeader().equals(player.getUniqueId())) && !pendingCreation.contains(player.getUniqueId());

    }

    public void reset(Player player) {
        pendingCreation.remove(player.getUniqueId());
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        reset(e.getPlayer());
    }

    private int cacheError(String message) {
        int id = errorCache.size() + 1;
        errorCache.put(id, message);
        return id;
    }

    public Map<Integer, String> getErrorCache() {
        return errorCache;
    }

}
