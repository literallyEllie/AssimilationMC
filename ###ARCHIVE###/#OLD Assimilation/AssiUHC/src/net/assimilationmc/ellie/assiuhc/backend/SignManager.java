package net.assimilationmc.ellie.assiuhc.backend;

import com.google.gson.reflect.TypeToken;
import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import net.assimilationmc.ellie.assicore.util.FileUtil;
import net.assimilationmc.ellie.assicore.util.Util;
import net.assimilationmc.ellie.assiuhc.UHC;
import net.assimilationmc.ellie.assiuhc.game.UHCGame;
import net.assimilationmc.ellie.assiuhc.games.UHCPlayer;
import net.assimilationmc.ellie.assiuhc.util.UHCPerm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Ellie on 29/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SignManager implements Listener {

    private UHC uhc;
    private HashMap<String, String> signs;

    private File file;

    public SignManager(UHC uhc){
        this.uhc = uhc;
        this.signs = new HashMap<>();

        file = new File(uhc.getDataFolder(), "sign.json");
        if(!file.exists()){
            FileUtil.createFile(file);
        }

        Bukkit.getPluginManager().registerEvents(this, uhc);

        try {
            Type signJson = new TypeToken<HashMap<String, String>>(){}.getType();
            this.signs = Util.getGson().fromJson(new FileReader(file), signJson);
            if(this.signs == null || this.signs.isEmpty()) this.signs = new HashMap<>();
        }catch(IOException | IllegalStateException e){
            uhc.logE("Failed to read from sign.json");
        }
    }

    public void finish(){
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(Util.getGson().toJson(signs));
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
            uhc.logE("Failed to open writer");
        }
    }

    public void create(Location location, String map){
        System.out.println("Created at "+new SerializableLocation(location));
        signs.put(new SerializableLocation(location).toString(), map);
    }

    public void delete(Location location){
        SerializableLocation a = new SerializableLocation(location);
        signs.remove(a);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(Util.getGson().toJson(signs));
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean isSign(Location location){
        return signs.containsKey(new SerializableLocation(location).toString());
    }

    public Set<String> getSignsOf(String map){
        return signs.entrySet().stream().filter(serializableLocationStringEntry -> serializableLocationStringEntry.getValue().equalsIgnoreCase(map))
                .map(serializableLocationStringEntry ->  serializableLocationStringEntry.getKey()).collect(Collectors.toSet());
    }

    public HashMap<String, String> getSigns() {
        return signs;
    }




    @EventHandler
    public void onSignCreate(SignChangeEvent e){

        Block block = e.getBlock();
        if(block == null) return;

        Player player = e.getPlayer();
        if(!player.hasPermission(UHCPerm.SIGN.SIGN_CREATE)) return;

        String[] lines = e.getLines();

        if(lines[0].equalsIgnoreCase("UHC")){
            e.setLine(0, Util.color("&c&lUHC"));
            UHCMap map = UHC.getPlugin(UHC.class).getMapManager().getMap(lines[1]);
            e.setLine(1, "Join "+Util.color( map != null ? "&7"+map.getName() : "&c"+lines[1]));
            if(map != null) {

               // UHCGame game = UHC.getPlugin(UHC.class).getGameManager().getGameByMapName(map.getName());
              //  if(game != null){
              //      e.setLine(2, Util.color("&6"+game.getPlayers().size()+"&7/&6"+game.getMap().getMaxPlayers()));
              //      e.setLine(3, game.getGameState().toString());
               //     return;
      //          }

              //  e.setLine(2, Util.color("&60&7/&6"+map.getMaxPlayers()));
             //   e.setLine(3, GameState.WAITING.toString());

                create(e.getBlock().getLocation(), map.getName());
                return;
            }
            Util.mINFO_noP(player, UHC.prefix+"Map doesn't exist at &c"+lines[1]);
            return;
        }

        if(lines[0].equalsIgnoreCase("Stats")){
            e.setLine(0, Util.color("&c&lStats"));
            e.setLine(1, Util.color("&aClick this to view"));
            e.setLine(2, Util.color("&ayour stats!"));
            e.setLine(3, Util.color("&7-------------"));
        }

    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e){
        Block b = e.getClickedBlock();
        Player p = e.getPlayer();

        if(b == null || b.getType() != Material.WALL_SIGN) return;
        if(!p.hasPermission(UHCPerm.SIGN.SIGN_USE)) return;

        Action a = e.getAction();

        if(a != Action.RIGHT_CLICK_BLOCK) return;

        Sign sign = (Sign) b.getState();

        if(a.equals(Action.RIGHT_CLICK_BLOCK)){

            if(isSign(b.getLocation())) {

                if (sign.getLine(0).equalsIgnoreCase(Util.color("&c&lUHC"))) {

                    String map = ChatColor.stripColor(sign.getLine(1).split(" ")[1]);
                    UHCMap uhcMap = UHC.getPlugin(UHC.class).getMapManager().getMap(map);
                    if (uhcMap != null) {

                        //UHCGame game = UHC.getPlugin(UHC.class).getGameManager().getGameById(uhcMap);
               //         if (game == null) {
                  //          Util.mINFO_noP(p, UHC.prefix+"Looks like you're first here, quickly getting the game ready...");
                  //          game = UHC.getPlugin(UHC.class).getGameManager().startGame(uhcMap);
                  //      }

                  //      if (game.getGameState() == GameState.FINISHED) {
                  //          Util.mINFO_noP(p, UHC.prefix + "&cThis game has already finished");
                  //          return;
                  //      }

                    //    Util.mINFO_noP(p, UHC.prefix + "Sending you to game " + game.getMap().getName());
                    //    UHC.getPlugin(UHC.class).getGameManager().joinGame(p, game);


                        return;
                    }
                    Util.mINFO_noP(p, UHC.prefix + "&cMap not found.");
                }
            }

            if(sign.getLine(0).equalsIgnoreCase("&c&lStats")){

                UHCPlayer player = UHC.getPlugin(UHC.class).getSqlManager().getData(p.getName());

                //// TODO: 24/12/2016

            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){

        if(e.getBlock() == null || e.getBlock().getType() != Material.WALL_SIGN) return;

        Player p = e.getPlayer();
        if(isSign(e.getBlock().getLocation())){
            if(!p.hasPermission(UHCPerm.SIGN.SIGN_DELETE)){
                e.setCancelled(true);
                Util.mINFO_noP(p, UHC.prefix + "&cYou may not destroy that sign.");
                return;
            }
            delete(e.getBlock().getLocation());
            Util.mINFO_noP(p, UHC.prefix + "&cYou have broken a game sign");
        }

    }

    public void onGameUpdate(UHCGame game){
        getSignsOf(game.getMap().getName()).forEach(location -> {
            SerializableLocation loc = new SerializableLocation(location);

            if(Bukkit.getWorld(loc.getWorld()) != null && Bukkit.getWorld(loc.getWorld()).getBlockAt(loc.toLocation()).getType() == Material.WALL_SIGN){

                Sign a = (Sign) Bukkit.getWorld(loc.getWorld()).getBlockAt(loc.toLocation()).getState();
                //a.setLine(2, Util.color("&6"+game.getPlayers().size()+"&7/&6"+game.getMap().getMaxPlayers()));
                a.setLine(3, game.getGameState().toString());
                a.update();
            }
        });
    }


}
