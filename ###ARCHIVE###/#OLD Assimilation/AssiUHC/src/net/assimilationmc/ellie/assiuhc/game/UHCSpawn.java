package net.assimilationmc.ellie.assiuhc.game;

import net.assimilationmc.ellie.assicore.api.SerializableLocation;

/**
 * Created by Ellie on 10/05/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class UHCSpawn {

    private int id;
    private int groupId;

    private SerializableLocation location;

    public UHCSpawn(SerializableLocation serializableLocation){
        this.location = serializableLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public SerializableLocation getLocation() {
        return location;
    }

    public void setLocation(SerializableLocation location) {
        this.location = location;
    }

}
