package com.mythicalgames.economy;

import com.mythicalgames.economy.api.EconomyAPIManager;
import com.mythicalgames.economy.commands.AddMoneyCommand;
import com.mythicalgames.economy.commands.BalanceCommand;
import com.mythicalgames.economy.commands.PayCommand;
import com.mythicalgames.economy.commands.ReduceMoneyCommand;
import com.mythicalgames.economy.commands.SetMoneyCommand;
import com.mythicalgames.economy.database.DatabaseHandler;
import com.mythicalgames.economy.database.MongoDBHandler;
import com.mythicalgames.economy.database.SQLiteHandler;
import com.mythicalgames.economy.listeners.PlayerListener;

import lombok.extern.slf4j.Slf4j;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;

import java.util.UUID;

import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.papi.PlaceholderAPI;
import org.allaymc.papi.PlaceholderProcessor;

@Slf4j
public class MythicalEconomy extends Plugin {
    private static MythicalEconomy instance;
    private DatabaseHandler database;
    public Config config;
    public enum DatabaseType { SQLITE, MONGODB };

    @Override
    public void onLoad() {
        instance = this;
        log.info("Loading Configuration file..!");
        config = ConfigManager.create(Config.class, config -> {
            config.withConfigurer(new YamlSnakeYamlConfigurer());
            config.withBindFile(pluginContainer.dataFolder().resolve("config.yml"));
            config.withRemoveOrphans(true);
            config.saveDefaults();
            config.load(true);
        });
    }

    @Override
    public void onEnable() {
        try {
            initializeDatabase();
            EconomyAPIManager.initialize(database);
            log.info("Mythical-Economy successfully initialized with provider: {}", database.getClass().getSimpleName());
            registerCommandsAndEvents();

            // https://github.com/AllayMC/PlaceholderAPI
            PlaceholderProcessor processor = (player, input) -> {
                 UUID playerUUID = player.getLoginData().getUuid();
                 double balance = getAPI().getBalance(playerUUID).join();

                 return String.format("%.0f", balance);
            };

            boolean success = PlaceholderAPI.getAPI().registerPlaceholder(instance, "balance", processor);

            if (success) log.info("Mythical-Economy placeholders registered successfully!");
            log.info("Mythical-Economy has been enabled!");
        } catch (Exception e) {
            log.error("Failed to enable Mythical-Economy: ", e);
        }
    }

    private void initializeDatabase() {
        switch (config.database_type) {
            case SQLITE:
                database = new SQLiteHandler(this.getPluginContainer().dataFolder() + "/economy.db");
                break;

            case MONGODB:
                String mongoURI = config.mongo_uri;
                String mongoDBName = config.mongo_database_name;
                if (mongoURI.isEmpty() || mongoDBName.isEmpty()) {
                    log.error("MongoDB configuration missing. Please check mongo-uri and mongo-database-name.");
                    throw new IllegalArgumentException("MongoDB settings are missing or invalid.");
                }
                database = new MongoDBHandler(mongoURI, mongoDBName);
                break;

            default:
                log.error("Unsupported database type: " + config.database_type);
                throw new UnsupportedOperationException("Unsupported Database Provider. Use SQLITE or MONGODB.");
        }
    }

    private void registerCommandsAndEvents() {
         Server.getInstance().getEventBus().registerListener(new PlayerListener());
         Registries.COMMANDS.register(new BalanceCommand());
         Registries.COMMANDS.register(new PayCommand(this));
         Registries.COMMANDS.register(new SetMoneyCommand(this));
         Registries.COMMANDS.register(new AddMoneyCommand(this));
         Registries.COMMANDS.register(new ReduceMoneyCommand(this));
    } 

    public static MythicalEconomy getInstance() {
        return instance;
    }
    
    public static EconomyAPIManager getAPI() {
        return EconomyAPIManager.getAPI();
    }
}