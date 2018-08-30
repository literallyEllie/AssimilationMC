package net.assimilationmc.assicore.command.commands;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.command.AssiCommand;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.UtilPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSpeed extends AssiCommand {

    public CmdSpeed(AssiPlugin plugin) {
        super(plugin, "speed", "Set your current speed", Rank.DEMONIC, Lists.newArrayList("fspeed", "wspeed"), "<speed>", "[player]");
        requirePlayer();
    }

    @Override
    public void onCommand(CommandSender sender, String usedLabel, String[] args) {

        AssiPlayer assiSender = asPlayer(sender);

        Player target = null;
        if (args.length > 1 && assiSender.getRank().isHigherThanOrEqualTo(Rank.MOD)) {

            target = UtilPlayer.get(args[1]);
            if (target == null) {
                couldNotFind(sender, args[1]);
                return;
            }

        }

        if (target == null) target = (Player) sender;

        float speed;
        try {
            speed = Float.parseFloat(args[0]);
            if (speed > 10 || speed < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(prefix(usedLabel) + C.II + "Invalid speed, please just a number between 1 and 10");
            return;
        }

        speed = speed / 10;

        if (usedLabel.equalsIgnoreCase("speed") || (usedLabel.equalsIgnoreCase("fspeed") && target.isFlying())) {

            final boolean setFlying = target.isFlying();

            if (setFlying) {
                target.setFlySpeed(speed);
            } else target.setWalkSpeed(speed);

            target.sendMessage(prefix(usedLabel) + "Your " + (setFlying ? "fly" : "walk") + " speed has been updated to " + C.V + speed * 10 + C.C + ".");

            if (target != sender) {
                target.sendMessage(prefix(usedLabel) + "The " + (setFlying ? "fly" : "walk") + " speed of " + target.getDisplayName() + C.C + " has been updated to " + C.V + speed * 10 + C.C + ".");
            }

            return;
        }

        target.setWalkSpeed(speed);
        target.sendMessage(prefix(usedLabel) + "Your walk speed has been updated to " + C.V + speed * 10 + C.C + ".");

        if (target != sender) {
            target.sendMessage(prefix(usedLabel) + "The fly speed of " + target.getDisplayName() + C.C + " has been updated to " + C.V + speed * 10 + C.C + ".");
        }

    }


}
