package net.assimilationmc.gameapi.game;

public class AssiGameMeta {

    private final String id;
    private String display, subType, description;

    public AssiGameMeta(String id, String display, String description, String subType) {
        this.id = id;
        this.display = display;
        this.description = description;
        this.subType = subType;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
