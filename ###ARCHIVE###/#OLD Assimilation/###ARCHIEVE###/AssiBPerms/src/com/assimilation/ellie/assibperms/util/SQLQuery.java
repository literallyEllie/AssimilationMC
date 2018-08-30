package com.assimilation.ellie.assibperms.util;

/**
 * Created by Ellie on 22/11/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SQLQuery {

    public static String TABLE = "assimilation_perm";
    public static String INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
            "`group_name` VARCHAR(100) NOT NULL PRIMARY KEY, "+
            "`prefix` VARCHAR(100), "+
            "`suffix` VARCHAR(100), "+
            "`parents` TEXT, "+
            "`options` TEXT, "+
            "`permissions` TEXT, "+
            "INDEX(group_name)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public static String GET_GROUPS = "SELECT * FROM `"+TABLE+"`;";
    public static String GET_GROUP = "SELECT prefix, suffix, parents, options, permissions, FROM `"+TABLE+"` WHERE group_name = ?;";
    public static String EXISTS = "SELECT prefix FROM `"+TABLE+"` WHERE group_name = ?;";
    public static String CREATE_GROUP = "INSERT INTO `"+TABLE+"` (group_name, prefix, suffix, parents, options, permissions) " +
            "VALUES (?, ?, ?, ?, ?, ?);";
    public static String PUSH_GROUP = "UPDATE `"+TABLE+"` SET prefix = ?, suffix = ?, parents = ?, options = ?, permissions = ? WHERE group_name = ?;";
    public static String DELETE_GROUP = "DELETE FROM `"+TABLE+"` WHERE group_name = ?;";

    public static class SPIGOT {

        public static String TABLE = "assimilation_perm_spigot";
        public static String INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
                "`group_name` VARCHAR(100) NOT NULL PRIMARY KEY, "+
                "`permissions` TEXT, "+
                "INDEX(group_name)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        public static String GET_GROUPS = "SELECT * FROM `"+TABLE+"`;";
        public static String DELETE_GROUP = "DELETE FROM `"+TABLE+"` WHERE group_name = ?;";
        public static String CREATE_GROUP = "INSERT INTO `"+TABLE+"` (group_name, permissions) " +
                "VALUES (?, ?);";
        public static String PUSH_GROUP = "UPDATE `"+TABLE+"` SET permissions = ? WHERE group_name = ?;";

    }

}
