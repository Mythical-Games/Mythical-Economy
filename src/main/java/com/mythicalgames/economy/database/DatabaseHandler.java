package com.mythicalgames.economy.database;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DatabaseHandler {

    CompletableFuture<Boolean> hasAccount(UUID uuid);

    CompletableFuture<Boolean> createAccount(UUID uuid, String username, double defaultBalance);

    CompletableFuture<Double> getBalance(UUID uuid);

    CompletableFuture<Void> setBalance(UUID uuid, double amount);

    CompletableFuture<Boolean> subtractBalance(UUID uuid, double amount);

    CompletableFuture<Void> addBalance(UUID uuid, double amount);

    CompletableFuture<String> getUUIDByUsername(String username);

    CompletableFuture<Void> updateUsername(UUID uuid, String newUsername);

    void close();
}

