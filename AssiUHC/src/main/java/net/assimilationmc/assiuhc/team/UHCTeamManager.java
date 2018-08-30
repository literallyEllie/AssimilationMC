package net.assimilationmc.assiuhc.team;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.assimilationmc.assicore.party.Party;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assiuhc.game.UHCGame;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.assiuhc.team.ui.TeamOptionsMenu;
import net.assimilationmc.gameapi.module.GameModule;
import net.assimilationmc.gameapi.module.ModuleActivePolicy;
import net.assimilationmc.gameapi.phase.GamePhase;
import net.assimilationmc.gameapi.phase.GamePhaseChangeEvent;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class UHCTeamManager extends GameModule {

    private final ItemBuilder.StackColor[] colors = {ItemBuilder.StackColor.ORANGE, ItemBuilder.StackColor.LIGHT_BLUE, ItemBuilder.StackColor.YELLOW,
            ItemBuilder.StackColor.LIME, ItemBuilder.StackColor.PINK, ItemBuilder.StackColor.CYAN, ItemBuilder.StackColor.PURPLE,
            ItemBuilder.StackColor.BLUE, ItemBuilder.StackColor.GREEN, ItemBuilder.StackColor.RED};
    private boolean enabled;
    private Map<UUID, GameTeam> teams; // team leader, their team
    private Map<GameTeam, TeamOptionsMenu> teamTeamOptionsMenuMap;
    private Map<String, Set<String>> invites; // team name, invites to team
    private Set<ItemBuilder.StackColor> takenColors;
    private int minTeams, overrideMaxTeamCount, maxTeamSize;

    private TeamCreateItem teamCreateItem;
    private PlayerInvitesItem playerInvitesItem;

    private List<String> teamNames = Lists.newArrayList("Rebels", "Cool",
            "Winnerz", "Eggplants", "Potatoes", "Pro", "Team 101", "->");

    public UHCTeamManager(UHCGame uhcGame) {
        super(uhcGame, "UHC-Team Manager", ModuleActivePolicy.PERMANENT);
    }

    @Override
    public void start() {
        enabled = ((UHCGame) getAssiGame()).getGameSubType().isTeamed();
        this.teams = Maps.newHashMap();
        this.teamTeamOptionsMenuMap = Maps.newHashMap();
        this.invites = Maps.newHashMap();
        this.takenColors = Sets.newHashSet();

        if (!enabled) return;
        this.overrideMaxTeamCount = -1;
        this.minTeams = 3;
        this.maxTeamSize = 10; // temp

        this.teamCreateItem = new TeamCreateItem(((UHCTeamedGame) getAssiGame()));
        this.playerInvitesItem = new PlayerInvitesItem(((UHCTeamedGame) getAssiGame()));

        getAssiGame().getPlugin().getJoinItemManager().addItem(teamCreateItem);
        getAssiGame().getPlugin().getJoinItemManager().addItem(playerInvitesItem);
    }

    @Override
    public void end() {
        teams.clear();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerQuitEvent e) {
        declineAllInvites(e.getPlayer());
        leaveTeam(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final GamePhaseChangeEvent e) {
        if (e.getTo() != GamePhase.WARMUP) return;
        getAssiGame().getPlugin().getJoinItemManager().removeItem(1);
        getAssiGame().getPlugin().getJoinItemManager().removeItem(2);

        Set<Player> teamLess = getAssiGame().getTeamManager().getTeamless();
        if (!teamLess.isEmpty()) {
            final Iterator<Player> iterator = teamLess.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                GameTeam gameTeam;

                if (teams.size() < getMinTeams()) {
                    final ItemBuilder.StackColor teamColor = getTeamColor();
                    gameTeam = new GameTeam(teamNames.get(new Random().nextInt(teamNames.size() - 1)), teamColor.getChatColor());
                    takenColors.add(teamColor);
                    teamNames.remove(gameTeam.getName());
                    teams.put(player.getUniqueId(), gameTeam);
                    getAssiGame().getTeamManager().addTeam(gameTeam);
                } else gameTeam = getAssiGame().getTeamManager().getMostEmptyTeam();

                gameTeam.message(ChatColor.BOLD + GC.V + player.getName() + GC.II + ChatColor.BOLD + " has joined the team.");
                gameTeam.add(player);
                player.sendMessage(GC.C + "You have joined the team " + gameTeam.getColor() + gameTeam.getName());
                iterator.remove();
            }
        }

        teamTeamOptionsMenuMap.values().forEach(UI::destroySelf);
        teamTeamOptionsMenuMap.clear();
        invites.clear();
        takenColors.clear();
    }

    public String createTeam(Player creator, String name) {
        if (!enabled) return GC.II + "There are no teams in this game.";

        if (this.teams.size() >= getMaxTeams()) {
            return C.C + "The max amount of teams has been reached.";
        }

        if (!getAssiGame().getTeamManager().addTeam(new GameTeam(name, getTeamColor().getChatColor())))
            return GC.II + "Team already name already taken.";

        GameTeam team = getAssiGame().getTeamManager().getTeam(name);
        takenColors.add(ItemBuilder.StackColor.fromChatColor(team.getColor()));
        team.add(creator);
        teams.put(creator.getUniqueId(), team);

        teamTeamOptionsMenuMap.put(team, new TeamOptionsMenu(((UHCTeamedGame) getAssiGame()), creator.getName(), team, maxTeamSize));
        declineAllInvites(creator);
        creator.getInventory().remove(playerInvitesItem.getItemStack());

        teamTeamOptionsMenuMap.get(team).open(creator);

        Party party = getAssiGame().getPlugin().getPartyManager().getPartyOf(creator, false);
        if (party != null && party.getLeader().equals(creator.getUniqueId())) {
            for (UUID uuid : party.getMembers()) {
                final Player player = UtilPlayer.get(uuid);
                joinTeam(player, team);
            }
        }

        return GC.C + "Team created with the name " + GC.V + name + GC.C + ".";
    }

    public boolean joinTeam(Player player, GameTeam team) {
        if (team.getPlayers().size() >= getMaxTeamSize()) {
            player.sendMessage(GC.II + "This team is full.");
            invites.remove(team.getName());
            return false;
        }

        Party party = getAssiGame().getPlugin().getPartyManager().getPartyOf(player, false);
        if (party != null && party.getLeader().equals(player.getUniqueId())) {
            if (team.getPlayers().size() + 1 + party.getMembers().size() > getMaxTeamSize()) {
                player.sendMessage(GC.II + "Your party is too big to join this team.");
                return false;
            } else {
                joinTeam(player, team);
            }
        }

        declineAllInvites(player);
        team.add(player);
        team.message(ChatColor.BOLD + GC.V + player.getName() + GC.II + ChatColor.BOLD + " has joined the team.");

        return true;
    }

    public void leaveTeam(Player player) {
        GameTeam team = getAssiGame().getTeamManager().getTeam(player);
        if (team == null) return;

        Party party = getAssiGame().getPlugin().getPartyManager().getPartyOf(player, false);
        if (party != null && !party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(GC.II + "You cannot leave your team.");
            return;
        }

        boolean findNewLeader = getAssiGame().getGamePhase() == GamePhase.LOBBY && isTeamLeader(player);

        team.remove(player);
        player.sendMessage(GC.C + "Left your team.");
        team.message(GC.V + ChatColor.BOLD + player.getName() + GC.II + ChatColor.BOLD + " left the team.");

        if (getAssiGame().getGamePhase() == GamePhase.LOBBY && party != null && party.getLeader().equals(player.getUniqueId())) {
            for (UUID uuid : party.getMembers()) {
                final Player player1 = UtilPlayer.get(uuid);
                if (player1 != null) {
                    team.getPlayers().remove(player1.getUniqueId());
                    party.messageParty(GC.II + "You have left the team as your team leader left.");
                }
            }
        }

        if (getAssiGame().getGamePhase() == GamePhase.LOBBY) {
            if (party == null || party.getLeader().equals(player.getUniqueId())) {
                player.getInventory().setItem(2, playerInvitesItem.getItemStack());
                if (teamCreateItem.hasGiveCondition() && teamCreateItem.getGiveCondition().onJoin(getAssiGame().getPlugin().getPlayerManager().getPlayer(player)))
                    player.getInventory().setItem(1, teamCreateItem.getItemStack());
            }
        }

        if (team.getPlayers().isEmpty()) {
            discardTeam(team);
            return;
        }

        if (findNewLeader) {

            Player newLeader = null;
            for (int i = 0; i < team.getPlayers().size(); i++) {
                newLeader = UtilPlayer.get(team.getPlayers().get(i));
                if (newLeader != null) break;
            }

            if (newLeader == null) {
                discardTeam(team);
                return;
            }
            if (teamTeamOptionsMenuMap.containsKey(team)) {
                teamTeamOptionsMenuMap.get(team).setLeader(newLeader.getName());
            }
            teams.replace(newLeader.getUniqueId(), team);
            team.message(GC.II + ChatColor.BOLD + "New team leader: " + GC.V + ChatColor.BOLD + newLeader.getName());
            newLeader.sendMessage(GC.II + "You are now the new team leader of " + team.getColor() + team.getName());
        }

    }

    public void discardTeam(GameTeam gameTeam) {
        invites.remove(gameTeam.getName());
        if (teamTeamOptionsMenuMap.containsKey(gameTeam)) {
            teamTeamOptionsMenuMap.get(gameTeam).destroySelf();
        }

        UUID oldLeader = null;
        for (Map.Entry<UUID, GameTeam> uuidGameTeamEntry : teams.entrySet()) {
            if (uuidGameTeamEntry.getValue().equals(gameTeam))
                oldLeader = uuidGameTeamEntry.getKey();
        }

        if (oldLeader != null) teams.remove(oldLeader);

        takenColors.remove(ItemBuilder.StackColor.fromChatColor(gameTeam.getColor()));
        getAssiGame().getTeamManager().removeTeam(gameTeam);
    }

    public void handleKick(GameTeam team, Player player) {
        if (!team.getPlayers().contains(player.getUniqueId())) return;

        Party party = getAssiGame().getPlugin().getPartyManager().getPartyOf(player, false);
        if (party != null && party.getLeader().equals(player.getUniqueId())) {
            team.getPlayers().removeAll(party.getMembers());
            team.message(GC.II + "The whole team of " + GC.V + player.getName() + GC.II + " has been removed.");
        } else if (party != null) {
            getAssiGame().getPlugin().getPartyManager().leaveParty(player, party);
            party = null;
        }

        team.getPlayers().remove(player.getUniqueId());

        if (party == null || party.getLeader().equals(player.getUniqueId())) {
            player.getInventory().setItem(2, playerInvitesItem.getItemStack());
            if (teamCreateItem.hasGiveCondition() && teamCreateItem.getGiveCondition().onJoin(getAssiGame().getPlugin().getPlayerManager().getPlayer(player)))
                player.getInventory().setItem(1, teamCreateItem.getItemStack());
        }

        player.sendMessage(GC.II + "You have been removed from the team.");
    }

    public Player getTeamLeader(GameTeam team) {
        for (Map.Entry<UUID, GameTeam> uuidGameTeamEntry : teams.entrySet()) {
            if (uuidGameTeamEntry.getValue() == team)
                return UtilPlayer.get(uuidGameTeamEntry.getKey());
        }
        return null;
    }

    public boolean isTeamLeader(Player player) {
        return enabled && teams.containsKey(player.getUniqueId());
    }

    public void openTeamUI(Player player) {
        if (!enabled) return;
        GameTeam team = getAssiGame().getTeamManager().getTeam(player);
        if (team == null || !teamTeamOptionsMenuMap.containsKey(team)) return;
        teamTeamOptionsMenuMap.get(team).open(player);
    }

    public List<Player> getLivePlayersOf(GameTeam team) {
        List<Player> players = Lists.newArrayList();
        for (UUID uuid : team.getPlayers()) {
            Player player = UtilPlayer.get(uuid);
            if (player == null || getAssiGame().getDeathLogger().hasDied(player)) continue;
            players.add(player);
        }
        return players;
    }

    public List<GameTeam> getRemainingTeams() {
        List<GameTeam> teams = Lists.newArrayList();
        for (GameTeam team : this.teams.values()) {
            if (!getLivePlayersOf(team).isEmpty())
                teams.add(team);
        }
        return teams;
    }

    public Collection<String> getInvitesOf(GameTeam team) {
        return invites.getOrDefault(team.getName(), Sets.newHashSet());
    }

    public Set<String> getInvitesOf(Player player) {
        Set<String> playerInvites = Sets.newHashSet();
        for (Map.Entry<String, Set<String>> stringSetEntry : invites.entrySet()) {
            if (stringSetEntry.getValue().contains(player.getName())) {
                playerInvites.add(stringSetEntry.getKey());
            }
        }
        return playerInvites;
    }

    public boolean hasInvited(GameTeam team, Player who) {
        return getInvitesOf(team).contains(who.getName());
    }

    public void declineInvite(Player player, GameTeam from) {
        if (!invites.containsKey(from.getName()))
            return;
        invites.get(from.getName()).remove(player.getName());
    }

    public void declineAllInvites(Player player) {
        for (Map.Entry<String, Set<String>> stringSetEntry : invites.entrySet()) {
            invites.get(stringSetEntry.getKey()).remove(player.getName());
        }
    }

    public void sendInviteTo(Player player, Player sender, GameTeam from) {
        if (getAssiGame().getTeamManager().hasTeam(player)) {
            sender.sendMessage(GC.II + "Player already in a team.");
            return;
        }
        if (invites.containsKey(from.getName()) && invites.get(from.getName()).contains(player.getName())) {
            sender.sendMessage(GC.II + "There was already an out-going invite to invite " + C.V + player.getName());
            return;
        }

        if (!invites.containsKey(from.getName()))
            invites.put(from.getName(), Sets.newHashSet());
        invites.get(from.getName()).add(player.getName());

        player.sendMessage(GC.C + "You have received an invite to join the team " + from.getColor() + from.getName() + GC.C + ". " +
                "You can choose to accept it from the " + ChatColor.YELLOW + ChatColor.BOLD + "Flower" + GC.C + " in your hand. (Sent by " + sender.getName() + ")");
        sender.sendMessage(GC.C + "Invite to join your team sent to " + GC.V + player.getName() + GC.C + ".");
    }

    public void unsendInviteTo(Player player, Player sender, GameTeam from) {
        if (!invites.containsKey(from.getName()) || !invites.get(from.getName()).contains(player.getName())) {
            sender.sendMessage(GC.C + "There was no pending invite to this player.");
            return;
        }

        if (!getAssiGame().getTeamManager().hasTeam(player)) {
            player.sendMessage(GC.II + "Your invite to " + from.getColor() + from.getName() + C.II + " was revoked. (By " + sender.getName() + ")");
        }

        sender.sendMessage(GC.II + "Invite to team unsent.");

        invites.get(from.getName()).remove(player.getName());
    }

    public ItemBuilder.StackColor getTeamColor() {
        return Arrays.stream(getAllTeamColors()).filter(this::isAvailable).findAny().orElse(ItemBuilder.StackColor.WHITE);
    }

    public ItemBuilder.StackColor[] getAllTeamColors() {
        return colors;
    }

    public boolean isAvailable(ItemBuilder.StackColor stackColor) {
        return !takenColors.contains(stackColor);
    }

    public int getMinTeams() {
        return minTeams;
    }

    public void setMinTeams(int minTeams) {
        this.minTeams = minTeams;
    }

    public int getAllSetMaxTeams() {
        return (int) Math.round(Math.ceil(getAssiGame().getAssiGameSettings().getMaxPlayers() / maxTeamSize));
    }

    public int getMaxTeams() {
        return (enabled ? overrideMaxTeamCount == -1 ?
                (int) Math.min(getAllSetMaxTeams(), Math.max(2, Math.ceil(getAssiGame().getLivePlayers().size() / maxTeamSize))) : overrideMaxTeamCount : -1);
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public int getOverrideMaxTeamCount() {
        return overrideMaxTeamCount;
    }

    public void setOverrideMaxTeamCount(int overrideMaxTeamCount) {
        this.overrideMaxTeamCount = overrideMaxTeamCount;
    }

    public Map<String, Set<String>> getInvites() {
        return invites;
    }

}
