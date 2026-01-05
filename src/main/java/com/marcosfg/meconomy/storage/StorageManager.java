package com.marcosfg.meconomy.storage;

import com.marcosfg.meconomy.Main;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StorageManager {

    private final Main plugin;
    private boolean isMysql;

    private HikariDataSource dataSource;
    private File file;
    private FileConfiguration yaml;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
        this.isMysql = plugin.getConfig().getString("storage.type").equalsIgnoreCase("MYSQL");

        if (isMysql) {
            setupMysql();
        } else {
            setupYaml();
        }
    }

    private void setupMysql() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + plugin.getConfig().getString("storage.mysql.host") + ":" +
                plugin.getConfig().getString("storage.mysql.port") + "/"
                + plugin.getConfig().getString("storage.mysql.database"));
        config.setUsername(plugin.getConfig().getString("storage.mysql.user"));
        config.setPassword(plugin.getConfig().getString("storage.mysql.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + plugin.getConfig().getString("storage.mysql.table") + " (" +
                                "uuid VARCHAR(36) PRIMARY KEY, " +
                                "balance DOUBLE DEFAULT 0)")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupYaml() {
        file = new File(plugin.getDataFolder(), "accounts.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        yaml = YamlConfiguration.loadConfiguration(file);
    }

    public double loadBalance(UUID uuid) {
        if (isMysql) {
            try (Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement("SELECT balance FROM "
                            + plugin.getConfig().getString("storage.mysql.table") + " WHERE uuid=?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getDouble("balance");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return plugin.getConfig().getDouble("settings.start-balance");
        } else {
            return yaml.getDouble(uuid.toString(), plugin.getConfig().getDouble("settings.start-balance"));
        }
    }

    public void saveBalance(UUID uuid, double balance) {
        if (isMysql) {
            try (Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn
                            .prepareStatement("INSERT INTO " + plugin.getConfig().getString("storage.mysql.table")
                                    + " (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance=?")) {
                ps.setString(1, uuid.toString());
                ps.setDouble(2, balance);
                ps.setDouble(3, balance);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            yaml.set(uuid.toString(), balance);
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Double> getTopAccounts(int limit) {
        Map<String, Double> top = new LinkedHashMap<>();

        if (isMysql) {
            try (Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement("SELECT uuid, balance FROM "
                            + plugin.getConfig().getString("storage.mysql.table")
                            + " ORDER BY balance DESC LIMIT ?")) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        top.put(rs.getString("uuid"), rs.getDouble("balance"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Map<String, Double> all = new LinkedHashMap<>();
            for (String key : yaml.getKeys(false)) {
                all.put(key, yaml.getDouble(key));
            }

            top = all.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(limit)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                            LinkedHashMap::new));
        }

        return top;
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
