package net.assimilationmc.enjinclient.connection;

/**
 * Created by Ellie on 01/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinSite {

    private String name;
    private String description;
    private String url;
    private String logo;
    private String banner;
    private boolean canJoin;
    private boolean isLiked;
    private int access;
    private int users;
    private int likes;

    public EnjinSite(String name, String description, String url, String logo, String banner, boolean canJoin, boolean isLiked, int access, int users, int likes){
        this.name = name;
        this.description = description;
        this.url = url;
        this.logo = logo;
        this.banner = banner;
        this.canJoin = canJoin;
        this.isLiked = isLiked;
        this.access = access;
        this.users = users;
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getLogo() {
        return logo;
    }

    public String getBanner() {
        return banner;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public int getAccess() {
        return access;
    }

    public int getUsers() {
        return users;
    }

}
