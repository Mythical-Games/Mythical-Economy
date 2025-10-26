package com.mythicalgames.economy.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBHandler implements DatabaseHandler {

    private final MongoClient client;
    private final MongoDatabase database;

    public MongoDBHandler(String uri, String dbName) {
        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(dbName);

        database.getCollection("economy_data")
                .createIndex(new Document("player_uuid", 1), new IndexOptions().unique(true));

        System.out.println("[MythicalEconomy] MongoDB connection established to database: " + dbName);
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(UUID uuid) {
        return CompletableFuture.supplyAsync(() ->
                database.getCollection("economy_data")
                        .find(Filters.eq("player_uuid", uuid.toString()))
                        .first() != null
        );
    }

    @Override
    public CompletableFuture<Boolean> createAccount(UUID uuid, String username, double defaultBalance) {
        return CompletableFuture.supplyAsync(() -> {
            if (hasAccount(uuid).join()) return false;
            try {
                Document doc = new Document("player_uuid", uuid.toString())
                        .append("username", username)
                        .append("amount", defaultBalance);
                database.getCollection("economy_data").insertOne(doc);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Double> getBalance(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document doc = database.getCollection("economy_data")
                        .find(Filters.eq("player_uuid", uuid.toString()))
                        .first();
                if (doc != null && doc.containsKey("amount")) {
                    return doc.getDouble("amount");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0.0;
        });
    }

    @Override
    public CompletableFuture<Void> setBalance(UUID uuid, double amount) {
        return CompletableFuture.runAsync(() -> {
            database.getCollection("economy_data")
                    .updateOne(Filters.eq("player_uuid", uuid.toString()),
                            new Document("$set", new Document("amount", amount)));
        });
    }

    @Override
    public CompletableFuture<Boolean> subtractBalance(UUID uuid, double amount) {
        return getBalance(uuid).thenApply(current -> {
            if (current >= amount) {
                setBalance(uuid, current - amount).join();
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Void> addBalance(UUID uuid, double amount) {
        return getBalance(uuid).thenAccept(current -> setBalance(uuid, current + amount).join());
    }

    @Override
    public CompletableFuture<String> getUUIDByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document doc = database.getCollection("economy_data")
                        .find(Filters.eq("username", username))
                        .first();
                if (doc != null && doc.containsKey("player_uuid")) {
                    return doc.getString("player_uuid");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> updateUsername(UUID uuid, String newUsername) {
        return CompletableFuture.runAsync(() ->
                database.getCollection("economy_data")
                        .updateOne(Filters.eq("player_uuid", uuid.toString()),
                                new Document("$set", new Document("username", newUsername)))
        );
    }

    @Override
    public void close() {
        try {
            client.close();
            System.out.println("[MythicalEconomy] MongoDB connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
