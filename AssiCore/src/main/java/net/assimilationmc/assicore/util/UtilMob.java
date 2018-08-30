package net.assimilationmc.assicore.util;

import org.bukkit.entity.EntityType;

public class UtilMob {

    public static boolean isAggressive(EntityType entityType) {
        return entityType == EntityType.CREEPER || entityType == EntityType.SKELETON
                || entityType == EntityType.SPIDER || entityType == EntityType.GIANT
                || entityType == EntityType.ZOMBIE || entityType == EntityType.SLIME
                || entityType == EntityType.PIG_ZOMBIE || entityType == EntityType.ENDERMAN
                || entityType == EntityType.GHAST || entityType == EntityType.SILVERFISH
                || entityType == EntityType.CAVE_SPIDER || entityType == EntityType.BLAZE
                || entityType ==  EntityType.MAGMA_CUBE || entityType == EntityType.ENDER_DRAGON
                || entityType == EntityType.WITHER || entityType == EntityType.ENDERMITE
                || entityType == EntityType.GUARDIAN || entityType == EntityType.WITCH;

    }

}
