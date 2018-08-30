package net.assimilationmc.assicore.patch;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.util.UtilTime;
import org.apache.commons.io.Charsets;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PatchPayload implements AssiPatch {

    private static final Map<Player, Long> PACKET_USAGE = new ConcurrentHashMap<>();
    private AssiPlugin plugin;
    private List<String> exploitChannels;
    private PacketListener listener;

    public PatchPayload(AssiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        this.exploitChannels = Lists.newArrayList("MC|BSIGN", "MC|BEdit", "REGISTER");

        listener = new PacketAdapter(plugin, PacketType.Play.Client.CUSTOM_PAYLOAD) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                checkPacket(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(listener);

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Iterator<Map.Entry<Player, Long>> iterator = PACKET_USAGE.entrySet().iterator(); iterator.hasNext(); ) {
                Player player = iterator.next().getKey();
                if (!player.isOnline() || !player.isValid())
                    iterator.remove();
            }
        }, 20L, 20L);

    }

    @Override
    public void unregister() {
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        if (exploitChannels != null) exploitChannels.clear();
        PACKET_USAGE.clear();
    }

    private void checkPacket(PacketEvent event) {
        Player player = event.getPlayer();
        long lastPacket = PACKET_USAGE.getOrDefault(player, -1L);

        if (lastPacket == -2L) {
            event.setCancelled(true);
            return;
        }

        final String name = event.getPacket().getStrings().readSafely(0);
        if (!exploitChannels.contains(name))
            return;

        try {
            if ("REGISTER".equals(name))
                checkChannels(event);
            else {
                if (UtilTime.elapsed(lastPacket, 100L)) {
                    PACKET_USAGE.put(player, System.currentTimeMillis());
                } else
                    throw new IOException("Packet flood");

                checkNbtTags(event);
            }
        } catch (IOException ex) {
            PACKET_USAGE.put(player, -2L);
//            plugin.getServer().getScheduler().runTask(plugin, () -> player.sendMessage(C.II));
            plugin.getDiscordCommunicator().messageChannel("ADMIN", "**" + player.getName() + "** attempted to exploit a custom payload on " +
                    plugin.getServerData().getId() + ": " + ex.getMessage());
            event.setCancelled(true);
        }
    }

    private void checkNbtTags(PacketEvent event) throws IOException {
        final PacketContainer container = event.getPacket();
        final ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();
        final byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);

        try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes))) {
            final ItemStack itemStack = StreamSerializer.getDefault().deserializeItemStack(inputStream);
            if (itemStack == null)
                throw new IOException("Unable to deserialize ItemStack");

            final NbtCompound root = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            if (root == null)
                throw new IOException("No NBT tag");
            else if (!root.containsKey("pages"))
                throw new IOException("No 'pages' NBT compound was found");
            else {
                NbtList<String> pages = root.getList("pages");
                if (pages.size() > 50)
                    throw new IOException("Too many pages");
            }
        } finally {
            buffer.release();
        }
    }

    private void checkChannels(PacketEvent event) throws IOException {
        try {
            if (event.isPlayerTemporary()) {
                return;
            }
        } catch (NoSuchMethodError ignored) {}

        int channelsSize = event.getPlayer().getListeningPluginChannels().size();
        final PacketContainer container = event.getPacket();
        final ByteBuf buffer = container.getSpecificModifier(ByteBuf.class).read(0).copy();
        try {
            for (int i = 0; i < buffer.toString(Charsets.UTF_8).split("\0").length; i++)
                if (++channelsSize > 124)
                    throw new IOException("Too many channels");
        } finally {
            buffer.release();
        }
    }

}
