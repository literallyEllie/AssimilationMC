package net.assimilationmc.assicore.joinitems;

import net.assimilationmc.assicore.player.AssiPlayer;

public interface ItemGiveCondition {

    boolean onJoin(AssiPlayer player);

}
