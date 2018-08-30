package net.assimilationmc.assicore.chat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.chat.cmd.CmdChat;
import net.assimilationmc.assicore.packetwrapper.WrapperPlayClientTabComplete;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager extends Module {

    private static final String MESSAGE_UNKNOWN_CMD = ChatColor.GRAY + "Unknown command.";
    private List<String> localDisallowed = Lists.newArrayList("me", "pl", "?", "plugins", "version", "ver", "icanhasbukkit", "op", "deop", "reflex");
    private String color, format;

    private ChatPolicy chatPolicy;
    private Map<UUID, Long> chatDelays;

    private Map<UUID, String> tabQueries;

    private Pattern chatFilter, hackFilter;

    public ChatManager(AssiPlugin plugin) {
        super(plugin, "Chat Manager");
    }

    @Override
    protected void start() {
        setChatPolicy(null);
        this.chatDelays = Maps.newHashMap();
        this.tabQueries = Maps.newHashMap();

        this.color = "abcdefABCDEF0123456789";
        this.format = "lmnorLMNOR";

        /* Tab Complete block */
        blockTabComplete();
        unknownCommandListener();

        getPlugin().getCommandManager().registerCommand(new CmdChat(getPlugin()));

        this.chatFilter = Pattern.compile("(sh([i1])t(ing)?)|((mother)?fucc?k?(er)?|fk ?(yo)?u)|(c([u0o])ck)|(puss?([yi]))|" +
                "(n([i1])gg?(e?r))|(negr([o0]))|(knee ?gro)|(([ck])unt)|(kkk)|(w([a@])nk(e?r)?)|(f([a@])g(g?ot)?)|(d([i1])ck?)|(pen([i1])([s5]))|" +
                "(autist(ic)?)|(whore)|(slag)|(slut)|(n([i1])+g+a)|(despacito)|(kys)|(bast[a@]rd)|(douche)|(hitler)|(nazi)|(commie)|" +
                "(fascist)|(retard(ed)?)|(re{3,})|(spaz?z)|(spank)|(speco)|(knob)|(c([o0]){2,}n)|(foreskin)|" +
                "(vag(([i1])na)?)|(cl([i1])t)|(abort(ed|tion))|(condom)|(bitch(e[sr]|ing?)?)|(ddos)|(dox)|(stfu)|(rape)|([jg]ew)" +
                "|(fornite)|(roblox)|(cum)|(pedo(phile)?)|(se(c{2,}|x))|([卍卐])", Pattern.CASE_INSENSITIVE);

        this.hackFilter = Pattern.compile("(h[a@](ck|x+)(ing)?)", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected void end() {
        chatDelays.clear();
        tabQueries.clear();
    }

    private void blockTabComplete() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) return;
                        WrapperPlayClientTabComplete playClientTabComplete = new WrapperPlayClientTabComplete(event.getPacket());

                        if (!playClientTabComplete.getText().startsWith("/")) return;
                        String query = playClientTabComplete.getText().substring(1);
                        if (query.contains(" ")) return;

                        tabQueries.put(event.getPlayer().getUniqueId(), query.trim());
                    }
                });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Server.TAB_COMPLETE) return;

                AssiPlayer player = ChatManager.this.getPlugin().getPlayerManager().getPlayer(event.getPlayer());
                if (!tabQueries.containsKey(player.getUuid())) return;

                String query = tabQueries.get(player.getUuid());

                final String[] commands = ChatManager.this.getPlugin().getCommandManager().getCommands().stream().filter(assiCommand -> {
                    if (!player.getRank().isHigherThanOrEqualTo(assiCommand.getMinRank())) return false;

                    if (assiCommand.getLabel().toLowerCase().startsWith(query)) return true;
                    for (String s : assiCommand.getAliases()) {
                        if (s.toLowerCase().startsWith(query)) return true;
                    }
                    return false;
                }).map(assiCommand -> "/" + assiCommand.getName().trim()).toArray(String[]::new);

                tabQueries.remove(player.getUuid());
                if (commands.length != 0) {
                    event.getPacket().getStringArrays().write(0, commands);
                }
            }
        });
    }

    private void unknownCommandListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() != PacketType.Play.Server.CHAT) return;
                        if (event.getPacket().getChatComponents().read(0) == null) return;

                        String jsonMessage = event.getPacket().getChatComponents().read(0).getJson();

                        if (jsonMessage.contains("command unrecognised")) {
                            event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromText(MESSAGE_UNKNOWN_CMD));
                        }

                    }
                });
    }

    @EventHandler(ignoreCancelled = true)
    public void on(final AsyncPlayerChatEvent e) {
        final Player bukkitPlayer = e.getPlayer();
        // fallback
        e.setFormat(bukkitPlayer.getDisplayName() + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%2$s");

        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(bukkitPlayer.getUniqueId());

        if (chatPolicy.isRestrictedChat() && !player.getRank().isHigherThanOrEqualTo(chatPolicy.getRequiredRankChat())) {
            player.sendMessage(C.II + "Sorry, looks like you can't talk right now!");
            e.setCancelled(true);
            return;
        }

        if (!player.getRank().isHigherThanOrEqualTo(Rank.HELPER) && !speak(player.getUuid())) {
            player.sendMessage(C.II + "Sorry it looks like you're still on chat cool-down! Try again in a couple seconds.");
            e.setCancelled(true);
            return;
        }

        if (!player.getRank().isHigherThanOrEqualTo(Rank.ADMIN)) {
            String badWord = passesChatFilter(e.getMessage());
            if (badWord != null) {

                bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ANVIL_LAND, 5f, 3f);
                player.sendMessage(C.II);
                player.sendMessage(C.II + ChatColor.BOLD + "Hey, it looks like what you just said triggered our chat filter.");
                player.sendMessage(C.II + "Don't worry, for now its just between us, but everyone else would appreciate it if you didn't " +
                        "waste your time trying to bypass this filter!");
                player.sendMessage(C.C + "If what you just said (" + C.V + badWord + C.C + ") isn't a bad thing, then just tell a member of staff.");
                player.sendMessage(C.II);

                e.setCancelled(true);
                return;
            }

            badWord = passesHackFilter(e.getMessage());
            if (badWord != null) {

                bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 5f, 3f);
                player.sendMessage(C.II);
                player.sendMessage(C.II + ChatColor.BOLD + "Hey, please use " + C.V + ChatColor.BOLD + "/helpop" + C.II + ChatColor.BOLD + " to report hackers.");
                player.sendMessage(C.II + "If you hackusate, they will probably just turn off their hacks or make it more subtle. So just tell staff only instead.");
                player.sendMessage(C.C + "If what you just said (" + C.V + badWord + C.C + ") isn't a bad thing, then just tell a member of staff.");
                player.sendMessage(C.II);

                e.setCancelled(true);
                return;
            }

        }

        // donator feature
        if (player.getRank().isHigherThanOrEqualTo(Rank.DEMONIC)) {
            e.setMessage(e.getMessage().replace("/shrug", "¯\\_(ツ)_/¯"));
        }

        try {
            final ChatMessage message = chatPolicy.handleChat(new ChatMessage(player, e.getFormat(), e.getMessage()));
            if (message.isCancelled()) {
                e.setCancelled(true);
                return;
            }

            e.setMessage(message.getMessage());
            e.setFormat(message.getFormat().replace("{display}", player.getDisplayName())
                    .replace("{message}", "%2$s")
                    .replace("{name}", player.getName())
                    .replace("{rank}", player.getRank().getPrefix()));

        } catch (Throwable ex) {
            getPlugin().getLogger().warning("Error processing chat message!");
            ex.printStackTrace();
        }

        if (player.getRank().isHigherThanOrEqualTo(Rank.ADMIN)) {
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        } else if (player.getRank().isHigherThanOrEqualTo(Rank.DEMONIC)) {
            for (char s : color.toCharArray()) {
                e.setMessage(e.getMessage().replace("&" + s, "§" + s));
            }
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerCommandPreprocessEvent e) {
        final AssiPlayer player = getPlugin().getPlayerManager().getPlayer(e.getPlayer());
        final String command = e.getMessage().split(" ")[0];

        if (!player.getRank().isHigherThanOrEqualTo(Rank.ADMIN)) {
            if (command.contains(":") || localDisallowed.stream().anyMatch(s -> command.equalsIgnoreCase("/" + s))
                    || command.contains("reflex")) {
                player.sendMessage(MESSAGE_UNKNOWN_CMD);
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        chatDelays.remove(e.getPlayer().getUniqueId());
    }

    public ChatPolicy getChatPolicy() {
        return chatPolicy;
    }

    public void setChatPolicy(ChatPolicy chatPolicy) {
        if (chatPolicy == null)
            chatPolicy = new ChatPolicy();
        this.chatPolicy = chatPolicy;
    }

    public boolean speak(UUID uuid) {
        if (!chatPolicy.hasChatDelay()) return true;
        if (!chatDelays.containsKey(uuid)) {
            chatDelays.put(uuid, UtilTime.now());
            return true;
        }

        boolean delayPassed = UtilTime.elapsed(chatDelays.get(uuid), TimeUnit.SECONDS.toMillis(chatPolicy.getChatDelay()));
        if (!delayPassed)
            return false;
        chatDelays.replace(uuid, UtilTime.now());
        return true;
    }

    public Map<UUID, Long> getChatDelays() {
        return chatDelays;
    }

    public String passesChatFilter(String message) {
        Matcher matcher = chatFilter.matcher(message.replaceAll("[ .,()]", ""));
        return matcher.find() ? matcher.group() : null;
    }

    public String passesHackFilter(String message) {
        Matcher matcher = hackFilter.matcher(message.replaceAll("[ .,()]", ""));
        return matcher.find() ? matcher.group() : null;
    }

}
