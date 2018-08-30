package net.assimilationmc.ellie.assipunish.punish.data;

import net.assimilationmc.ellie.assipunish.punish.Punishment;

import java.util.UUID;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class DataPunishment {

    private int id;
    private UUID uuid;
    private String ip;

    private Punishment type;
    private int offence;
    private long issued;
    private long expire;
    private String punished_by;
    private String custom_reason;

    private String unpunished_by;
    private long unpunished_time;
    private String unpunished_reason;

    public DataPunishment(){
    }

    public DataPunishment(String uuid, Punishment type, int offence, long issued, long expire, String punishedBy){
        setUuid(uuid);
        this.type = type;
        this.offence = offence;
        this.issued = issued;
        this.expire = expire;
        this.punished_by = punishedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(String object) {
        try{
            this.uuid = UUID.fromString(object);
        }catch(IllegalArgumentException e){
            this.ip = object;
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Punishment getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Punishment.valueOf(type.toUpperCase());
    }

    public void setOffence(int offence) {
        this.offence = offence;
    }

    public int getOffence() {
        return offence;
    }

    public long getIssued() {
        return issued;
    }

    public void setIssued(long issued) {
        this.issued = issued;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire.equals("null") ? -1 : Long.valueOf(expire);
    }

    public String getPunishedBy() {
        return punished_by;
    }

    public void setPunished_by(String punishedBy) {
        this.punished_by = punishedBy;
    }

    public String getCustomReason() {
        return custom_reason;
    }

    public void setCustom_reason(String customReason) {
        this.custom_reason = customReason;
    }

    public String getUnpunishedBy() {
        return unpunished_by;
    }

    public void setUnpunished_by(String unpunishedBy) {
        this.unpunished_by = unpunishedBy;
    }

    public long getUnpunishedTime() {
        return unpunished_time;
    }

    public void setUnpunished_time(long unpunishedTime) {
        this.unpunished_time = unpunishedTime;
    }

    public String getUnpunishedReason() {
        return unpunished_reason;
    }

    public void setUnpunished_reason(String unpunished_reason) {
        this.unpunished_reason = unpunished_reason;
    }

}
