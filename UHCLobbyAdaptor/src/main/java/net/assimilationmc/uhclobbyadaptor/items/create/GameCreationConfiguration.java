package net.assimilationmc.uhclobbyadaptor.items.create;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import net.assimilationmc.assicore.web.WebAPIManager;
import net.assimilationmc.assicore.web.request.JSONRequestBuilder;
import net.assimilationmc.assicore.web.request.RequestMethod;
import net.assimilationmc.assicore.web.request.WebEndpoint;
import net.assimilationmc.uhclobbyadaptor.lib.UHCGameSubType;
import org.bukkit.entity.Player;

import java.util.Map;

public class GameCreationConfiguration {

    private final Player creator;
    private UHCGameSubType gameSubType;
    private String map;

    private Map<String, Object> customProperties;

    public GameCreationConfiguration(Player creator) {
        this.creator = creator;
        this.customProperties = Maps.newHashMap();
    }

    public Player getCreator() {
        return creator;
    }

    public UHCGameSubType getGameSubType() {
        return gameSubType;
    }

    public void setGameSubType(UHCGameSubType gameSubType) {
        this.gameSubType = gameSubType;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void addProperty(String id, Object key) {
        customProperties.put(id, key);
    }

    public JSONRequestBuilder prepare(WebAPIManager webAPIManager) {
        return webAPIManager.defaultBuilder().setEndpoint(WebEndpoint.SERVER).setMethod(RequestMethod.CREATE_SERVER)
                .addParameter("type", "uhc/" + gameSubType.name() + "/" + map).addParameter("custom", customProperties);
    }

}
