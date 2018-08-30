package net.assimilationmc.ellie.assiuhc.game.task;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.event.AssiChatEvent;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assicore.util.UtilPlayer;
import net.assimilationmc.ellie.assicore.util.UtilTime;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.map.SingledGameType;
import net.assimilationmc.ellie.assiuhc.backend.map.TeamedGameType;
import net.assimilationmc.ellie.assiuhc.game.GameBoarder;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.games.util.GameState;
import net.assimilationmc.ellie.assiuhc.games.util.UHCTeam;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ellie on 24/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameKeeperTask implements Runnable, Listener {

    private UHCGame game;
    private int seconds;
    private int maxTime;
    private GameState gameState;
    private int task;

    private GameBoarder gameBoarder;

    private SingledGameType singledGameType;
    private TeamedGameType teamedGameType;

    public GameKeeperTask(UHCGame game){
        this.game = game;
        this.seconds = 121;
        //this.gameBoarder = new GameBoarder(game.getMap().getMapSize(), Bukkit.getWorld("uhc_"+game.getMap().getName())); // TODO: 11/03/2017
        setGameState(GameState.WAITING);
        Bukkit.getPluginManager().registerEvents(this, UHC.getPlugin(UHC.class));

        if(game.getMap().isForTeams()) {
            teamedGameType = game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed());
            switch (teamedGameType) {
                default:
                    this.maxTime = UtilTime.toSecondsFromMinutes(45);
            }
        }else {
            singledGameType = game.getMap().getSingledGameType().get(game.getMap().getSelectedSingled());
            switch (singledGameType) {
                default:
                    this.maxTime = UtilTime.toSecondsFromMinutes(45);
            }
        }

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHC.getPlugin(UHC.class), this, 60L, 20L);
        UHC.getPlugin(UHC.class).getGameManager().gameSQLBegin(game);
    }

    @Override
    public void run() {

        switch (gameState) {

            case WAITING:
                game.getPlayers().forEach(s -> {
                    if (Bukkit.getPlayer(s) != null) {
                        Bukkit.getPlayer(s).setLevel(seconds);
                    }
                });
                game.getSpectators().forEach(s -> {
                    if (Bukkit.getPlayer(s) != null) {
                        Bukkit.getPlayer(s).setLevel(seconds);
                    }
                });

                if (seconds == 0) {
                    if (enoughTeams() && enoughPlayers()) {
                        setGameState(GameState.WARMUP);
                        UHC.getPlugin(UHC.class).getGameManager().gameSQLBegin(game);
                        game.getTeamManager().getInvites().clear();
                        game.getTeamManager().assignTeamless();
                        messagePlayers("&lThe game has now begun! You will be teleported now! ", true);
                        messagePlayers(UColorChart.R + "There will be a warmup for "+UColorChart.VARIABLE +"5 minutes"+UColorChart.R+".", false);


                        game.getUhcSpawnManager().ready(game);

                        game.getPlayers().forEach(s -> {
                            Player player = Bukkit.getPlayer(s);
                            if (player != null) {
                                player.getInventory().clear();
                                player.getInventory().setArmorContents(null);
                                player.setFlying(false);
                                player.setAllowFlight(false);
                                player.setLevel(0);
                                player.setFoodLevel(20);
                                player.setWalkSpeed(0.2f);
                                player.setFlySpeed(0.2f);
                                player.setHealthScale(20);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));
                            }
                        });

                        if (game.getTeamManager() != null) {
                            game.getPlayers().forEach(s -> {
                                UHCTeam team = game.getTeamManager().getTeam(Bukkit.getPlayer(s));
                                if(team == null){
                                    Bukkit.getPlayer(s).sendMessage("Null checks evaluated you have no team :("); // wat
                                }else game.getUhcSpawnManager().teleport(game.getTeamManager().getTeam(Bukkit.getPlayer(s)).getName(), Bukkit.getPlayer(s));
                            });
                        } else
                            game.getPlayers().forEach(s -> game.getUhcSpawnManager().teleport(null, Bukkit.getPlayer(s)));

                        seconds = 300;
                        break;
                    }
                    seconds = 61;
                }

                if (seconds < 6) {

                    if (!enoughTeams()) {
                        seconds = 61;
                        messagePlayers("The game countdown has restarted since there were not enough teams! (" + UColorChart.VARIABLE + game.getTeamManager().getTeams().size() + UColorChart.R + "/" +
                                UColorChart.VARIABLE + (game.getMap().isForTeams() ? teamedGameType.getMinTeams() : singledGameType.getMinPlayers()) + UColorChart.R + ")", true);
                    } else if (!enoughPlayers()) {
                        seconds = 61;
                        messagePlayers("The game countdown has restarted since there were not enough players! (" + UColorChart.VARIABLE + game.getPlayers().size() + UColorChart.R + "/" + UColorChart.VARIABLE +
                                (game.getMap().isForTeams() ? teamedGameType.getMinPlayers() : singledGameType.getMinPlayers()) + UColorChart.R + ")", true);
                    } else {
                        messageAll("The game will be starting in " + UColorChart.VARIABLE + seconds + UColorChart.R + "!", true);
                    }
                } else {

                    if (seconds % 15 == 0) {
                        messagePlayers("The game will be starting in " + UColorChart.VARIABLE + seconds + UColorChart.R + " seconds!", true);
                    }
                }
                seconds--;
                break;

            case WARMUP:
                UHC.getPlugin(UHC.class).getGameManager().resetScore(game, game.getScoreboard().getData(String.valueOf(game.getId()), true));
                if (seconds == 0) {
                    setGameState(GameState.INGAME);
                    messagePlayers("&lYou are no longer invincible.", true);
                    seconds = maxTime;
                    break;
                }

                if (seconds == 297) {
                    game.getPlayers().forEach(s -> {
                        Player player = Bukkit.getPlayer(s);
                        if (player != null) {
                            player.removePotionEffect(PotionEffectType.BLINDNESS);
                            player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                        }
                    });
                }

                game.getPlayers().forEach(s -> {
                    Player player = Bukkit.getPlayer(s);
                    if (player != null) {
                        player.setHealth(20);
                        player.setFoodLevel(20);
                    }
                });

                seconds--;
                break;

            case INGAME:

                if (seconds % 100 == 0) {
                    messageAll("The game has &6" + UColorChart.VARIABLE +  Math.round(seconds / 60) + UColorChart.R + " minutes left!", true);
                }else if(seconds < 60){
                    if(seconds == 30 || seconds == 10 || seconds < 6){
                        messageAll("The game has "+ UColorChart.VARIABLE + seconds+ UColorChart.R + " seconds remaining!", true);
                        if(seconds == 0){
                            seconds = 30;
                            setGameState(GameState.FINISHED);
                        }
                    }

                }

                seconds--;
                break;
            case FINISHED:
                // do some stuff

                if(seconds == 1){
                    UHC.getPlugin(UHC.class).getGameManager().postGameFinish(game);
                }

                seconds--;
                break;
        }

        if(seconds < 0){
            messageAll("The game has been ended!", true);
            UHC.getPlugin(UHC.class).getGameManager().postGameFinish(game);
        }


    }

    public void messageAll(String message, boolean prefix){
        List<String> players = new ArrayList<>();
        players.addAll(game.getPlayers());
        players.addAll(game.getSpectators());
        players.forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                UtilPlayer.mINFO_noP(player, (prefix ?UHC.prefix : "")+message);
            }
        });
    }

    public void messagePlayers(String message, boolean prefix){
        game.getPlayers().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                UtilPlayer.mINFO_noP(player, (prefix ?UHC.prefix : "")+message);
            }
        });
    }

    public void messageSpectators(String message, boolean prefix){
        game.getSpectators().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                UtilPlayer.mINFO_noP(player, (prefix ?UHC.prefix : "")+message);
            }
        });
    }

    public void messageTeam(UHCTeam team, String message, boolean prefix){
        team.getMembers().keySet().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                UtilPlayer.mINFO_noP(player, (prefix ?UHC.prefix : "")+message);
            }
        });
    }

    private void messagePlayer(Player player, String message){
        UtilPlayer.mINFO_noP(player, UHC.prefix+message);
    }

    private boolean enoughPlayers(){
        return (game.getMap().isForTeams() ? game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMinPlayers() :
                game.getMap().getSingledGameType().get(game.getMap().getSelectedSingled()).getMinPlayers()) <= game.getPlayers().size();
    }

    private boolean enoughTeams(){
        return !game.getMap().isForTeams() || game.getMap().getTeamedGameTypes().get(game.getMap().getSelectedTeamed()).getMinTeams() <= game.getTeamManager().getTeams().size();
    }

    private void setGameState(GameState gameState){
        game.setGameState(gameState);
        this.gameState = gameState;
    }

    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        System.out.println("[ED] called");
        if(game.getSpectators().contains(e.getEntity().getName())){
            System.out.println("[ED] is spectator");
            e.setCancelled(true);
            return;
        }
        if(e.getEntity() instanceof Player && gameState != GameState.INGAME && game.getPlayers().contains(e.getEntity().getName())){
            System.out.println("[ED] is player, gamestate NOT INGAME, IS GAME PLAYER");
            e.setCancelled(true);
            return;
        }

        if(e.getEntity() instanceof Player && game.getPlayers().contains(e.getEntity().getName()) && gameState == GameState.INGAME){
            System.out.println("[ED] is player,  IS GAME PLAYER AND IS INGAME");
            Player player = (Player) e.getEntity();
            System.out.println("[ED] "+ player.getHealth());

            if(player.getHealth() < 1) {
                e.setCancelled(true);

                game.getPlayers().remove(player.getName());
                game.addSpectator(player, false);

                messageAll(this.getMessage(player.getName(), e.getCause()), true);

            }
        }
        System.out.println("[ED] event passed");
    }


    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e) {
        System.out.println("[EDBE] caled");
        if(game.getSpectators().contains(e.getEntity().getName())) {
            System.out.println("[EDBE] is spectatore");
            e.setCancelled(true);
            return;
        }

        if(e.getEntity() instanceof Player && gameState != GameState.INGAME && game.getPlayers().contains(e.getEntity().getName())){
            System.out.println("[EDBE] s player, gamestate NOT INGAME, IS GAME PLAYE");
            e.setCancelled(true);
            return;
        }

        if(e.getEntity() instanceof Player && game.getPlayers().contains(e.getEntity().getName()) && gameState == GameState.INGAME){

            Player player = (Player) e.getEntity();
            System.out.println("[EDBE] "+ player.getHealth());

            if(player.getHealth() < 1) {
                e.setCancelled(true);

                game.getPlayers().remove(player.getName());
                game.addSpectator(player, false);

                messageAll(this.getMessage(player.getName(), e.getCause()), true);

            }

        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        if((gameState != GameState.INGAME && gameState != GameState.WARMUP) && game.getPlayers().contains(e.getPlayer().getName())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent e) {
        if (gameState != GameState.INGAME && gameState != GameState.WARMUP && e.getWhoClicked() instanceof Player && e.getClickedInventory() != null && e.getClickedInventory().getName().equals("container.inventory") &&
                e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && game.getPlayers().contains(e.getWhoClicked().getName())) {
            e.getWhoClicked().closeInventory();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLoss(FoodLevelChangeEvent e){
        if(gameState != GameState.INGAME && game.getPlayers().contains(e.getEntity().getName())){
            e.setCancelled(true);
        }
        if(game.getSpectators().contains(e.getEntity().getName())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if((gameState != GameState.INGAME && gameState != GameState.WARMUP)&& game.getPlayers().contains(e.getPlayer().getName())){
            messagePlayer(e.getPlayer(), "You cannot break blocks yet.");
            e.setCancelled(true);
        }
        if(game.getSpectators().contains(e.getPlayer().getName())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if((gameState != GameState.INGAME && gameState != GameState.WARMUP)&& game.getPlayers().contains(e.getPlayer().getName())){
            messagePlayer(e.getPlayer(), "You cannot place blocks yet.");
            e.setCancelled(true);
        }
        if(game.getSpectators().contains(e.getPlayer().getName())) e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(gameState != GameState.WAITING && (game.getPlayers().contains(e.getPlayer().getName()) /*|| game.getSpectators().contains(e.getPlayer().getName())*/)){
            Location location = e.getTo();
            if(!game.getMap().getRegion().intersects(location, true)){
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(-1));
                e.getPlayer().damage(0.5d);
                //Bukkit.getPluginManager().callEvent(new EntityDamageEvent());
                game.getDedicatedWorld().getCBWorld().createExplosion(location, 0F, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AssiChatEvent e) {
        AssiPlayer player = e.getPlayer();
        if (game.getPlayers().contains(e.getPlayer().getName())) {

            if (gameState == GameState.WAITING || gameState == GameState.FINISHED) {
                e.setCancelled(true);
                if (teamedGameType != null) {
                    UHCTeam team = game.getTeamManager().getTeam(player.getBase());
                    if (team != null) {
                        messageAll(Util.color(team.getTeamColor().getChatColor() + "&l" + team.getName() + " &7" + player.getName() + ": " + team.getChatColor())+ e.getMessage(), false);
                        return;
                    }
                }
                messageAll(Util.color("&7" + player.getName() + ": " + e.getMessage()), false);
                return;
            }

            if(teamedGameType != null){
                UHCTeam team = game.getTeamManager().getTeam(player.getBase());
                if (team != null) {
                    e.setCancelled(true);
                    messageTeam(team, Util.color(team.getTeamColor().getChatColor() + "&l" + team.getName() + " &7" + player.getName() + ": " + team.getChatColor() + e.getMessage()), false);
                    return;
                }
                player.sendMessage(UHC.prefix+Util.color("&cTeam is null &7/helpop &cfor help."));
                return;
            }
            messageAll(Util.color("&7" + player.getName() + ": " + e.getMessage()), false);
            return;
        }

        if (game.getSpectators().contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            messageSpectators(Util.color("&7[SPEC] " + e.getPlayer().getName() + ": ") + e.getMessage(), false);
        }

    }

    public boolean forceStart(){
        if(gameState == GameState.WAITING || gameState == GameState.WARMUP || gameState == GameState.INGAME) {
            this.seconds = 6;
            return true;
        }
        return false;
    }

    public int getTask() {
        return task;
    }

    private Random r = new Random();

    private String getMessage(String player, EntityDamageEvent.DamageCause cause){
        int bound = r.nextInt(3);

        switch (cause) {
            case BLOCK_EXPLOSION:
                switch (bound) {
                    case 0:
                        return player + " was blown to pieces!";
                    case 1:
                        return player + " set off a fuse and didn't get away in time!";
                    case 2:
                        return player + " went boom.";
                }
                break;
            case DROWNING:
                switch (bound) {
                    case 0:
                        return player + " couldn't swim, too late for lessons now.";
                    case 1:
                        return player + " shared their last breath with the fish.";
                    case 2:
                        return player + " drank too much water.";
                }
                break;
            case ENTITY_EXPLOSION:
                return player + " got creeped.";
            case FALL:
                switch (bound) {
                    case 0:
                        return player + " fell to their inevitable doom.";
                    case 1:
                        return player + " slipped 'n fell 'n died.";
                    case 2:
                        return player + " proved adventure kills.";
                }
                break;
            case LIGHTNING:
                switch (bound) {
                    case 0:
                        return "Zeus literally killed " + player+".";
                    default:
                        return player + " got hit in the lightning.";
                }
            case FALLING_BLOCK:
                switch (bound) {
                    case 0:
                        return player + " got squished.";
                    case 1:
                        return player + " fell victim to a falling block.";
                    case 2:
                        return "A block fell on "+player+" and they became dead.";
                }
                break;
            case PROJECTILE:
                switch (bound){
                    case 0:
                        return player+" was sniped.";
                    case 1:
                        return player+" got an unfortunate arrow to the nee.";
                    case 2:
                        return player+" got a shot.";
                }
                break;
            case STARVATION:
                switch (bound){
                    case 0:
                        return player+" forgot to pack their packed-lunch, and for the worse...";
                    case 1:
                        return player+" forgot to eat.";
                    case 2:
                        return player+"";
                }


            default:
                return player+" died.";
        }

        return player+" died of natural causes.";
    }

    private boolean dropPackage() {
        int a = seconds / 2;
        int b = new Random().nextInt(a); // 300
        return (b / 2 <= b - 10); // 41 <= 150}
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
