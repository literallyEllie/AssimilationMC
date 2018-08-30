package net.assimilationmc.assicore.cosmetic.cosmetics.particle;

import net.assimilationmc.assicore.cosmetic.Cosmetic;
import net.assimilationmc.assicore.cosmetic.CosmeticType;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.UtilMath;
import org.bukkit.Location;

public class CosmeticLove extends Cosmetic {

    public CosmeticLove() {
        super(CosmeticType.LOVE, "Show off your love for AssimilationMC!", Rank.DEMONIC);
    }

    @Override
    public void tick(Location center) {
        final Location[] locations = UtilMath.generateCircmerfence(center, 10);

    }

}
