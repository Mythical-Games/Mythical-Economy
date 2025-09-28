package com.mythicalgames.economy.database;

import java.sql.*;

public class SQLiteHandler implements DatabaseHandler {
    private Connection connection;

    public SQLiteHandler(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            String createTable = "CREATE TABLE IF NOT EXISTS economy_data (" +
                                 "player_uuid TEXT PRIMARY KEY, " +
                                 "username TEXT NOT NULL, " +
                                 "amount DOUBLE NOT NULL DEFAULT 0)";
            connection.prepareStatement(createTable).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasAccount(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM economy_data WHERE player_uuid = ?");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean createAccount(String uuid, String username, double defaultBalance) {
        if (!hasAccount(uuid)) {
            try {
                PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO economy_data (player_uuid, username, amount) VALUES (?, ?, ?)");
                statement.setString(1, uuid);
                statement.setString(2, username);
                statement.setDouble(3, defaultBalance);
                statement.executeUpdate();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public double getBalance(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT amount FROM economy_data WHERE player_uuid = ?");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setBalance(String uuid, double amount) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE economy_data SET amount = ? WHERE player_uuid = ?");
            statement.setDouble(1, amount);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean subtractBalance(String uuid, double amount) {
        double current = getBalance(uuid);
        if (current >= amount) {
            setBalance(uuid, current - amount);
            return true;
        }
        return false;
    }

    @Override
    public void addBalance(String uuid, double amount) {
        double current = getBalance(uuid);
        setBalance(uuid, current + amount);
    }

    @Override
    public String getUUIDByUsername(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT player_uuid FROM economy_data WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("player_uuid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
