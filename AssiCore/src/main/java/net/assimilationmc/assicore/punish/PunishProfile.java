package net.assimilationmc.assicore.punish;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.model.PunishmentCategory;
import net.assimilationmc.assicore.punish.model.PunishmentData;
import net.assimilationmc.assicore.util.UtilTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PunishProfile {

    private final UUID uuid;
    private String name;

    private Map<PunishmentCategory, List<PunishmentData>> activePunishments;
    private Map<PunishmentCategory, List<PunishmentData>> oldPunishments;

    private int punishmentCounter;

    /**
     * A holder for all punishments to a player.
     *
     * @param uuid the uuid it will hold request for.
     */
    public PunishProfile(UUID uuid) {
        this.uuid = uuid;
        activePunishments = Maps.newHashMap();
        oldPunishments = Maps.newHashMap();
        punishmentCounter = 0;
    }

    /**
     * Add a new punishment internally, THIS WILL NOT HAVE IMMEDIATE EFFECT.
     * If you wish to punish a player please use {@link PunishmentManager#punish(AssiPlayer, AssiPlayer, PunishmentCategory, String)}
     *
     * @param category       the punishment category.
     * @param punishmentData The punishment request.
     */
    public void addPunishment(PunishmentCategory category, PunishmentData punishmentData) {
        if (!activePunishments.containsKey(category))
            activePunishments.put(category, Lists.newArrayList());
        activePunishments.get(category).add(punishmentData);
        punishmentCounter++;
    }

    /**
     * @return an effective ban that could prevent them from joining, or null if there is none.
     */
    public PunishmentData getEffectiveBan() {
        for (List<PunishmentData> punishmentDataList : activePunishments.values()) {
            for (PunishmentData punishmentData : punishmentDataList) {
                if (punishmentData.getPunishmentType().isBan() && !punishmentData.expired()) {
                    return punishmentData;
                }
            }
        }
        return null;
    }

    /**
     * @return an effective mute that could prevent them from joining, or null if there is none.
     */
    public PunishmentData getEffectiveMute() {
        for (List<PunishmentData> punishmentDataList : activePunishments.values()) {
            for (PunishmentData punishmentData : punishmentDataList) {
                if (punishmentData.getPunishmentType().isMute() && !punishmentData.expired()) {
                    return punishmentData;
                }
            }
        }
        return null;
    }

    /**
     * Calculates the next punishment severity for a category.
     *
     * @param punishmentCategory The punishment category.
     * @return The next severity level of the next punishment that could be carried out.
     */
    public int nextPunishmentSeverity(PunishmentCategory punishmentCategory) {
        int violations = (activePunishments.containsKey(punishmentCategory) ? activePunishments.get(punishmentCategory).size() : 0);

        if (oldPunishments.containsKey(punishmentCategory)) {
            // ignore punishments if given more than 30 days ago
            long ignore = TimeUnit.DAYS.toMillis(30);

            for (PunishmentData data : oldPunishments.get(punishmentCategory)) {
                if (UtilTime.elapsed(data.getPunishIssued(), ignore)) continue;
                violations++;
            }
        }

        return Math.min(violations + 1, punishmentCategory.getMaxOffenses());
    }

    /**
     * Add an old punishment internally.
     *
     * @param transfer       is the punishment a transfer from a current punishment? (Effects the counter)
     * @param category       the category of the punishment.
     * @param punishmentData The punishment request to add.
     */
    public void addOldPunishment(boolean transfer, PunishmentCategory category, PunishmentData punishmentData) {
        if (!oldPunishments.containsKey(category))
            oldPunishments.put(category, Lists.newLinkedList());
        oldPunishments.get(category).add(punishmentData);
        if (!transfer) punishmentCounter++;
    }

    /**
     * @return the next punishment id.
     */
    public int getPunishmentCounter() {
        return punishmentCounter;
    }

    /**
     * Get a punishment by an ID, searches all their active punishments and old punishments.
     *
     * @param id the id to find.
     * @return the punishment that is assigned to that id, or null if there isn't one.
     */
    public PunishmentData getPunishmentId(int id) {
        for (List<PunishmentData> punishmentDataList : activePunishments.values()) {
            for (PunishmentData data : punishmentDataList) {
                if (data.getId() == id)
                    return data;
            }
        }

        for (List<PunishmentData> punishmentDataList : oldPunishments.values()) {
            for (PunishmentData data : punishmentDataList) {
                if (data.getId() == id)
                    return data;
            }
        }
        return null;
    }

    /**
     * @return the name of player that the profile this is assigned to.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the punish profile.
     *
     * @param name The name of the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the UUID of the punishment profile.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return an unmodifiable map of all their old and expired punishments.
     */
    public Map<PunishmentCategory, List<PunishmentData>> getOldPunishments() {
        return Collections.unmodifiableMap(oldPunishments);
    }

    /**
     * @return an unmodifiable map of all their active punishments.
     */
    public Map<PunishmentCategory, List<PunishmentData>> getActivePunishments() {
        return activePunishments;
    }

}
