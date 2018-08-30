package net.assimilationmc.uhclobbyadaptor.stats.comp;

import java.util.concurrent.TimeUnit;

public enum CompCooldowns {

    FIRST(TimeUnit.MINUTES.toMillis(30)),
    SECOND(TimeUnit.MINUTES.toMillis(45)),
    THIRD(TimeUnit.HOURS.toMillis(1)),
    FORTH(TimeUnit.MINUTES.toMillis(90)),
    FIFTH(TimeUnit.HOURS.toMillis(3)),
    SIXTH(TimeUnit.HOURS.toMillis(12)),
    SEVENTH(TimeUnit.DAYS.toMillis(2)),
    EIGHT(TimeUnit.DAYS.toMillis(3)),
    NINTH(TimeUnit.HOURS.toMillis((3 * 24) + 12)),
    TENTH(TimeUnit.DAYS.toMillis(7)),
    ELEVENTH(TimeUnit.DAYS.toMillis(31));

    private final long length;

    CompCooldowns(long length) {
        this.length = length;
    }

    public static CompCooldowns getNextCooldown(CompCooldowns compCooldowns) {
        for (CompCooldowns cooldowns : values()) {
            if (compCooldowns.ordinal() + 1 == cooldowns.ordinal())
                return compCooldowns;
        }
        return FIRST;
    }

    public long getLength() {
        return length;
    }

}
