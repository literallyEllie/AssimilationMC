package net.assimilationmc.ellie.assiuhc.games;

import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.api.ItemBuilder;
import net.assimilationmc.ellie.assicore.manager.ModuleManager;
import net.assimilationmc.ellie.assicore.manager.SQLManager;
import net.assimilationmc.ellie.assicore.score.ScoreboardData;
import net.assimilationmc.ellie.assicore.util.ColorChart;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.backend.UHCMap;
import net.assimilationmc.ellie.assiuhc.game.*;
import net.assimilationmc.ellie.assiuhc.game.score.*;
import net.assimilationmc.ellie.assiuhc.ui.GameCreateMenu;
import net.assimilationmc.ellie.assiuhc.ui.GameJoinMenu;
import net.assimilationmc.ellie.assiuhc.ui.GamePlayerSelectorMenu;
import net.assimilationmc.ellie.assiuhc.ui.team.TeamCreateMenu;
import net.assimilationmc.ellie.assiuhc.ui.team.TeamOptionsMenu;
import net.assimilationmc.ellie.assiuhc.util.GameBuilder;
import net.assimilationmc.ellie.assiuhc.util.UColorChart;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.sql2o.Connection;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

/**
 * Created by Ellie on 19/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class GameManager implements Listener {

    private HashMap<Integer, UHCGame> games;
    private HashMap<String, TimeoutCountdown> disconnected;

    private SQLManager sqlManager;

    public GameManager(){
        games = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, getPlugin(UHC.class));
        disconnected = new HashMap<>();
        sqlManager = AssiCore.getCore().getModuleManager().getSQLManager();

        try(Connection connection = sqlManager.getSql2o().open()){
            connection.createQuery("CREATE TABLE IF NOT EXISTS `assimilation_uhc_gamelog` (" +
                    "`id` INT(100) NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT, "+
                    "`map` VARCHAR(100) NOT NULL, " +
                    "`players` LONGTEXT NULL, "+
                    "`deaths` LONGTEXT NULL, "+
                    "`winner` VARCHAR(100) NULL, "+
                    "INDEX(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;").executeUpdate().close();

        }
    }

    public HashMap<Integer, UHCGame> getGames() {
        return games;
    }

    public UHCGame startGame(UHCMap map) {
        UHCGame game = new UHCGame(map);
        game = init_setupSQLGame(game);
        games.put(game.getId(), game);
        getPlugin(UHC.class).logI("Loading map " + game.getMap().getName() + " by ID " + game.getId());
        getPlugin(UHC.class).getMapManager().preStartGame(game.getMap(), game.getId());
        getPlugin(UHC.class).logI("Loaded map " + game.getMap().getName() + ".");
        game.getMap().getLobbySpawn().setWorld(game.getDedicatedWorld().getName());
        return game;
    }

    public void startGame(GameBuilder gameBuilder) {
        UHCGame game = new UHCGame(getPlugin(UHC.class).getMapManager().getMap(gameBuilder.getMap()));
        game = init_setupSQLGame(game);
        games.put(game.getId(), game);
        getPlugin(UHC.class).logI("Loading map " + game.getMap().getName() + " by ID " + game.getId());
        getPlugin(UHC.class).getMapManager().preStartGame(game.getMap(), game.getId());
        getPlugin(UHC.class).logI("Loaded map " + game.getMap().getName() + ".");
        game.getMap().getLobbySpawn().setWorld(game.getDedicatedWorld().getName());

        if(!gameBuilder.isTeamed()) {
            for (int i = 0; i < game.getMap().getSingledGameType().size(); i++) {
                if(game.getMap().getSingledGameType().get(i) == gameBuilder.getSingledGameType()){
                    game.getMap().setSelectedSingled(i);
                    break;
                }
            }
        }else{
            for (int i = 0; i < game.getMap().getTeamedGameTypes().size(); i++) {
                if(game.getMap().getTeamedGameTypes().get(i) == gameBuilder.getTeamedGameType()){
                    game.getMap().setSelectedTeamed(i);
                    break;
                }
            }
        }
        setupGameScore(game);

        joinGame(Bukkit.getPlayer(gameBuilder.getBuilder()), game);
    }

    public void finish(){
        for (UHCGame game : games.values()) {
            forceEndGame(game);
        }
        disconnected.clear();
    }

    public void joinGame(Player player, UHCGame game){

        if(getPlayerGame(player) != null){
            Util.mINFO_noP(player, UHC.prefix+"You are already in a game.");
            return;
        }

        if(game.getGameState() == GameState.WAITING && game.getTeamManager().allowJoins()){

            game.getPlayers().add(player.getName());
            if(game.getTeamManager().passJoin(player)){
                //getPlugin(UHC.class).getSignManager().onGameUpdate(game);
                player.closeInventory();
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.setGameMode(GameMode.SURVIVAL);
                player.setFlying(false);
                player.setAllowFlight(false);
                player.setWalkSpeed(0.2f);

                ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().remove(player.getName());
                //ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().put(player.getName(), );

                player.setScoreboard(game.getScoreboard().getScoreboard());

                uhcLobbyInventory(player);

                player.teleport(game.getMap().getLobbySpawn().toLocation());

                Bukkit.getOnlinePlayers().forEach(o -> {
                    if(!game.getPlayers().contains(o.getName())) {
                        player.hidePlayer(o);
                    }
                });

                game.getPlayers().forEach(o -> {
                    Player gamePlayers = Bukkit.getPlayer(o);
                    if(gamePlayers != null) {
                        gamePlayers.showPlayer(player);
                    }
                });

                return;
            }else{
                game.getPlayers().remove(player.getName());
            }


            Util.mINFO_noP(player, UHC.prefix+"You may not join this game as there is no space.");
            return;
        }

        if(game.getGameState() != GameState.FINISHED){
            joinSpectator(player, game);
            Util.mINFO_noP(player, UHC.prefix+"You have joined the game as a spectator.");
        }
    }

    public void quitGame(Player player, UHCGame game, boolean onEvent){
        UHCTeam team = game.getTeamManager().getTeam(player);
        if(team != null){
            game.getTeamManager().quitTeam(team.getName(), player);
        }

        if(onEvent && game.getGameState().equals(GameState.INGAME)){
            if(!game.getMap().isForTeams() &&  game.getMap().getSingledGameType().get(0) == SingledGameType.RANKED) {
                game.getPlayers().forEach(s -> {
                    Player p = Bukkit.getPlayer(s);
                    if (p != null) {
                        Util.mINFO_noP(player, UHC.prefix + "Player "+ UColorChart.VARIABLE + player.getName() + UColorChart.R + " left the game and if they don't join within 3 minutes they will get a cooldown.");
                    }
                });
                disconnected.put(player.getName(), new TimeoutCountdown(player.getName(), game.getId(), Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(UHC.class), () ->
                        abandonGame(player.getName()), 3600L).getTaskId(),
                        Util.parseDuration("3m")));
            }else if(game.getGameState() == GameState.INGAME){

                if(game.getMap().isForTeams()){
                    UHCTeam plTeam = game.getTeamManager().getTeam(player);
                    if(plTeam != null){
                        plTeam.getMembers().entrySet().stream().filter(Map.Entry::getValue).forEach(stringBooleanEntry -> {

                        });
                    }

                }

            }
        }else{
            if(!onEvent) {
                ModuleManager.getModuleManager().getScoreboardManager().createNewScore(player, true);
                Bukkit.getOnlinePlayers().forEach(player::showPlayer);
            }
            ModuleManager.getModuleManager().getScoreboardManager().createNewScore(player, true);
        }
        if(game.getPlayers().size() != 0){
            game.getPlayers().remove(player.getName());
            player.teleport(AssiCore.getCore().getModuleManager().getConfigManager().getSpawn().toLocation());
            ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().remove(player.getName());
            Util.mainLobby(player);
            mainLobbyInventory(player);
        }

        if(game.getGameState() == GameState.WAITING && game.getPlayers().size() == 0){
            gamePrematureSQLQuit(game);
            postGameFinish(game);
        }

        Util.mINFO_noP(player, UHC.prefix+"You have left the game.");

    }

    public void joinSpectator(Player player, UHCGame game){
        ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().remove(player.getName());
        game.addSpectator(player, true);
        player.getInventory().setItem(2, playerSelector);
        player.getInventory().setItem(8, leave);
    }

    public void rejoinGame(Player player, UHCGame game){

    }

    public UHCGame getGameById(Integer id){
        return games.get(id);
    }

    private void abandonGame(String player){
        if(disconnected.containsKey(player)) {

            TimeoutCountdown timeoutCountdown = disconnected.get(player);
            UHCGame game = getGameById(timeoutCountdown.getGame());
            if(game != null){

                CooldownValues cooldownValues = giveCooldown(player);
                getPlugin(UHC.class).logI("Given "+player+" a competitive cooldown for "+Util.getDuration(cooldownValues.getFutureCooldown())+".");

                UHCTeam team = game.getTeamManager().getTeam(player);
                team.getMembers().remove(player);
                game.getPlayers().forEach(s -> {
                    Player player1 = Bukkit.getPlayer(s);
                    if (player1 != null) {
                        Util.mINFO_noP(player1, UHC.prefix+UColorChart.VARIABLE+player+UColorChart.R+" has been suspended from the competitive gameplay for "+UColorChart.VARIABLE+
                                Util.getDuration(cooldownValues.getFutureCooldown())+UColorChart.R+" for not reconnecting with in 3 minutes.");

                    }
                });
            }
            disconnected.remove(player);
        }else{

            if(Bukkit.getPlayer(player) == null) return;
            UHCGame game = getPlayerGame(Bukkit.getPlayer(player));
            if(game != null){

                CooldownValues cooldownValues = giveCooldown(player);
                getPlugin(UHC.class).logI("Given "+player+" a competitive cooldown for "+Util.getDuration(cooldownValues.getFutureCooldown())+".");

                UHCTeam team = game.getTeamManager().getTeam(player);
                team.getMembers().remove(player);
                game.getPlayers().forEach(s -> {
                    Player player1 = Bukkit.getPlayer(s);
                    if (player1 != null) {
                        Util.mINFO_noP(player1, UHC.prefix+"&c"+player+" has been suspended from the competitive gameplay for &6"+
                                Util.getDuration(cooldownValues.getFutureCooldown())+" amount of time&c for abandoning the match");

                    }
                });

            }
        }
    }

    public void forceEndGame(UHCGame game){
        game.getGameKeeperTask().messageAll("&cThis game has has been forcefully ended.", true);
        postGameFinish(game);
    }

    public void postGameFinish(UHCGame game){

        Bukkit.getScheduler().cancelTask(game.getGameKeeperTask().getTask());
        game.getScoreboard().getScoreboard().getObjectives().forEach(Objective::unregister);

        game.getPlayers().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                player.teleport(AssiCore.getCore().getModuleManager().getConfigManager().getSpawn().toLocation());
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFireTicks(0);
                player.setFoodLevel(20);
                ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().remove(player.getName());
                Bukkit.getOnlinePlayers().forEach(player::showPlayer);
                Util.mainLobby(player);
                mainLobbyInventory(player);
            }
        });
        game.getPlayers().clear();

        game.getSpectators().forEach(s -> {
            Player player = Bukkit.getPlayer(s);
            if(player != null){
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20);
                player.setFireTicks(0);
                player.setFoodLevel(20);
                ModuleManager.getModuleManager().getScoreboardManager().getPlayerScores().remove(player.getName());
                Bukkit.getOnlinePlayers().forEach(player::showPlayer);
                player.teleport(AssiCore.getCore().getModuleManager().getConfigManager().getSpawn().toLocation());
                Util.mainLobby(player);
                mainLobbyInventory(player);
            }
        });
        game.getSpectators().clear();

        game.getTeamManager().getTeams().clear();
        game.getTeamManager().getUntakenTeamColors().clear();
        game.getTeamManager().getTakenTeamColors().clear();
        getPlugin(UHC.class).getMapManager().postEndGame(game.getMap(), game.getId());
        games.remove(game.getId());

    }

    public UHCGame getPlayerGame(Player player){
        try {
            return  games.values().stream().filter(game -> game.getPlayers().contains(player.getName())).limit(1).collect(Collectors.toList()).get(0);
        }catch(Exception e){
            return null;
        }
    }

    public UHCGame getPlayerSpectating(Player player){
        try {
            return  games.values().stream().filter(game -> game.getSpectators().contains(player.getName())).limit(1).collect(Collectors.toList()).get(0);
        }catch(Exception e){
            return null;
        }
    }

    public CooldownValues giveCooldown( String player ) {

        UHCPlayer uhcPlayer = getPlugin(UHC.class).getSqlManager().getData(player);
        if (uhcPlayer != null) {

            CooldownValues cooldownValues;
            uhcPlayer.setCooldownStrike(uhcPlayer.getCooldownStrike() + 1);
            int cooldown = uhcPlayer.getCooldownStrike();
            long future;

            if (CooldownValues.byStrike(cooldown) != null) {
                cooldownValues = CooldownValues.byStrike(cooldown);
                future = cooldownValues.getFutureCooldown();
                uhcPlayer.setCooldownEnd(future);
            } else {
                cooldownValues = CooldownValues.byStrike(1);
                future = cooldownValues.getFutureCooldown();
                uhcPlayer.setCooldownEnd(future);
            }
            uhcPlayer.setCooldown(true);

            getPlugin(UHC.class).getSqlManager().pushPlayer(uhcPlayer);
            return cooldownValues;
        }
        return CooldownValues.FIRST;
    }


    public HashMap<String, TimeoutCountdown> getDisconnected() {
        return disconnected;
    }


    private ItemStack selector = new ItemBuilder(Material.COMPASS).setDisplay("&c&lPlay").build();
    private ItemStack gameStarter = new ItemBuilder(Material.EMERALD_BLOCK).setDisplay("&a&lBegin a game").build();

    private ItemStack teams = new ItemBuilder(Material.DIAMOND_HELMET).setDisplay("&aTeams").setLore("&cLeft click this to open the team menu").build();
    private ItemStack leave = new ItemBuilder(Material.REDSTONE_BLOCK).setDisplay("&cLeave").build();

    private ItemStack playerSelector = new ItemBuilder(Material.COMPASS).setDisplay("&cPlayer Selector").build();

    public void uhcLobbyInventory(Player player){
        player.getInventory().clear();
        Inventory inventory = player.getInventory();
        inventory.setItem(1, teams);
        inventory.setItem(8, leave);
    }

    public void mainLobbyInventory(Player player){
        player.setLevel(0);
        player.setTotalExperience(0);
        player.getInventory().clear();
        player.getInventory().setItem(5, selector);
        if(games.size() < 10) {
            player.getInventory().setItem(3, gameStarter);
        }
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        ItemStack itemStack = e.getItem();
        Player player = e.getPlayer();

        if (itemStack != null && itemStack.getItemMeta() != null) {

            if (itemStack.equals(teams) || itemStack.equals(leave) || itemStack.equals(playerSelector)) {

                e.setCancelled(true);

                UHCGame game = getPlayerGame(player);

                if (game == null) {
                    player.getInventory().remove(teams);
                    player.getInventory().remove(leave);
                    player.getInventory().remove(playerSelector);
                    return;
                }

                if (itemStack.equals(teams)) {

                    player.getInventory().setArmorContents(null);


                    UHCTeam team = game.getTeamManager().getTeam(player);

                    if (team == null) {

                        new TeamCreateMenu(game.getTeamManager(), player);

                    } else {
                        new TeamOptionsMenu(team, player);
                    }
                } else if (itemStack.equals(leave)) {
                    quitGame(player, game, false);
                }

                if(itemStack.equals(playerSelector)){
                    new GamePlayerSelectorMenu(player, game);
                }

                return;
            }

            if (itemStack.equals(selector)) {
                e.setCancelled(true);
                new GameJoinMenu(player);
            }

            if (itemStack.equals(gameStarter)) {
                if (games.size() >= 10) {
                    player.getInventory().remove(gameStarter);
                    Util.mINFO_noP(player, UHC.prefix + "&cMax games threshold reached. You must join an existing game.");
                    return;
                }

                e.setCancelled(true);
                new GameCreateMenu(player);
            }

        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        if(disconnected.get(player.getName()) != null){
            TimeoutCountdown timeoutCountdown = disconnected.get(player.getName());
            Util.mINFO_noP(player, timeoutCountdown.toMessage());
        }

        games.values().forEach(game -> game.getPlayers().forEach(s -> {
            Player p = Bukkit.getPlayer(s);
            if(p != null){
                p.hidePlayer(player);
            }
        }));

        mainLobbyInventory(player);
    }

    @EventHandler
    public void onDisconnnect(PlayerQuitEvent e){

        Player player = e.getPlayer();

        UHCGame game = getPlayerGame(player);
        if(game != null){
            this.quitGame(player, game, true);
        }
    }

    @EventHandler
    public void onMotd(ServerListPingEvent e){
        List<String> motd = Arrays.asList("       &7There are currently &a"+ games.size()+" &7games occurring!",
                "             &7Join &a"+Bukkit.getOnlinePlayers().size()+" &7players playing &c&lUHC &7now!",
                "             &7We always have room for 1 more!");
        int random = new Random().nextInt(3);

        e.setMaxPlayers(Bukkit.getOnlinePlayers().size()+1);
        e.setMotd(Util.color("&7&l&m----------> &r &a&lAssi&2&lmilation &C&LUHC &7&l&m-<----------&r\n"+motd.get(random)));
    }

    private UHCGame init_setupSQLGame(UHCGame uhcGame) {
        //RESERVE SLOT
        int id;
        try (Connection connection = sqlManager.getSql2o().open()) {
            List<UHCGame> games = connection.createQuery("SELECT id FROM `assimilation_uhc_gamelog` ORDER BY id DESC LIMIT 1;").executeAndFetch(UHCGame.class);
            if (games.isEmpty()) {
                id = 0;
            } else {
                id = games.get(0).getId() + 1;
            }
            connection.createQuery("INSERT INTO `assimilation_uhc_gamelog` (map) VALUES (:map);").addParameter("map", uhcGame.getMap().getName()).executeUpdate().close();
        }
        uhcGame.setId(id);
        return uhcGame;
    }

    public void gameSQLBegin(UHCGame uhcGame){
        int id = uhcGame.getId();
        try (Connection connection = sqlManager.getSql2o().open()) {
            List<UHCGame> games = connection.createQuery("SELECT id FROM `assimilation_uhc_gamelog` WHERE id = :id").addParameter("id", id).executeAndFetch(UHCGame.class);
            if(games.isEmpty()){
                init_setupSQLGame(uhcGame);
            }else{
                UHCGame game = games.get(0);
                connection.createQuery("INSERT INTO `assimilation_uhc_gamelog` (players) VALUES (:players);").addParameter("players", Util.getGson().toJson(game.getPlayers())).executeUpdate().close();
            }
            connection.close();
        }
    }

    private void gamePrematureSQLQuit(UHCGame uhcGame){
        int id = uhcGame.getId();
        try(Connection connection = sqlManager.getSql2o().open()){
            connection.createQuery("DELETE FROM `assimilation_uhc_gamelog` WHERE id = :id;").addParameter("id", id).executeUpdate().close();
        }
    }

    // Map scores will be indexed by their game ID
    private void setupGameScore(UHCGame game) {
        ScoreboardData data = ModuleManager.getModuleManager().getScoreboardManager().getData(String.valueOf(game.getId()), true);
        final boolean t = game.getMap().isForTeams();

        data.writeEmpty();

        data.write(Util.color(ColorChart.VARIABLE + "Map"));
        data.writeCustom(new ScoreUMap());
        data.writeEmpty();

        if(t) {
            data.write(Util.color(ColorChart.VARIABLE + "Team"));
            data.writeCustom(new ScoreUTeam());
            data.writeEmpty();
        }

        data.write(Util.color(ColorChart.VARIABLE + "Players"));
        if (t) {
            data.writeCustom(new ScoreUTeamCount());
        }else{
            data.writeCustom(new ScoreUSingleCount());
        }
        data.writeEmpty();
    }

    private void scoreUpdate(UHCGame game) {

    }

}
