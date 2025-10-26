package com.mythicalgames.economy.api;

import com.mythicalgames.economy.database.DatabaseHandler;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyAPIManager {

    private static EconomyAPIManager instance;
    private final DatabaseHandler database;

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

    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        return database.getBalance(playerUUID);
    }

    public CompletableFuture<Void> addBalance(UUID playerUUID, double amount) {
        return database.addBalance(playerUUID, amount);
    }

    public CompletableFuture<Boolean> subtractBalance(UUID playerUUID, double amount) {
        return database.subtractBalance(playerUUID, amount);
    }

    public CompletableFuture<Void> setBalance(UUID playerUUID, double amount) {
        return database.setBalance(playerUUID, amount);
    }

    public CompletableFuture<String> getUUIDByUsername(String username) {
        return database.getUUIDByUsername(username);
    }

    public CompletableFuture<Boolean> hasAccount(UUID playerUUID) {
        return database.hasAccount(playerUUID);
    }

    public CompletableFuture<Boolean> createAccount(UUID playerUUID, String username, double defaultBalance) {
        return database.createAccount(playerUUID, username, defaultBalance);
    }

    public CompletableFuture<Void> updateUsername(UUID playerUUID, String newUsername) {
        return database.updateUsername(playerUUID, newUsername);
    }
}
