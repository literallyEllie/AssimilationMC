package net.assimilationmc.assicore.booster;

public class ActiveBooster {

    private String starter;
    private Booster booster;
    private long start;

    public ActiveBooster(String starter, Booster booster) {
        this.starter = starter;
        this.booster = booster;
        this.start = System.currentTimeMillis();
    }

    public String getStarter() {
        return starter;
    }

    public Booster getBooster() {
        return booster;
    }

    public long getStart() {
        return start;
    }

    public long getRemaining() {
        return start + booster.getLength();
    }

}
