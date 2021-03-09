package net.vaxile.analytics.common.storage.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.vaxile.analytics.common.AnalyticsPlugin;
import net.vaxile.analytics.common.storage.DataStorage;
import net.vaxile.analytics.common.util.UuidUtil;
import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Slf4j
public class H2Storage implements DataStorage {
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `server_joins` (" +
                    "hostname VARCHAR(32) NOT NULL, " +
                    "uuid BINARY(16) NOT NULL, " +
                    "join_count INT NOT NULL, " +
                    "PRIMARY KEY (hostname, uuid))";
    private static final String GET_UNIQUE =
            "SELECT COUNT(*) from `server_joins` WHERE hostname = ?";
    private static final String GET_TOTAL =
            "SELECT SUM(join_count) FROM `server_joins` WHERE hostname = ?";
    private static final String INSERT =
            "INSERT INTO `server_joins` " +
                    "(hostname, uuid, join_count) VALUES(?, ?, 1) " +
                    "ON DUPLICATE KEY UPDATE join_count = join_count + 1";


    private HikariDataSource hikariDataSource;
    private final File dataFolder;

    public H2Storage(@NonNull AnalyticsPlugin plugin) {
        this.dataFolder = plugin.getDataFolder();
    }

    @Override
    public boolean init() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName(JdbcDataSource.class.getName());
        hikariConfig.addDataSourceProperty("URL", "jdbc:h2:" + dataFolder.getAbsolutePath() +
                "/database;MODE=MySQL");

        try {
            hikariDataSource = new HikariDataSource(hikariConfig);
        } catch (HikariPool.PoolInitializationException e) {
            log.error("Unable to connect to the plugin primary database.", e);
        }

        try (Connection conn = hikariDataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Unable to create plugin primary database table.", e);
        }

        return hikariDataSource.isRunning();
    }

    @Override
    public void close() {
        if (hikariDataSource != null && hikariDataSource.isRunning()) {
            hikariDataSource.close();
        }
    }

    @Override
    public void insertJoin(String hostname, UUID uniqueId) {
        try (Connection conn = hikariDataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            stmt.setString(1, hostname);
            stmt.setBytes(2, UuidUtil.wrap(uniqueId));
            stmt.execute();
        } catch (SQLException e) {
            log.error("Unable to insert join for {} on hostname {}.", uniqueId, hostname);
        }
    }

    @Override
    public int getUnique(String hostname) {
        return get(hostname, GET_UNIQUE);
    }

    @Override
    public int getTotal(String hostname) {
        return get(hostname, GET_TOTAL);
    }

    private int get(String hostname, String statement) {
        try (Connection conn = hikariDataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(statement)) {
            stmt.setString(1, hostname);
            try (ResultSet set = stmt.executeQuery()) {
                if (set.next()) {
                    return set.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("Unable to retrieve data for {}.", hostname, e);
        }
        return 0;
    }
}
