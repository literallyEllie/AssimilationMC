package net.assimilationmc.ellie.assiuhc.ui;

import com.google.common.base.Joiner;
import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.api.ui.DynamicUI;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.game.SingledGameType;
import net.assimilationmc.ellie.assiuhc.game.TeamedGameType;
import net.assimilationmc.ellie.assiuhc.util.GameBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Ellie on 27/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameCreateMenu extends DynamicUI implements Listener {

    private Player player;

    public GameCreateMenu(Player player) {
        super(27, "           &2Create a game", player, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17,
                18, 19, 20, 21, 22, 23, 24, 25), new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.LIME).setDisplay("&f").build());
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
        player.openInventory(this.build());
    }


    @Override
    public Inventory build() {
        addButton(11, ((player1, type) -> {
            closeInventory();
            new TeamedCreateMenu(player, new GameBuilder(true));
        }), new ItemBuilder(Material.WATCH).setDisplay("&cTeams")
                .setLore("&f", "&9Work together to win!", "&f",
                        "&cClick this to create a team game").build());

        addButton(13, ((player1, type) -> {
            closeInventory();

            int i = new Random().nextInt(2);
            if (i == 1) new SingularCreateMenu(player, new GameBuilder(false));
            else new TeamedCreateMenu(player, new GameBuilder(true));

        }), new ItemBuilder(Material.JUKEBOX).setDisplay("&cRandom").setLore("&f", "&cClick this to choose a random option").build());

        addButton(15, ((player1, type) -> {
            closeInventory();
            new SingularCreateMenu(player, new GameBuilder(false));
        }), new ItemBuilder(Material.COMPASS).setDisplay("&cSingles")
                .setLore("&f", "&9Can you survive on your own?", "&f",
                        "&cClick this to create a singles game").build());

        addButton(26, ((player1, type) -> closeInventory()), new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cQuit").build());


        return super.build();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        handleClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        handleClose(this, e);
    }

    private static class SingularCreateMenu extends DynamicUI implements Listener {

        private GameBuilder gameBuilder;

        public SingularCreateMenu(Player player, GameBuilder gameBuilder) {
            super(36, "     &2Create a game &8➞ &cSingles", player, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18,
                    26, 27, 28, 29, 30, 31, 32, 33, 34), new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.LIME).setDisplay("&f").build());
            this.gameBuilder = gameBuilder;
            Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
            player.openInventory(this.build());
        }

        @Override
        public Inventory build() {

            SingledGameType[] singledGameTypes = SingledGameType.values();
            int index = 0;
            for (int i = 10; i < 26 && index < singledGameTypes.length; i++) {
                if(isButton(i)) continue;
                SingledGameType singledGameType = singledGameTypes[index];

                addButton(i, (player, type) -> {
                    closeInventory();
                    gameBuilder.setSingledGameType(singledGameType);
                    new MapsCreateMenu(player, gameBuilder);
                }, singledGameType.getItemStack());

                index++;
            }

            addButton(35, (player1, type) -> {
                closeInventory();
                new GameCreateMenu(player1);
            }, new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cBack").build());


            return super.build();
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            handleClick(e);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            handleClose(this, e);
        }
    }

    private static class TeamedCreateMenu extends DynamicUI implements Listener {

        private GameBuilder gameBuilder;

        public TeamedCreateMenu(Player player, GameBuilder gameBuilder) {
            super(36, "    &2Create a game &8➞ &cTeamed",player, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17,
                    18, 26, 27, 28, 29, 30, 31, 32, 33, 34), new ItemBuilder(Material.STAINED_GLASS_PANE).setColor(ItemBuilder.StackColor.LIME).setDisplay("&f").build());
            this.gameBuilder = gameBuilder;
            Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
            player.openInventory(this.build());
        }

        @Override
        public Inventory build() {

            TeamedGameType[] teamedGameTypes = TeamedGameType.values();
            int index = 0;
            for (int i = 10; i < 26 && index < teamedGameTypes.length; i++) {
                if(isButton(i)) continue;
                TeamedGameType teamedGameType = teamedGameTypes[index];

                addButton(i, (player, type) -> {
                    closeInventory();
                    gameBuilder.setTeamedGameType(teamedGameType);
                    new MapsCreateMenu(player, gameBuilder);
                }, teamedGameType.getItemStack());


                index++;
            }

            addButton(35, (player1, type) -> {
                closeInventory();
                new GameCreateMenu(player1);
            }, new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cBack").build());

            return super.build();
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            handleClick(e);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            handleClose(this, e);
        }
    }

    private static class MapsCreateMenu extends DynamicUI implements Listener {

        private GameBuilder gameBuilder;

        public MapsCreateMenu(Player player, GameBuilder gameBuilder) {
            super(36, "&2Create a game &8➞ &2"+(gameBuilder.isTeamed()
                    ? gameBuilder.getTeamedGameType().getFriendly()
                    : gameBuilder.getSingledGameType().getFriendly()) +" &8➞ &cMap",player, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17,
                    18, 26, 27, 28, 29, 30, 31, 32, 33, 34), new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setColor(ItemBuilder.StackColor.LIME).setDisplay("&f").build());
            this.gameBuilder = gameBuilder;
            Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));
            player.openInventory(this.build());
        }

        @Override
        public Inventory build() {

            Object[] maps = gameBuilder.isTeamed() ? UHC.getPlugin(UHC.class).getMapManager().getMaps().values().stream()
                        .filter(uhcMap -> uhcMap.isToggled() && uhcMap.getTeamedGameTypes().contains(gameBuilder.getTeamedGameType())).toArray()
                 : UHC.getPlugin(UHC.class).getMapManager().getMaps().values().stream()
                        .filter(uhcMap -> uhcMap.isToggled() && uhcMap.getSingledGameType().contains(gameBuilder.getSingledGameType())).toArray();

            int index = 0;
            for (int i = 10; i < 26 && index < maps.length; i++) {
                if (isButton(i)) continue;
                UHCMap map = (UHCMap) maps[index];

                addButton(i, (player, type) -> {
                    closeInventory();
                    gameBuilder.setMap(map.getName());
                    gameBuilder.setBuilder(player.getUniqueId());
                    player.sendMessage(UHC.prefix+ Util.color("Your game is being setup now..."));
                    UHC.getPlugin(UHC.class).getGameManager().startGame(gameBuilder);
                }, new ItemBuilder(map.getMaterial()).setDisplay("&a&l"+map.getName())
                        .setLore("&f", "&aBuilders: &9"+ Joiner.on("&f, &9").join(map.getBuilders())).build());
                index++;
            }

            addButton(35, (player1, type) -> {
                closeInventory();
                if(gameBuilder.isTeamed()) new TeamedCreateMenu(player1, gameBuilder);
                else new SingularCreateMenu(player1, gameBuilder);
            }, new ItemBuilder(Material.WOOD_DOOR).setDisplay("&cBack").build());

            return super.build();
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            handleClick(e);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            handleClose(this, e);
        }
    }


}
