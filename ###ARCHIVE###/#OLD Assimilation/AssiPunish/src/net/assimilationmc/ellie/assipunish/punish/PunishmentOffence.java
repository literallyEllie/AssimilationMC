package net.assimilationmc.ellie.assipunish.punish;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public enum PunishmentOffence {

    CLIENT_FIRST(Punishment.CLIENT, 0, PunishmentResult.BAN, "7d", "1st Offence: ", "assipunish.client.1"),
    CLIENT_SECOND(Punishment.CLIENT, 1, PunishmentResult.BAN, "14d", "2nd Offence: ", "assipunish.client.2"),
    CLIENT_THIRD(Punishment.CLIENT, 2, PunishmentResult.BAN, "14d", "3rd Offence: ", "assipunish.client.3"),
    CLIENT_FORTH(Punishment.CLIENT, 3, PunishmentResult.BAN, "PERM", "Final Offence: ", "assipunish.client.4"),

    ADVERTISING_FIRST(Punishment.ADVERTISING, 0, PunishmentResult.WARN, "-1", "1st Offence: ", "assipunish.chat.1"),
    ADVERTISING_SECOND(Punishment.ADVERTISING, 1, PunishmentResult.MUTE, "10m", "2nd Offence: ", "assipunish.chat.2"),
    ADVERTISING_THIRD(Punishment.ADVERTISING, 2, PunishmentResult.MUTE, "1hr", "3rd Offence: ", "assipunish.chat.3"),
    ADVERTISING_FORTH(Punishment.ADVERTISING, 3, PunishmentResult.MUTE, "1d", "4th Offence: ", "assipunish.chat.4"),
    ADVERTISING_FIFTH(Punishment.ADVERTISING, 4, PunishmentResult.MUTE, "7d", "5th Offence: ", "assipunish.chat.5"),
    ADVERTISING_SIXTH(Punishment.ADVERTISING, 5, PunishmentResult.MUTE, "30d", "6th Offence: ", "assipunish.chat.6"),
    ADVERTISING_SEVENTH(Punishment.ADVERTISING, 6, PunishmentResult.MUTE, "PERM", "Final Offence: ", "assipunish.chat.7"),

    SPAMMING_FIRST(Punishment.SPAMMING, 0, PunishmentResult.WARN, "-1", "1st Offence: ", "assipunish.chat.1"),
    SPAMMING_SECOND(Punishment.SPAMMING, 1, PunishmentResult.MUTE, "10m", "2nd Offence: ", "assipunish.chat.2"),
    SPAMMING_THIRD(Punishment.SPAMMING, 2, PunishmentResult.MUTE, "1hr", "3rd Offence: ", "assipunish.chat.3"),
    SPAMMING_FORTH(Punishment.SPAMMING, 3, PunishmentResult.MUTE, "1d", "4th Offence: ", "assipunish.chat.4"),
    SPAMMING_FIFTH(Punishment.SPAMMING, 4, PunishmentResult.MUTE, "7d", "5th Offence: ", "assipunish.chat.5"),
    SPAMMING_SIXTH(Punishment.SPAMMING, 5, PunishmentResult.MUTE, "30d", "6th Offence: ", "assipunish.chat.6"),
    SPAMMING_SEVENTH(Punishment.SPAMMING, 6, PunishmentResult.MUTE, "PERM", "Final Offence: ", "assipunish.chat.7"),

    TOXIC_FIRST(Punishment.TOXICITY, 0, PunishmentResult.WARN, "-1", "1st Offence: ", "assipunish.chat.1"),
    TOXIC_SECOND(Punishment.TOXICITY, 1, PunishmentResult.MUTE, "10m", "2nd Offence: ", "assipunish.chat.2"),
    TOXIC_THIRD(Punishment.TOXICITY, 2, PunishmentResult.MUTE, "1hr", "3rd Offence: ", "assipunish.chat.3"),
    TOXIC_FORTH(Punishment.TOXICITY, 3, PunishmentResult.MUTE, "1d", "4th Offence: ", "assipunish.chat.4"),
    TOXIC_FIFTH(Punishment.TOXICITY, 4, PunishmentResult.MUTE, "7d", "5th Offence: ", "assipunish.chat.5"),
    TOXIC_SIXTH(Punishment.ADVERTISING, 5, PunishmentResult.MUTE, "30d", "6th Offence: ", "assipunish.chat.6"),
    TOXIC_SEVENTH(Punishment.TOXICITY, 6, PunishmentResult.MUTE, "PERM", "Final Offence: ", "assipunish.chat.7"),

    TEAM_FIRST(Punishment.TEAM_NAME, 0, PunishmentResult.WARN, "-1", "1st Offence: ", "assipunish.team.1"),
    TEAM_SECOND(Punishment.TEAM_NAME, 1, PunishmentResult.BLACKLIST, "7d", "2nd Offence: ", "assipunish.team.2"),
    TEAM_THIRD(Punishment.TEAM_NAME, 2, PunishmentResult.BAN, "10d", "3rd Offence: ", "assipunish.team.3"),
    TEAM_FORTH(Punishment.TEAM_NAME, 3, PunishmentResult.BAN, "PERM", "Final Offence: ", "assipunish.team.4"),

    EVADE_FIRST(Punishment.BAN_EVADE, 0, PunishmentResult.BAN_AND_IP, "14d", "1st Offence: Evading a punishment", "assipunish.evade.1"),
    EVADE_SECOND(Punishment.BAN_EVADE, 1, PunishmentResult.BAN_AND_IP, "PERM", "Final Offence: Evading a punishment", "assipunish.evade.2"),

    CHARGEBACK_FIRST(Punishment.CHARGEBACK, 0, PunishmentResult.BAN, "PERM", "Final Offence: Chargeback from a payment", "assipunish.chargeback.1"),

    THREATENING_FIRST(Punishment.THREATENING, 0, PunishmentResult.BAN, "7d", "1st Offence: Evading a punishment", "assipunish.threatening.1"),
    THREATENING_SECOND(Punishment.THREATENING, 1, PunishmentResult.BAN, "PERM", "Final Offence: Evading a punishment", "assipunish.threatening.2"),

    MALICIOUS_INTENT_FIRST(Punishment.MALICIOUS_INTENT, 0, PunishmentResult.BAN_ALL, "7d", "Final Offence: Evading a punishment", "assipunish.malicious.1"),

    ;

    private Punishment superPunish;
    private int offence;
    private PunishmentResult punishmentResult;
    private String time;
    private String reason;
    private String permission;

    PunishmentOffence(Punishment punishment, int offence, PunishmentResult result,
                      String time, String reason, String permission){
        this.superPunish = punishment;
        this.offence = offence;
        this.punishmentResult = result;
        this.time = time;
        this.reason = reason;
        this.permission = permission;
    }

    public Punishment getSuperPunish() {
        return superPunish;
    }

    public int getOffence() {
        return offence;
    }

    public PunishmentResult getPunishmentResult() {
        return punishmentResult;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }

    public String getPermission() {
        return permission;
    }

    public static PunishmentOffence getNextPunishment(Punishment punishment, int previous){
        for (PunishmentOffence punishmentOffence : values()) {
            if(punishmentOffence.getSuperPunish().equals(punishment) && punishmentOffence.getOffence() == previous+1){
                return punishmentOffence;
            }
        }
        return null;
    }

    public static PunishmentOffence getMaxPunishment(Punishment punishment){
        PunishmentOffence last = getPunishInfo(punishment, 0);
        int lastOf = 0;
        for (PunishmentOffence punishmentOffence : values()) {
            if(punishmentOffence.getSuperPunish().equals(punishment) && punishmentOffence.getOffence() > lastOf){
                last = punishmentOffence;
                lastOf = punishmentOffence.getOffence();
            }
        }
        return last;
    }
    
    public static PunishmentOffence getPunishInfo(Punishment punishment, int offence){
        for (PunishmentOffence punishmentOffence : values()) {
            if(punishmentOffence.getSuperPunish().equals(punishment) && punishmentOffence.getOffence() == offence) {
                return punishmentOffence;
            }
        }
        return null;
    }
    
}


