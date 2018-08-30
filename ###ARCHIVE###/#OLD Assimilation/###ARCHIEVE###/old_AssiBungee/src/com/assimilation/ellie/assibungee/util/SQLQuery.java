package com.assimilation.ellie.assibungee.util;

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
        public static String CREATE_PLAYER = "INSERT INTO `"+TABLE+"` (uuid, name, last_seen, last_ip, previous_names, perm_rank, rank, coins) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        public static String PUSH_PLAYER = "UPDATE `"+TABLE+"` SET name = ?, last_seen = ?, last_ip = ?, previous_names = ?, perm_rank = ?, rank = ?, coins = ? WHERE uuid = ?;";
        public static String PUSH_PLAYER_GROUP = "UPDATE `"+TABLE+"` SET perm_rank = ? WHERE uuid = ?;";

        public static String GET_PLAYER_NAME = "SELECT uuid, last_ip, previous_names, perm_rank, rank, coins FROM `"+TABLE+"` WHERE name = ?;";

    }

    public static class HELPOP {

        public static String TABLE = "assimilation_helpop";
        public static String INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
                "`id` INT(100) NOT NULL PRIMARY KEY AUTO_INCREMENT, "+
                "`sender` VARCHAR(100) NOT NULL, " +
                "`server` VARCHAR(100) NOT NULL, "+
                "`handler` VARCHAR(100) NULL, "+
                "`handled` VARCHAR(6) NOT NULL, "+
                "`sent` BIGINT NOT NULL, " +
                "`ssid` INT(100) NOT NULL, "+
                "`content` VARCHAR(300) NOT NULL, "+
                "INDEX(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;";
        public static String GET_NEXT = "SELECT id FROM `"+TABLE+"` ORDER BY id DESC LIMIT 1;";
        public static String GET_UNHANDLED = "SELECT * FROM `"+TABLE+"` WHERE handled = 0;";
        public static String PUT_NEW_HELPOP = "INSERT INTO `"+TABLE+"` (sender, server, handled, sent, ssid, content) VALUES (?, ?, ?, ?, ?, ?);";
        public static String HANDLE_HELPOP = "UPDATE `"+TABLE+"` SET handler = ?, handled = ? WHERE id = ?;";



    }

}
