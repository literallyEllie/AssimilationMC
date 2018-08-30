package net.assimilationmc.assicore.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.assimilationmc.assicore.AssiPlugin;
import org.bukkit.entity.Player;

public class UtilBungee {

    public static void sendPlayer(AssiPlugin plugin, Player player, String server) {
        player.sendMessage(C.C + "Connecting to " + C.V + server + C.C + "...");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

}
