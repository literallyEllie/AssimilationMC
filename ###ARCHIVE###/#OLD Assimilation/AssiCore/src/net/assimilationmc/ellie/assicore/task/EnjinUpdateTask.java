package net.assimilationmc.ellie.assicore.task;

import com.google.gson.reflect.TypeToken;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.task.fakeenjin.*;
import net.assimilationmc.ellie.assicore.task.fakeenjin.rpc.EnjinRPC;
import net.assimilationmc.ellie.assicore.task.fakeenjin.rpc.RPCData;
import net.assimilationmc.ellie.assicore.task.fakeenjin.rpc.SyncResponse;
import net.assimilationmc.ellie.assicore.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinUpdateTask implements Runnable {

    private final String authKey;

    public EnjinUpdateTask(String authKey){
        this.authKey = authKey;
    }

    @Override
    public void run() {
        //AssiCore.getCore().logI("Sending Enjin some fake goodies...");
        try {
            send();
        } catch (Exception e) {
            AssiCore.getCore().logW("[Enjin] Oh no! Enjin are on to us! Error...");
            e.printStackTrace();
        }
    }

    public void send() {

        EnjinStatus status = new EnjinStatus(System.getProperty("java.version"),
                getVersion(), getPlugins(), isPermissionsAvailable(), "3.1.4", getWorlds(), getGroups(), getMaxPlayers(),
                getOnlineCount(), getOnlinePlayers(), getPlayerGroups(), (double) Math.round(AssiCore.getCore().getMonitorTask().getAvgTPS()),
                new ExecutedCommandsConfig().getExecutedCommands(),
                getVotes(), null);

        RPCData<SyncResponse> data = sync(status);
       // AssiCore.getCore().logI("[Enjin] Sync completed");


        if(data == null){
            AssiCore.getCore().logW("[Enjin] Enjin data returned null!");
            return;
        }

    //   AssiCore.getCore().logI("[Enjin] sent "+data.getRequest().toJSON().toJSONString());
    //    AssiCore.getCore().logI("[Enjin] recieed "+data.getResponse().toJSON().toJSONString());

        if(data.getError() != null){
            AssiCore.getCore().logW("[Enjin] Error from Enjin whilst syncing: "+data.getError().getMessage());
        }else {
            SyncResponse syncResponse = data.getResult();
            if ((syncResponse != null && syncResponse.getStatus() != null)) {
                if (!syncResponse.getStatus().equalsIgnoreCase("ok"))
                    AssiCore.getCore().logW("[Enjin] Sync response isn't ok! Status: " + data.getResult().getStatus());

                for (EnjinInstruction enjinInstruction : syncResponse.getInstructions()) {
                }

                syncResponse.getInstructions().clear();
            }
        }

    }

    private List<String> getPlugins() {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.add(plugin.getName());
        }
        return plugins;
    }

    private List<String> getWorlds() {
        ArrayList<String> worlds = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
        return worlds;
    }

    private List<String> getGroups() {
        return new ArrayList<>();
    }

    private int getMaxPlayers() {
        return Bukkit.getOnlinePlayers().size() + 1;
    }

    private int getOnlineCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    private List<EnjinPlayerInfo> getOnlinePlayers() {
        List<EnjinPlayerInfo> a = new ArrayList<>();
        AssiCore.getCore().getModuleManager().getPlayerManager().getLoadedPlayers().forEach((s, assiPlayer) -> a.add(new EnjinPlayerInfo(assiPlayer.getName(),
                AssiCore.getCore().getVanishedPlayers().contains(assiPlayer.getUuid()), assiPlayer.getUuid())));
     // a.add(new EnjinPlayerInfo("Arraying", UUID.fromString("c9dcaad2-ff2a-4d8b-a82b-8fc06102550a")));
      //  a.add(new EnjinPlayerInfo("ExplodingTNT", UUID.fromString("c0e914fd-4eb6-47b7-9806-ee36dce9f608")));
        return a;
    }

    private Map<String, EnjinPlayerGroupInfo> getPlayerGroups() {
        return new HashMap<>();
    }

    private Map<String, List<Object[]>> getVotes() {
        return new HashMap<>();
    }

    private boolean isPermissionsAvailable() {
        return false;
        //nothing to see here
    }

    private String getVersion(){
        return "Assi-1.8-8";
    }

    public RPCData<SyncResponse> sync(final EnjinStatus status) {
        String method = "Plugin.sync";
        Map<String, Object> params = new HashMap<>();
        params.put("authkey", authKey);
        params.put("status", status);
        int id = EnjinRPC.genNum();

        JSONRPC2Session session = null;
        JSONRPC2Request request = null;
        JSONRPC2Response response = null;
        try {
            session = EnjinRPC.getSession("minecraft.php");
            request = new JSONRPC2Request(method, params, id);
            response = session.send(request);

         //   AssiCore.getCore().logI("JSONRPC2 Request: " + request.toJSON().toJSONString());
          //  AssiCore.getCore().logI("JSONRPC2 Response: " + response.toJSON().toJSONString());

            RPCData<SyncResponse> data = Util.getGson().fromJson(response.toJSON().toJSONString(), new TypeToken<RPCData<SyncResponse>>(){}.getType());
            data.setRequest(request);
            data.setResponse(response);
            return data;
        } catch (JSONRPC2SessionException e) {
            e.printStackTrace();
            AssiCore.getCore().logW("[Enjin] Oh no Enjin are on to us! Error...");
            AssiCore.getCore().logW("[Enjin] Failed Request to " + session.getURL().toString() + ": " + request.toJSON().toJSONString());
        }
        return null;
    }

    public RPCData<Boolean> auth(final Integer port, final boolean save) {
        String method = "Plugin.auth";
        Map<String, Object> parameters = new HashMap<>() ;
        parameters.put("authkey", authKey);
        parameters.put("port", port);
        parameters.put("save", save);
        Integer id = EnjinRPC.genNum();

        JSONRPC2Session session = null;
        JSONRPC2Request request = null;
        JSONRPC2Response response = null;
        try {
            session = EnjinRPC.getSession("minecraft.php");
            request = new JSONRPC2Request(method, parameters, id);
            response = session.send(request);

           // AssiCore.getCore().logI("JSONRPC2 Request: " + request.toJSON().toJSONString());
          //  AssiCore.getCore().logI("JSONRPC2 Response: " + response.toJSON().toJSONString());

            RPCData<Boolean> data = EnjinRPC.gson.fromJson(response.toJSON().toJSONString(), new TypeToken<RPCData>() {}.getType());
            data.setRequest(request);
            data.setResponse(response);
            return data;
        } catch (JSONRPC2SessionException e) {
            e.printStackTrace();
            AssiCore.getCore().logW("[Enjin] Failed Request to " + session.getURL().toString() + ": " + request.toJSON().toJSONString());
        }
        return null;
    }


}
