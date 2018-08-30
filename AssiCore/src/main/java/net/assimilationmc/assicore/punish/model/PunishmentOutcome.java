package net.assimilationmc.assicore.punish.model;

public enum PunishmentOutcome {

    WARN, KICK, TEMP_MUTE, PERM_MUTE, TEMP_BAN, PERM_BAN,

    IP_BAN_TEMP, IP_BAN_PERM,

    UHC_TEAM_BLACKLIST;

    /**
     * @return is the punish outcome a mute?
     */
    public boolean isMute() {
        return this == TEMP_MUTE || this == PERM_MUTE;
    }

    /**
     * @return is the punish outcome a ban?
     */
    public boolean isBan() {
        return this == TEMP_BAN || this == PERM_BAN
                || this == IP_BAN_TEMP || this == IP_BAN_PERM;
    }

    public boolean isPerm() {
        return this == PERM_MUTE || this == PERM_BAN || this == IP_BAN_PERM;
    }

    /**
     * @return is the punish a ip ban?
     */
    public boolean isIPPunish() {
        return this == IP_BAN_TEMP || this == IP_BAN_PERM;
    }

    @Override
    public String toString() {
        switch (this) {
            case WARN:
                return "warned";
            case KICK:
                return "kicked";
            case TEMP_MUTE:
                return "temp-muted";
            case PERM_MUTE:
                return "muted";
            case TEMP_BAN:
                return "temp-banned";
            case PERM_BAN:
                return "banned";
            case IP_BAN_TEMP:
                return "temp-ip-banned";
            case IP_BAN_PERM:
                return "ip-banned";
            case UHC_TEAM_BLACKLIST:
                return "uhc-team-blacklisted";
            default:
                return "unrecognized action";
        }
    }

}
