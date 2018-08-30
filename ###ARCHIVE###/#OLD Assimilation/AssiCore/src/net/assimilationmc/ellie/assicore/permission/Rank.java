package net.assimilationmc.ellie.assicore.permission;

import org.bukkit.ChatColor;

/**
 * Created by Ellie on 27/08/2017 for AssimilationMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum Rank {

    USER("User", ChatColor.GRAY),

    DEMONIC("Demonic", ChatColor.GRAY), // todo
    INFERNAL("Infernal", ChatColor.GRAY), //todo

    STREAMER("Streamer", ChatColor.DARK_PURPLE),
    YOUTUBE("YouTuber", ChatColor.RED),

    BUILDER("Builder", ChatColor.DARK_AQUA),
    DEVELOPER("Developer", ChatColor.BLUE),

    HELPER("Helper", ChatColor.GREEN),
    MOD("Moderator", ChatColor.AQUA),
    ADMIN("Admin", ChatColor.RED),
    MANAGER("Manager", ChatColor.DARK_PURPLE),
    OWNER("Owner", ChatColor.LIGHT_PURPLE),


    ;

    private String name;
    private ChatColor chatColor;

    Rank(String name, ChatColor chatColor) {
        this.name = name;
        this.chatColor = chatColor;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getPrefix() {
        return isDefault() ? chatColor.toString()
                : chatColor + ChatColor.BOLD.toString() + this.name + ChatColor.RESET;
    }

    public boolean isHigherThanOrEqualTo(Rank rank) {
        return this.ordinal() >= rank.ordinal();
    }

    public boolean isDefault() {
        return this == USER;
    }

    public boolean isPromoter() {
        return this == YOUTUBE || this == STREAMER;
    }

    public boolean isDonator() {
        return this == DEMONIC || this == INFERNAL;
    }

    public int getId() {
        return this.ordinal();
    }

    public static boolean exists(String name) {
        return fromString(name) != null;
    }

    public static Rank fromString(String name) {
        for (Rank rank : values()) {
            if(rank.toString().equalsIgnoreCase(name) || rank.getName().equalsIgnoreCase(name))
                return rank;
        }
        return null;
    }


}
