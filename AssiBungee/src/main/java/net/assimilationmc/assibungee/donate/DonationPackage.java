package net.assimilationmc.assibungee.donate;

public enum DonationPackage {

    RANK_DEMONIC(3094768),
    RANK_DEMONIC_LIFE(3094802),
    RANK_INFERNAL(3094780),
    RANK_INFERNAL_LIFE(3094805),

    XP_2_45(3094842),
    BUCKS_2_15(3094818),
    BUCKS_2_30(3094824),
    UC_2_15(3094834),
    UC_2_30(3094839),

    POCKET_ULTRA_COINS(3094787),
    POUCH_ULTRA_COINS(3094790),
    BOX_ULTRA_COINS(3094794),
    VAULT_ULTRA_COINS(3094799),

    ;

    private final int id;

    DonationPackage(int id) {
        this.id = id;
    }

    public static DonationPackage fromId(int id) {
        for (DonationPackage donationPackage : values()) {
            if (donationPackage.getId() == id)
                return donationPackage;
        }
        return null;
    }

    public int getId() {
        return id;
    }

}
