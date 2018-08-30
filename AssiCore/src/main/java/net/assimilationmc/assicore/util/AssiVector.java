package net.assimilationmc.assicore.util;

public class AssiVector {

    private int x;
    private int y;
    private int z;

    public AssiVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isInAABB(AssiVector min, AssiVector max) {
        return ((this.x <= max.x) && (this.x >= min.x) && (this.z <= max.z) && (this.z >= min.z) && (this.y <= max.y) && (this.y >= min.y));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
