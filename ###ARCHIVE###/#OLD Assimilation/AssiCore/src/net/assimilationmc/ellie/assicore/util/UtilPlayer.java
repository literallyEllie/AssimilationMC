package net.assimilationmc.ellie.assicore.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static net.assimilationmc.ellie.assicore.util.Util.color;

public class UtilPlayer {



    public static void mINFO(CommandSender commandSender, String message){
        commandSender.sendMessage(color(Util.prefix()+message));
    }

    public static void mWARN(CommandSender commandSender, String message){
        commandSender.sendMessage(color("&c&lError! &c"+message));
    }

    public static void mINFO_noP(CommandSender commandSender, String message){
        commandSender.sendMessage(color(message));
    }

    public static void kickPlayer(Player player, String reason){
        player.kickPlayer(color("&2Assi&amilationMC")+color("&7\n"+reason));
    }

    public static void kickPlayer(Player player, String reason, boolean prefix){
        player.kickPlayer(color("&7\n"+reason));
    }

    public static void mainLobby(Player player){
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        player.setHealthScale(20);
        player.setFoodLevel(20);
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 30L, 20L);
        player.setWalkSpeed(0.6F);
        player.setGameMode(GameMode.ADVENTURE);

        if(player.hasPermission(PermissionLib.LOBBY.FLY)){
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        player.setExp(0);
        player.setLevel(0);

    }

    public static void sendHotbar(Player player, String message){
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat();
        setValue(packetPlayOutChat, "a", IChatBaseComponent.ChatSerializer.a("{\"text\":\""+Util.color(message)+"\",\"color\":\"white\"}"));
        setValue(packetPlayOutChat, "b", (byte) 2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    private static void setValue(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
