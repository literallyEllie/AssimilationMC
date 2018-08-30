package net.assimilationmc.assibungee.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.assimilationmc.assibungee.AssiBungee;
import net.assimilationmc.assibungee.Module;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class SQLManager extends Module {

    public static final String DATABASE_DEV = "assimilationmc_dev", DATABASE_ACTIVE = "assimilationmc_prod";
    public static File FILE = new File("SQL_SERVER");
    private HikariDataSource dataSource;

    public SQLManager(AssiBungee plugin) {
        super(plugin, "SQL");
    }

    @Override
    protected void start() {
        if (!FILE.exists()) throw new RuntimeException("SQL_SERVER file cannot be null during module instantiation!");


        final Map<String, String> values = new SQLPropertyReader(FILE).readSQL();
        if (values.isEmpty()) throw new IllegalArgumentException("SQL Values cannot be empty!");

        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + values.get("ip") + ":3306/" + (getPlugin().getServerData()
                .isDev() ? DATABASE_DEV : DATABASE_ACTIVE) + "?useSSL=true");
        config.setUsername(values.get("username"));
        config.setPassword(values.get("password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useLocalTransactionState", "true");

        config.setLeakDetectionThreshold(60 * 1000);

        config.setPoolName("AssiBungee-sql");

        dataSource = new HikariDataSource(config);
    }

    @Override
    protected void end() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
