package net.assimilationmc.assicore.donate;

public enum DonationPackage {

    RANK_DEMONIC(3094768, "Demonic"),
    RANK_DEMONIC_LIFE(3094802, "Demonic Life"),
    RANK_INFERNAL(3094780, "Infernal"),
    RANK_INFERNAL_LIFE(3094805, "Infernal Life"),

    XP_2_45(3094842, "x2 UHC XP for a game"),
    BUCKS_2_15(3094818, "x2 Bucks for 15 minutes"),
    BUCKS_2_30(3094824, "x2 Bucks for 30 minutes"),
    UC_2_15(3094834, "x2 Ultra Coins for 15 minutes"),
    UC_2_30(3094839, "x2 Ultra Coins for 30 minutes"),

    POCKET_ULTRA_COINS(3094787, "Pocket of Ultra Coins"),
    POUCH_ULTRA_COINS(3094790, "Pouch of Ultra Coins"),
    BOX_ULTRA_COINS(3094794, "Box of Ultra Coins"),
    VAULT_ULTRA_COINS(3094799, "Vault of Ultra Coins"),

    ;

    private final int id;
    private final String name;

    DonationPackage(int id, String name) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

}
