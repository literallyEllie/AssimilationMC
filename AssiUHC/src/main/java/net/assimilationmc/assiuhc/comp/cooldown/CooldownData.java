package net.assimilationmc.assiuhc.comp.cooldown;

import com.mysql.jdbc.StringUtils;
import net.assimilationmc.assicore.util.UtilTime;

public class CooldownData {

    private CompCooldowns compCooldowns;
    private long issued;

    public CooldownData() {
    }

    public CooldownData(String data) {
        if (StringUtils.isNullOrEmpty(data)) {
            return;
        }
        final String[] split = data.split(";#;");

        this.compCooldowns = CompCooldowns.valueOf(split[0]);
        this.issued = Long.valueOf(split[1]);
    }

    public CompCooldowns getCompCooldowns() {
        return compCooldowns;
    }

    public void setCompCooldowns(CompCooldowns compCooldowns) {
        this.compCooldowns = compCooldowns;
    }

    public long getIssued() {
        return issued;
    }

    public void setIssued(long issued) {
        this.issued = issued;
    }

    public boolean isActive() {
        return compCooldowns != null && !UtilTime.elapsed(issued, compCooldowns.getLength());
    }

    public long getRemaining() {
        return issued + compCooldowns.getLength();
    }

    @Override
    public String toString() {
        return compCooldowns == null ? "" : compCooldowns.name() + ";#;" + issued;
    }

}
