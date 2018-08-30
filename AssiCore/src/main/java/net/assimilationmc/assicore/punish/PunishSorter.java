package net.assimilationmc.assicore.punish;

import net.assimilationmc.assicore.punish.model.PunishmentData;

import java.util.Comparator;

public class PunishSorter implements Comparator<PunishmentData> {

    @Override
    public int compare(PunishmentData o1, PunishmentData o2) {
        return Long.compare(o2.getPunishIssued(), o1.getPunishIssued());
    }
}
