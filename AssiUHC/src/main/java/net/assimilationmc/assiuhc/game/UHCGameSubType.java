package net.assimilationmc.assiuhc.game;

public enum UHCGameSubType {

    TEAMED_CLASSIC(UHCGameType.TEAMED, "Classic UHC"),
    TEAMED_BLIND(UHCGameType.TEAMED, "Blind Teams"),
    TEAMED_SCATTER(UHCGameType.TEAMED, "Scatter"),
    TEAMED_DEATHMATCH(UHCGameType.TEAMED, "Teamed Death-Match"),

    SINGLES_CLASSIC(UHCGameType.SINGLES, "Classic UHC"),
    SINGLES_DEATHMATCH(UHCGameType.SINGLES, "Death-match"),
    SINGLES_SKY(UHCGameType.SINGLES, "SkyUHC"),
    SINGLES_OP(UHCGameType.SINGLES, "OP-UHC"),
    SINGLES_RANKED(UHCGameType.SINGLES, "Ranked (Competitive)"),

    TEST_SINGLES(UHCGameType.SINGLES, "single test"),
    TEST_TEAMED(UHCGameType.TEAMED, "team test");

    private UHCGameType type;
    private String display;

    UHCGameSubType(UHCGameType parent, String display) {
        this.type = parent;
        this.display = display;
    }

    public UHCGameType getType() {
        return type;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isTeamed() {
        return type == UHCGameType.TEAMED;
    }

    public boolean isSingle() {
        return type == UHCGameType.SINGLES;
    }

}
