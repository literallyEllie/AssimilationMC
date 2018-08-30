package net.assimilationmc.ellie.assicore.task.fakeenjin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinPlayerGroupInfo {

    private String uuid;

    public String toString() {
        return "PlayerGroupInfo(uuid=" + getUuid() + ", worlds=" + getWorlds() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EnjinPlayerGroupInfo)) {
            return false;
        }
        EnjinPlayerGroupInfo other = (EnjinPlayerGroupInfo) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$uuid = getUuid();
        Object other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) {
            return false;
        }
        Object this$worlds = getWorlds();
        Object other$worlds = other.getWorlds();
        return this$worlds == null ? other$worlds == null : this$worlds.equals(other$worlds);
    }

    protected boolean canEqual(Object other) {
        return other instanceof EnjinPlayerGroupInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $uuid = getUuid();
        result = result * 59 + ($uuid == null ? 43 : $uuid.hashCode());
        Object $worlds = getWorlds();
        result = result * 59 + ($worlds == null ? 43 : $worlds.hashCode());
        return result;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Map<String, List<String>> getWorlds() {
        return this.worlds;
    }

    private Map<String, List<String>> worlds = new HashMap<>();

    public EnjinPlayerGroupInfo(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public EnjinPlayerGroupInfo() {
    }

}
