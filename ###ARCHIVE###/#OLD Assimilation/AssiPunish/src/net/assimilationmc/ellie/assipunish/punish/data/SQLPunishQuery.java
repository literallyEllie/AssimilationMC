package net.assimilationmc.ellie.assipunish.punish.data;

/**
 * Created by Ellie on 18/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SQLPunishQuery {

    private static final String TABLE = "assimilation_punish";
    public static final String INITIAL_STATEMENT = "CREATE TABLE IF NOT EXISTS `"+TABLE+"` (" +
            "`id` INT(100) NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT, "+
            "`uuid` VARCHAR(100) NULL, " + // punished UUID/IP
            "`type` VARCHAR(100) NOT NULL, "+     // PUNISHMENT TYPE
            "`offence` INT(100) NOT NULL, "+     // PUNISHMENT OFFENCE
            "`issued` BIGINT NOT NULL, "+     // PUNISHMENT ISSUED
            "`expire` VARCHAR(100) NULL, "+ // WHEN punisher expires
            "`punished_by` VARCHAR(100) NOT NULL, "+ // PUNISHER NAME
            "`custom_reason` LONGTEXT NULL, "+ // Custom reason
            "`unpunished_by` VARCHAR(100) NULL, "+ // UNPUNISHER NAME
            "`unpunished_time` BIGINT NULL, "+ // UNPUNISHER TIME
            "`unpunished_reason` LONGTEXT NULL, "+ // UNPUNISHER TIME
            "INDEX(uuid)) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;";

    public static final String GET_PLAYER_PUNISHMENTS = "SELECT * FROM `"+TABLE+"` WHERE uuid = :uuid ORDER BY id ASC;";

    public static final String GET_PUNISHMENT_OF_TYPE = "SELECT * FROM `"+TABLE+"` WHERE uuid = :uuid, type = :type ORDER BY id ASC;";

    public static final String GET_LATEST_PUNISHMENT_OF_TYPE = "SELECT * FROM `"+TABLE+"` WHERE uuid = :uuid AND type = :type ORDER BY issued DESC LIMIT 1;";

    public static final String INSERT_PUNISHMENT = "INSERT `"+TABLE+"` (uuid, type, offence, issued, expire, punished_by, custom_reason) " +
            "VALUES (:uuid, :type, :offence, :issued, :expire, :punisher, :customReason);";

    public static final String UNPUNISH = "UPDATE `"+TABLE+"` SET unpunished_by = :punishedBy, unpunished_time = :unpunishedtime, unpunished_reason " +
            "= :unpunishReason WHERE id = :id;";

    public static final String GET_ACTIVE = "SELECT * FROM `"+TABLE+"` WHERE NOT expire = -1 AND uuid = :uuid AND unpunished_by IS NULL ORDER BY issued ASC;";

    public static final String GET_ALL_ACTIVE = "SELECT * FROM `"+TABLE+"` WHERE NOT expire = -1 AND unpunished_by IS NULL;";


}
