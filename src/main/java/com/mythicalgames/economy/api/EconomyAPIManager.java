package com.mythicalgames.economy.api;

import com.mythicalgames.economy.database.DatabaseHandler;

public class EconomyAPIManager {

    private static EconomyAPIManager instance;
    private DatabaseHandler database;

    private EconomyAPIManager(DatabaseHandler database) {
        this.database = database;
    }

    public static void initialize(DatabaseHandler database) {
        if (instance == null) {
            instance = new EconomyAPIManager(database);
        }
    }

    public static EconomyAPIManager getAPI() {
        if (instance == null) {
            throw new IllegalStateException("EconomyAPIManager has not been initialized!");
        }
        return instance;
    }

    public double getBalance(String playerUUID) {
        return database.getBalance(playerUUID);
    }

    public void addBalance(String playerUUID, double amount) {
        database.addBalance(playerUUID, amount);
    }

    public boolean subtractBalance(String playerUUID, double amount) {
        return database.subtractBalance(playerUUID, amount);
    }

    public void setBalance(String playerUUID, double amount) {
        database.setBalance(playerUUID, amount);
    }

    public String getUUIDByUsername(String username) {
        return database.getUUIDByUsername(username);
    }

    public boolean hasAccount(String playerUUID) {
        return database.hasAccount(playerUUID);
    }

    public boolean createAccount(String uuid, String username, double defaultBalance) {
    return database.createAccount(uuid, username, defaultBalance);
}

}
