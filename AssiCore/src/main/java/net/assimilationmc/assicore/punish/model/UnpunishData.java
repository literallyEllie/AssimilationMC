package net.assimilationmc.assicore.punish.model;

public class UnpunishData {

    private String unpunisherDisplay;
    private long unpunishTime;
    private String unpunishReason;

    /**
     * Data representing the unpunishment request of a punishment.
     *
     * @param unpunisherDisplay The display name of the unpunishmer.
     * @param unpunishTime      the time representing when the unpunish event happened.
     * @param unpunishReason    the reason the player was unpunished.
     */
    public UnpunishData(String unpunisherDisplay, long unpunishTime, String unpunishReason) {
        this.unpunisherDisplay = unpunisherDisplay;
        this.unpunishTime = unpunishTime;
        this.unpunishReason = unpunishReason;
    }

    /**
     * @return the display name of the person who unpunished them.
     */
    public String getUnpunisherDisplay() {
        return unpunisherDisplay;
    }

    /**
     * @return the time that the unpunish event happened.
     */
    public long getUnpunishTime() {
        return unpunishTime;
    }

    /**
     * @return the reason specified they were unpunished.
     */
    public String getUnpunishReason() {
        return unpunishReason;
    }

}
