package net.assimilationmc.ellie.assicore.task.fakeenjin;

import java.util.UUID;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinPlayerInfo {

    private String name;
    private Boolean vanish;
    private String uuid;

    public String toString() {
        return "PlayerInfo(name=" + getName() + ", vanish=" + getVanish() + ", uuid=" + getUuid() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EnjinPlayerInfo)) {
            return false;
        }
        EnjinPlayerInfo other = (EnjinPlayerInfo) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$name = getName();
        Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Object this$vanish = getVanish();
        Object other$vanish = other.getVanish();
        if (this$vanish == null ? other$vanish != null : !this$vanish.equals(other$vanish)) {
            return false;
        }
        Object this$uuid = getUuid();
        Object other$uuid = other.getUuid();
        return this$uuid == null ? other$uuid == null : this$uuid.equals(other$uuid);
    }

    protected boolean canEqual(Object other) {
        return other instanceof EnjinPlayerInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $name = getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $vanish = getVanish();
        result = result * 59 + ($vanish == null ? 43 : $vanish.hashCode());
        Object $uuid = getUuid();
        result = result * 59 + ($uuid == null ? 43 : $uuid.hashCode());
        return result;
    }

    public String getName() {
        return this.name;
    }

    public Boolean getVanish() {
        return this.vanish;
    }

    public String getUuid() {
        return this.uuid;
    }

    public EnjinPlayerInfo(String name, Boolean vanished, UUID uuid) {
        this.name = name;
        this.vanish = vanished;
        this.uuid = uuid.toString().replace("-", "");
    }

    public EnjinPlayerInfo(String name, UUID uuid) {
        this(name, false, uuid);
    }

    public EnjinPlayerInfo() {
    }
}