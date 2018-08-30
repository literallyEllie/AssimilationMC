package com.assimilation.ellie.assicore.util;

/**
 * Created by Ellie on 20/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public final class SQLQuery {

    public static class PLAYERS {

        public static String TABLE = "assimilation_core_players";
        public static String INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
                "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, "+
                "`uuid` VARCHAR(100) NOT NULL UNIQUE, " +
                "`name` VARCHAR(100) NOT NULL, "+
                "`last_seen` BIGINT NOT NULL, "+
                "`last_ip` VARCHAR(100) NOT NUll, "+
                "`previous_names` MEDIUMTEXT NULL, "+
                "`perm_rank` VARCHAR(100) NOT NULL," +
                "`rank` INT(100), " +
                "`coins` INT(100), "+
                "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;";

        public static String GET_PLAYER = "SELECT name, last_ip, previous_names, perm_rank, rank, coins FROM `"+TABLE+"` WHERE uuid = ?;";
        public static String EXISTS = "SELECT name FROM `"+TABLE+"` WHERE uuid = ?;";

    }

    public static class FRIENDS {

        public static String TABLE = "assimilation_core_friend";
        public static String INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
                "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, "+
                "`uuid` VARCHAR(100) NOT NULL UNIQUE, " +
                "`name` VARCHAR(100) NOT NULL, "+
                "`last_seen` BIGINT NOT NULL, "+
                "`op_requests` TINYINT, "+ // allow receiving of request
                "`op_join` TINYINT, "+ // sendin messages when player joins
                "`op_leave` TINYINT, "+ // send message wehen player levaes
                "`friends` LONGTEXT NULL, "+ // json string of friends
                "`requests` LONGTEXT NULL, "+
                "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;";

        public static String GET_FRIEND = "SELECT name, last_seen, op_requests , op_join, op_leave, friends, requests FROM `"+TABLE+"` WHERE uuid = ?;";
        public static String GET_FRIENDS = "SELECT friends FROM `"+TABLE+"` WHERE uuid = ?;";
        public static String PUSH_FRIENDS = "UPDATE `"+TABLE+"` SET friends = ? WHERE uuid = ?;";
        public static String PUSH_FRIEND = "UPDATE `"+TABLE+"` SET name = ?, last_seen = ?, friends = ? WHERE uuid = ?;";

        public static String GET_REQUESTS = "SELECT requests FROM `"+TABLE+"` WHERE uuid = ?;";
        public static String PUSH_REQUESTS = "UPDATE `"+TABLE+"` SET requests = ? WHERE uuid = ?;";

        public static String GET_ALL_OPTIONS = "SELECT op_requests, op_join, op_leave WHERE uuid = ?;";
        public static String GET_OP_REQUEST = "SELECT op_requests FROM `"+TABLE+"` WHERE uuid = ?;";
        public static String GET_OP_JOIN = "SELECT op_join FROM `"+TABLE+"` WHERE uuid = ?;";
        public static String GET_OP_LEAVE = "SELECT op_leave FROM `"+TABLE+"` WHERE uuid = ?;";

        public static String PUSH_ALL_OPTIONS = "UPDATE `"+TABLE+"` SET op_requests = ?, op_join = ?, op_leave = ? WHERE uuid = ?;";
        public static String PUSH_OP_REQUEST = "UPDATE `"+TABLE+"` SET op_requests = ? WHERE uuid = ?;";
        public static String PUSH_OP_JOIN = "UPDATE `"+TABLE+"` SET op_join = ? WHERE uuid = ?;";
        public static String PUSH_OP_LEAVE = "UPDATE `"+TABLE+"` SET op_leave = ? WHERE uuid = ?;";

    }

    public static class PERMISSION {

        public static String BUNGEE_TABLE = "assimilation_perm";
        public static String SPIGOT_TABLE = "assimilation_perm_spigot";

        public static String BUNGEE_GET_GROUPS = "SELECT * FROM `" + BUNGEE_TABLE + "`;";
        public static String BUNGEE_GET_GROUP = "SELECT prefix, suffix, parents, options, permissions, FROM `" + BUNGEE_TABLE + "` WHERE group_name = ?;";

        public static String SPIGOT_INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `" + SPIGOT_TABLE + "` (" +
                "`group_name` VARCHAR(100) NOT NULL PRIMARY KEY, " +
                "`permissions` TEXT, " +
                "INDEX(group_name)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        public static String SPIGOT_GET_GROUPS = "SELECT * FROM `" + SPIGOT_TABLE + "`;";

    }
}
