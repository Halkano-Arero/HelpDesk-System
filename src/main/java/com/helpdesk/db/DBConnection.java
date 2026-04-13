package com.helpdesk.db;

import com.helpdesk.config.AppConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public final class DBConnection {

    private static final org.apache.tomcat.jdbc.pool.DataSource DATA_SOURCE = createDataSource();

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    public static void shutdown() {
        DATA_SOURCE.close();
    }

    public static void ensureSchema() {
        try (Connection connection = getConnection()) {
            if (!columnExists(connection, "users", "category")) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE users ADD COLUMN category VARCHAR(100) DEFAULT 'Other' AFTER role");
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE users SET category = 'Other' WHERE role = 'agent' AND (category IS NULL OR TRIM(category) = '')")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to verify helpdesk database schema.", e);
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?")) {
            ps.setString(1, connection.getCatalog());
            ps.setString(2, tableName);
            ps.setString(3, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static org.apache.tomcat.jdbc.pool.DataSource createDataSource() {
        PoolProperties properties = new PoolProperties();
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        properties.setUrl(AppConfig.get("db.url"));
        properties.setUsername(AppConfig.get("db.username"));
        properties.setPassword(AppConfig.get("db.password"));
        properties.setInitialSize(2);
        properties.setMaxActive(AppConfig.getInt("db.pool.maxSize", 10));
        properties.setMaxIdle(5);
        properties.setMinIdle(2);
        properties.setTestOnBorrow(true);
        properties.setValidationQuery("SELECT 1");
        properties.setJmxEnabled(true);

        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setPoolProperties(properties);
        return dataSource;
    }
}
