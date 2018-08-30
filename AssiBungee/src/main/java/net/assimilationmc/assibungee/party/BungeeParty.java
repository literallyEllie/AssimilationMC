package net.assimilationmc.assibungee.party;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

public class BungeeParty {

    private UUID leader;
    private Set<UUID> members;
    private String target;

    public BungeeParty(UUID leader) {
        this.leader = leader;
        this.members = Sets.newHashSet();
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean includes(UUID uuid) {
        return leader.equals(uuid) || members.contains(uuid);
    }

    @Override
    public String toString() {
        return leader.toString();
    }

}
