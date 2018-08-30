package net.assimilationmc.assicore.punish.model;

import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.util.UtilTime;

import java.util.UUID;

public class PunishmentData {

    private final int id;
    private final UUID punisher;
    private final UUID punishee;

    private final String punisherDisplay;
    private final PunishmentCategory punishmentCategory;
    private final PunishmentOutcome punishmentType;
    private final int severity;
    private final long punishIssued;
    private final long punishLength;
    private String ip;
    private String customReason;
    private UnpunishData unpunishData;

    /**
     * The request wrapper to represent an IP punishment.
     *
     * @param id                 The punishment ID (relative to the player)
     * @param punisher           the punisher.
     * @param punishee           the person who got punished.
     * @param punisherDisplay    the person who punished them's name.
     * @param punishmentCategory the category of the punishment.
     * @param punishmentType     the outcome of the punishment.
     * @param severity           the severity of the punishment.
     * @param punishIssued       when the punishment was issued (in millis)
     * @param ip                 The IP the punishment covers.
     * @param punishLength       the length of the punishment exactly (in millis)
     * @param unpunishData       the request of the unpunished (invalidates the punishment)
     */
    public PunishmentData(int id, UUID punisher, UUID punishee, String punisherDisplay,
                          PunishmentCategory punishmentCategory, PunishmentOutcome punishmentType, int severity, long punishIssued, String ip,
                          long punishLength, UnpunishData unpunishData) {
        this.id = id;
        this.punisher = punisher;
        this.punishee = punishee;
        this.punishmentCategory = punishmentCategory;
        this.punishmentType = punishmentType;
        this.severity = severity;
        this.punishIssued = punishIssued;
        this.ip = ip;
        this.punisherDisplay = punisherDisplay;
        this.punishLength = punishLength;
        this.unpunishData = unpunishData;
    }

    /**
     * The request wrapper to represent a punishment.
     *
     * @param id                 The punishment ID (relative to the player)
     * @param punisher           the punisher.
     * @param punishee           the person who got punished.
     * @param punisherDisplay    the person who punished them's name.
     * @param punishmentCategory the category of the punishment.
     * @param punishmentType     the outcome of the punishment.
     * @param severity           the severity of the punishment.
     * @param punishIssued       when the punishment was issued (in millis)
     * @param punishLength       the length of the punishment exactly (in millis)
     * @param unpunishData       the request of the unpunished (invalidates the punishment)
     */
    public PunishmentData(int id, UUID punisher, UUID punishee, String punisherDisplay,
                          PunishmentCategory punishmentCategory, PunishmentOutcome punishmentType, int severity, long punishIssued,
                          long punishLength, UnpunishData unpunishData) {
        this.id = id;
        this.punisher = punisher;
        this.punishee = punishee;
        this.punishmentCategory = punishmentCategory;
        this.punishmentType = punishmentType;
        this.severity = severity;
        this.punishIssued = punishIssued;
        this.punisherDisplay = punisherDisplay;
        this.punishLength = punishLength;
        this.unpunishData = unpunishData;
    }

    /**
     * The request wrapper to represent an active punishment - no unpunish request.
     *
     * @param id                 The punishment ID (relative to the player)
     * @param punisher           the punisher.
     * @param punishee           the person who got punished.
     * @param punisherDisplay    the person who punished them's name.
     * @param punishmentCategory the category of the punishment.
     * @param punishmentType     the outcome of the punishment.
     * @param severity           the severity of the punishment
     * @param punishIssued       when the punishment was issued (in millis)
     * @param ip                 The IP the punishment covers.
     * @param punishLength       the length of the punishment exactly (in millis)
     */
    public PunishmentData(int id, UUID punisher, UUID punishee, String punisherDisplay, PunishmentCategory punishmentCategory,
                          PunishmentOutcome punishmentType, int severity, long punishIssued, String ip, long punishLength) {
        this(id, punisher, punishee, punisherDisplay, punishmentCategory, punishmentType, severity, punishIssued, ip, punishLength, null);
    }

    /**
     * The request wrapper to represent an active punishment - no unpunish request.
     *
     * @param id                 The punishment ID (relative to the player)
     * @param punisher           the punisher.
     * @param punishee           the person who got punished.
     * @param punisherDisplay    the person who punished them's name.
     * @param punishmentCategory the category of the punishment.
     * @param punishmentType     the outcome of the punishment.
     * @param severity           the severity of the punishment.
     * @param punishIssued       when the punishment was issued (in millis)
     * @param punishLength       the length of the punishment exactly (in millis)
     */
    public PunishmentData(int id, UUID punisher, UUID punishee, String punisherDisplay, PunishmentCategory punishmentCategory,
                          PunishmentOutcome punishmentType, int severity, long punishIssued, long punishLength) {
        this(id, punisher, punishee, punisherDisplay, punishmentCategory, punishmentType, severity, punishIssued, punishLength, null);
    }

    /**
     * Parse a punishment from a serialized string.
     *
     * @param serializedData the serialized punishment request.
     */
    public PunishmentData(String serializedData) {
        String[] args = serializedData.split(RedisPubSubMessage.PARAM_SEPARATOR);

        this.id = Integer.parseInt(args[0]);
        this.punisher = UUID.fromString(args[1]);
        this.punishee = UUID.fromString(args[2]);
        this.punisherDisplay = args[3];
        this.punishmentCategory = PunishmentCategory.valueOf(args[4]);
        this.punishmentType = PunishmentOutcome.valueOf(args[5]);
        this.severity = Integer.parseInt(args[6]);
        this.punishIssued = Long.parseLong(args[7]);
        this.ip = args[8].equals("null") ? args[8] : null;
        this.customReason = args[9];
        this.punishLength = Long.parseLong(args[10]);

        if (args.length > 11) {
            String unpunishDisplay = args[11];
            long time = Long.parseLong(args[12]);
            String reason = args[13];
            this.unpunishData = new UnpunishData(unpunishDisplay, time, reason);
        }

    }

    /**
     * @return the id of the punishment, relative to the player and how many punishments they have received.
     */
    public int getId() {
        return id;
    }

    /**
     * @return the uuid of the punisher.
     */
    public UUID getPunisher() {
        return punisher;
    }

    /**
     * @return the display name of the punisher.
     */
    public String getPunisherDisplay() {
        return punisherDisplay;
    }

    /**
     * @return the uuid of the person who was punished.
     */
    public UUID getPunishee() {
        return punishee;
    }

    /**
     * @return the punishment category.
     */
    public PunishmentCategory getPunishmentCategory() {
        return punishmentCategory;
    }

    /**
     * @return the outcome of the punishment.
     */
    public PunishmentOutcome getPunishmentType() {
        return punishmentType;
    }

    /**
     * @return the severity of the punishment.
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * @return a timestamp to present when the punishment was issued in millis.
     */
    public long getPunishIssued() {
        return punishIssued;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isIPBan() {
        return ip != null;
    }

    /**
     * @return the reason, if the custom reason isn't null, that will be supplied, else the base reason to the punishmentCategory.
     * See {@link PunishmentCategory#getBaseReason()}
     */
    public String getReason() {
        return customReason == null ? punishmentCategory.getBaseReason() : customReason;
    }

    /**
     * @return the custom reason of the punishment (may be null)
     */
    public String getCustomReason() {
        return customReason;
    }

    /**
     * Set the custom reason to the punishment (may be null)
     *
     * @param customReason the custom reason.
     */
    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

    /**
     * @return the length of the punishment or:
     * -1 if it is inapplicable to this punishment.
     * -2 if it is a permanent punishment.
     */
    public long getPunishLength() {
        if (punishLength == PunishmentCategory.TIME_PERM || punishLength == PunishmentCategory.TIME_INAPPLICABLE)
            return punishLength;
        return Math.abs(punishLength);
    }

    /**
     * @return Evaluates if a punishment is expired.
     */
    public boolean expired() {
        return isInvalid() || punishLength == PunishmentCategory.TIME_INAPPLICABLE || (!isPerm() && UtilTime.elapsed(punishIssued, punishLength));
    }

    /**
     * @return calculates the punishment expiry or:
     * -1 if it is inapplicable to this punishment.
     * -2 if it is a permanent punishment.
     */
    public long getPunishExpiry() {
        if (punishLength == PunishmentCategory.TIME_PERM || punishLength == PunishmentCategory.TIME_INAPPLICABLE)
            return punishLength;
        return punishIssued + punishLength;
    }

    /**
     * @return evaluates if the punishment is invalid, basically does the punishment have unpunish request attached to it or not.
     */
    public boolean isInvalid() {
        return unpunishData != null;
    }

    /**
     * @return evaluates if the punishment is permanent. I.e is the punishLength == to {@link PunishmentCategory#TIME_PERM}
     */
    public boolean isPerm() {
        return punishLength == PunishmentCategory.TIME_PERM;
    }

    /**
     * @return the unpunish request to the punishment (may be null)
     */
    public UnpunishData getUnpunishData() {
        return unpunishData;
    }

    /**
     * Set the punishment request for this punishment and effectively expires it.
     *
     * @param unpunishData the unpunishment request.
     */
    public void setUnpunishData(UnpunishData unpunishData) {
        this.unpunishData = unpunishData;
    }

    // 10 args for active, 14 for inactive
    @Override
    public String toString() {
        return id + RedisPubSubMessage.PARAM_SEPARATOR + punisher + RedisPubSubMessage.PARAM_SEPARATOR + punishee + RedisPubSubMessage.PARAM_SEPARATOR +
                punisherDisplay + RedisPubSubMessage.PARAM_SEPARATOR + punishmentCategory + RedisPubSubMessage.PARAM_SEPARATOR + punishmentType.name() +
                RedisPubSubMessage.PARAM_SEPARATOR + severity + RedisPubSubMessage.PARAM_SEPARATOR + punishIssued + RedisPubSubMessage.PARAM_SEPARATOR +
                ip + RedisPubSubMessage.PARAM_SEPARATOR + getReason() + RedisPubSubMessage.PARAM_SEPARATOR + punishLength +
                (unpunishData != null ? RedisPubSubMessage.PARAM_SEPARATOR + unpunishData.getUnpunisherDisplay() + RedisPubSubMessage.PARAM_SEPARATOR +
                        unpunishData.getUnpunishTime() + RedisPubSubMessage.PARAM_SEPARATOR + unpunishData.getUnpunishReason() : "");
    }

}
