package net.assimilationmc.assicore.cosmetic;

import com.google.common.collect.Lists;
import net.assimilationmc.assicore.event.update.UpdateEvent;
import net.assimilationmc.assicore.event.update.UpdateType;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.util.C;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class Cosmetic {

    private String name, description;
    private CosmeticType type;
    private Rank rank;
    private int priceBucks, priceUC;
    private List<Class> conflictsWith;

    public Cosmetic(CosmeticType type, String description) {
        this.name = type.getPrettyName();
        this.description = description;
        this.type = type;
        this.rank = Rank.PLAYER;
        this.conflictsWith = Lists.newArrayList();
    }

    public Cosmetic(CosmeticType type, String description, Rank rank) {
        this(type, description);
        this.rank = rank;
    }

    public Cosmetic(String description, CosmeticType cosmeticType, int priceBucks, int priceUC) {
        this(cosmeticType, description);
        this.priceBucks = priceBucks;
        this.priceUC = priceUC;
    }

    public void tick(Location center) {
    }

    public void apply(Player player) {
    }

    public void remove(Player player) {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CosmeticType getType() {
        return type;
    }

    public Rank getRank() {
        return rank;
    }

    public int getPriceBucks() {
        return priceBucks;
    }

    public int getPriceUC() {
        return priceUC;
    }

    protected void addConflictions(Class... classes) {
        conflictsWith.addAll(Arrays.asList(classes));
    }

    public boolean conflictsWith(Class clazz) {
        return conflictsWith.contains(clazz);
    }

    public String formatPrice() {
        if (!rank.isDefault()) {
            return C.II + "Required rank: " + rank.getPrefix();
        }
        if (priceBucks == 0 && priceUC == 0) {
            return null;
        }
        String priceString = C.II + "This costs ";

        // UC
        if (priceBucks == 0) {
            priceString += C.UC + priceUC + " UC";
        } else {
            priceString += C.BUCKS + priceBucks + " Bucks";
        }

        return priceString;
    }

}
