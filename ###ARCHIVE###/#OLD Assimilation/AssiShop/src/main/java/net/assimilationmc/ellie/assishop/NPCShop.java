package net.assimilationmc.ellie.assishop;

import net.assimilationmc.ellie.assicore.api.SerializableLocation;
import org.bukkit.entity.Villager;

/**
 * Created by Ellie on 13.7.17 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class NPCShop {

    private int id;
    private String display;
    private SerializableLocation location;
    private String action;

    private boolean edit;
    private String method;

    private Villager entity;

    public NPCShop(int id, String display, SerializableLocation location, String action){
        this.id = id;
        this.display = display;
        this.location = location;
        this.action = action;
    }

    public NPCShop () {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public SerializableLocation getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = new SerializableLocation(location);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit, String method) {
        this.edit = edit;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public Villager getEntity() {
        return entity;
    }

    public void setEntity(Villager entity) {
        this.entity = entity;
    }

}
