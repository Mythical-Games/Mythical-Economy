package com.mythicalgames.economy.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class MongoDBHandler implements DatabaseHandler {

    private final MongoDatabase database;

    public MongoDBHandler(String uri, String dbName) {
        MongoClient client = MongoClients.create(uri);
        this.database = client.getDatabase(dbName);
    }

    @Override
    public boolean hasAccount(String uuid) {
        try {
            Document doc = database.getCollection("economy_data")
                .find(Filters.eq("player_uuid", uuid))
                .first();
            return doc != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean createAccount(String uuid, String username, double defaultBalance) {
        if (!hasAccount(uuid)) {
            try {
                Document doc = new Document("player_uuid", uuid)
                        .append("username", username)
                        .append("amount", defaultBalance);
                database.getCollection("economy_data").insertOne(doc);
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
            Document doc = database.getCollection("economy_data")
                .find(Filters.eq("player_uuid", uuid)).first();
            if (doc != null) {
                return doc.getDouble("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setBalance(String uuid, double amount) {
        try {
            Document doc = new Document("player_uuid", uuid)
                .append("amount", amount);
            database.getCollection("economy_data")
                .updateOne(Filters.eq("player_uuid", uuid),
                    new Document("$set", new Document("amount", amount)));
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
            Document doc = database.getCollection("economy_data")
                .find(Filters.eq("username", username)).first();
            if (doc != null) {
                return doc.getString("player_uuid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
