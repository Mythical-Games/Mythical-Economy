package com.mythicalgames.economy.database;

public interface DatabaseHandler {
    boolean hasAccount(String uuid);
    boolean createAccount(String uuid, String username, double defaultBalance);
    double getBalance(String uuid);
    void setBalance(String uuid, double amount);
    boolean subtractBalance(String uuid, double amount);
    void addBalance(String uuid, double amount);
    String getUUIDByUsername(String username); // For reverse lookup
}
