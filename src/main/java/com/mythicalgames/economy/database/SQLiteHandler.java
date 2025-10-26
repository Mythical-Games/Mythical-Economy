package com.mythicalgames.economy.database;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLiteHandler implements DatabaseHandler {
    private final Connection connection;

    public SQLiteHandler(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS economy_data (" +
                        "player_uuid TEXT PRIMARY KEY, " +
                        "username TEXT NOT NULL, " +
                        "amount DOUBLE NOT NULL DEFAULT 0)");
            }

            System.out.println("[MythicalEconomy] SQLite initialized successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite", e);
        }
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT 1 FROM economy_data WHERE player_uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> createAccount(UUID uuid, String username, double defaultBalance) {
        return hasAccount(uuid).thenApplyAsync(exists -> {
            if (exists) return false;
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO economy_data (player_uuid, username, amount) VALUES (?, ?, ?)")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.setDouble(3, defaultBalance);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Double> getBalance(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT amount FROM economy_data WHERE player_uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble("amount");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0.0;
        });
    }

    @Override
    public CompletableFuture<Void> setBalance(UUID uuid, double amount) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE economy_data SET amount = ? WHERE player_uuid = ?")) {
                ps.setDouble(1, Math.round(amount * 100.0) / 100.0);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> subtractBalance(UUID uuid, double amount) {
        return getBalance(uuid).thenApplyAsync(current -> {
            if (current >= amount) {
                setBalance(uuid, current - amount).join();
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Void> addBalance(UUID uuid, double amount) {
        return getBalance(uuid).thenAcceptAsync(current -> setBalance(uuid, current + amount).join());
    }

    @Override
    public CompletableFuture<String> getUUIDByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT player_uuid FROM economy_data WHERE username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("player_uuid");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> updateUsername(UUID uuid, String newUsername) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE economy_data SET username = ? WHERE player_uuid = ?")) {
                ps.setString(1, newUsername);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
            System.out.println("[MythicalEconomy] SQLite connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
