package net.assimilationmc.assicore.patch;

import com.google.common.collect.Sets;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.patch.uuidpool.PatchUUIDPool;

import java.util.Set;

public class PatchManager extends Module {

    private Set<AssiPatch> patchSet;

    public PatchManager(AssiPlugin plugin) {
        super(plugin, "Patch Manager");
    }

    @Override
    protected void start() {
        this.patchSet = Sets.newHashSet();

        patchSet.add(new PatchPayload(getPlugin()));
        patchSet.add(new PatchUUIDPool(getPlugin()));
        patchSet.add(new PatchWDL(getPlugin()));
        patchSet.add(new PatchMinimap(getPlugin()));

        patchSet.forEach(AssiPatch::load);
    }

    @Override
    protected void end() {
        patchSet.forEach(AssiPatch::unregister);
        patchSet.clear();
    }

    public Set<AssiPatch> getPatchSet() {
        return patchSet;
    }

}
