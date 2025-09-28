package com.mythicalgames.economy;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import com.mythicalgames.economy.api.EconomyAPIManager;
import com.mythicalgames.economy.commands.AddMoneyCommand;
import com.mythicalgames.economy.commands.BalanceCommand;
import com.mythicalgames.economy.commands.EconomyInfoCommand;
import com.mythicalgames.economy.commands.PayCommand;
import com.mythicalgames.economy.commands.ReduceMoneyCommand;
import com.mythicalgames.economy.commands.SetMoneyCommand;
import com.mythicalgames.economy.database.DatabaseHandler;
import com.mythicalgames.economy.database.MongoDBHandler;
import com.mythicalgames.economy.database.SQLiteHandler;

import com.mythicalgames.economy.events.PlayerListener;

public class EconomyAPI extends PluginBase {

    private static EconomyAPI instance;
    private DatabaseHandler database;
    private Config config;
    private boolean enableAscii = true;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        getLogger().info("Loading Configuration file..!");
        config = getConfig();
    }

    @Override
    public void onEnable() {
        if (enableAscii) displayASCII();
        try {
            initializeDatabase();
            EconomyAPIManager.initialize(database);
            registerCommandsAndEvents();
            getLogger().info("Mythical-Economy has been enabled!");
        } catch (Exception e) {
            getLogger().error("Failed to enable Mythical-Economy: ", e);
            getPluginLoader().disablePlugin(instance);
        }
    }

    private void initializeDatabase() {
        String databaseType = config.getString("database-type", "SQLITE");

        switch (databaseType.toUpperCase()) {
            case "SQLITE":
                database = new SQLiteHandler(getDataFolder().getPath() + "/economy.db");
                getLogger().info("Using SQLITE as default Database-Provider...");
                break;

            case "MONGODB":
                String mongoURI = config.getString("mongo-uri", "");
                String mongoDBName = config.getString("mongo-database-name", "");
                if (mongoURI.isEmpty() || mongoDBName.isEmpty()) {
                    getLogger().error("MongoDB configuration missing. Please check mongo-uri and mongo-database-name.");
                    getPluginLoader().disablePlugin(instance);
                    throw new IllegalArgumentException("MongoDB settings are missing or invalid.");
                }
                database = new MongoDBHandler(mongoURI, mongoDBName);
                getLogger().info("Using MONGODB as Database-Provider...");
                break;

            default:
                getLogger().error("Unsupported database type: " + databaseType);
                getPluginLoader().disablePlugin(instance);
                throw new UnsupportedOperationException("Unsupported Database Provider. Use SQLITE or MONGODB.");
        }
    }

    private void registerCommandsAndEvents() {
         getServer().getPluginManager().registerEvents(new PlayerListener(), this);

         getServer().getCommandMap().register("mythicaleconomy", new SetMoneyCommand(this));
         getServer().getCommandMap().register("mythicaleconomy", new AddMoneyCommand(this));
         getServer().getCommandMap().register("mythicaleconomy", new ReduceMoneyCommand(this));
         getServer().getCommandMap().register("mythicaleconomy", new BalanceCommand(this));
         getServer().getCommandMap().register("mythicaleconomy", new EconomyInfoCommand());
         getServer().getCommandMap().register("mythicaleconomy", new PayCommand(this));

    }

    public void displayASCII() {
        this.getLogger().info(".   _____  _________  ____  __.________________ __________ ");
        this.getLogger().info("  /  _  \\ \\_   ___ \\|    |/ _|\\__    ___/  _  \\\\______   \\");
        this.getLogger().info(" /  /_\\  \\/    \\  \\/|      <  |    | /  /_\\   \\|       _/");
        this.getLogger().info("/    |    \\     \\___|    |\\    |    |/    |     \\   |   \\");
        this.getLogger().info("\\____|__  /\\______  /____|__\\  |____| \\____|__   /____|_  /");
        this.getLogger().info("         \\/        \\/      \\/              \\/      \\/ ");
    }

    public static EconomyAPI getInstance() {
        return instance;
    }
    
    public static EconomyAPIManager getAPI() {
        return EconomyAPIManager.getAPI();
    }

}
