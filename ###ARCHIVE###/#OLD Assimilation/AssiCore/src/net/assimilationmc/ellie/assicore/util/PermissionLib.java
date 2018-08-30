package net.assimilationmc.ellie.assicore.util;

/**
 * Created by Ellie on 19/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class PermissionLib {

    private static String a = "assimilation";

    public static class CMD {

        public static String VANISH = a+".cmd.vanish";
        public static String ASSI = a+".cmd.assi";
        public static String MEMORY = a+".cmd.memory";
        public static String SET_SPAWN = a+".cmd.setspawn";
        public static String BROADCAST = a+".cmd.broadcast";
        public static String ECO = a+".cmd.eco";
        public static String MAINTENANCE = a+".cmd.maintenance";

        public static String TP = a+".cmd.tp";
        public static String TP_OTHER = a+".cmd.tp.other";
        public static String GAMEMODE = a+".cmd.gamemode";
        public static String GAMEMODE_OTHER = a+".cmd.gamemode.other";
        public static String CLEARINVENTORY = a+".cmd.clearinventory";
        public static String CLEARINVENTORY_OTHER = a+".cmd.clearinventory.other";
        public static String FLY = a+".cmd.fly";
        public static String FLY_OTHER = a+".cmd.fly.other";
        public static String FEED = a+".cmd.fly";
        public static String FEED_OTHER = a+".cmd.fly.other";
        public static String HEAL = a+".cmd.heal";
        public static String HEAL_OTHER = a+".cmd.heal.other";
        public static String SPAWN_OTHER = a+".cmd.spawn.other";

        public static class PERM {

            private static String b = a+".cmd.perms";

            public static String PERMISSION = b+".permission";
            public static String REFRESH = b+".refresh";

            public static String CREATE_GROUP = b+".creategroup";
            public static String LIST_GROUP = b+".listgroup";
            public static String DEL_GROUP = b+".delgroup";
            public static String PERM_ADD = b+".addperm";
            public static String PERM_DEL = b+".delperm";
            public static String GROUP_INFO = b+".info";
            public static String GROUP_OPTIONS = b+".info";

            public static String SET_GROUP = b+".setgroup";

        }
    }

    public static class CHAT {

        public static String COLOR = a+".chat.color";
        public static String FORMAT = a+".chat.format";

    }

    public static class BYPASS {

        public static String COMMAND_FILTER = a+".bypass.command";
        public static String VANISH = a+".bypass.vanish";
        public static String MAINTENANCE = a+".bypass.maintenance";

        public static String HUB = a+".bypass.hub";

    }

    public static class LOBBY {

        public static String FLY = "assihub.fly";
        public static String ADMIN = "assihub.admin";
    }

    public static String STAFF_CHAT = a+".staffchat";






}
