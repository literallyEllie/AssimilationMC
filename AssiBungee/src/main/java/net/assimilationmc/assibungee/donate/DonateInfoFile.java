package net.assimilationmc.assibungee.donate;

import com.google.gson.annotations.SerializedName;

public class DonateInfoFile {

    @SerializedName("last_giveaway")
    private long lastGiveaway;

    public long getLastGiveaway() {
        return lastGiveaway;
    }

    public void setLastGiveaway(long lastGiveaway) {
        this.lastGiveaway = lastGiveaway;
    }

}
