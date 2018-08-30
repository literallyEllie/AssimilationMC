package net.assimilationmc.assiuhc.team.ui;

import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.ui.Button;
import net.assimilationmc.assicore.ui.UI;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.ItemBuilder;
import net.assimilationmc.assiuhc.game.UHCTeamedGame;
import net.assimilationmc.gameapi.team.GameTeam;
import net.assimilationmc.gameapi.util.GC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TeamColorsMenu extends UI {

    private UHCTeamedGame game;
    private GameTeam team;

    public TeamColorsMenu(UHCTeamedGame game, GameTeam team) {
        super(game.getPlugin(), C.C + "Choose a team color", 27);
        this.game = game;
        this.team = team;
    }

    @Override
    public void open(Player player) {
        refresh();
        super.open(player);
    }

    private void refresh() {
        removeAllButtons();

        for (ItemBuilder.StackColor stackColor : game.getUHCTeamManager().getAllTeamColors()) {
            if (stackColor.getChatColor() == null) continue;
            String pretty = StringUtils.capitalize(stackColor.name().toLowerCase().replace("_", " "));

            boolean available = game.getUHCTeamManager().isAvailable(stackColor);
            addButton(new Button() {
                @Override
                public ItemStack getItemStack(AssiPlayer assiPlayer) {
                    return new ItemBuilder((available ? Material.WOOL : Material.BARRIER)).setColor(stackColor)
                            .setDisplay(stackColor.getChatColor() + pretty)
                            .setLore(C.C, (available ? GC.C + "Click this color to set your team color to " + stackColor.getChatColor() + pretty + GC.C + "." :
                                    GC.II + "This team color has already been taken.")).build();
                }

                @Override
                public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                    if (!available) return;
                    team.setColor(stackColor.getChatColor());
                    team.message(GC.C + ChatColor.BOLD + "The team's color has been updated to " + stackColor.getChatColor() + pretty + GC.II + ChatColor.BOLD + ".");
                    closeInventory(assiPlayer.getBase());
                    game.getPlugin().getScoreboardManager().update(false);
                }
            });

        }

        addButton(26, new Button() {
            @Override
            public ItemStack getItemStack(AssiPlayer assiPlayer) {
                return new ItemBuilder(Material.IRON_DOOR).setDisplay(GC.II + "Back").build();
            }

            @Override
            public void onAction(AssiPlayer assiPlayer, ClickType clickType) {
                closeInventory(assiPlayer.getBase());
                game.getUHCTeamManager().openTeamUI(assiPlayer.getBase());
            }
        });


    }

}
