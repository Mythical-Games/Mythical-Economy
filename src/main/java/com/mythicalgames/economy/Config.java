package com.mythicalgames.economy;

import com.mythicalgames.economy.MythicalEconomy.DatabaseType;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;

@Header({
    "###############################################",
    "# Thank you for downloading Mythical-Economy",
    "# You are now part of our Mythical Ecosystem",
    "###############################################"
})

public class Config extends OkaeriConfig {
    @Comment(" ")
    @Comment(" ")

    // ========================
    // DATABASE SETTINGS
    // ========================
    @Comment({
        "Database-Type - Choose your provider for the database that you want to use!",
        "It's either (SQLITE) or (MONGODB)"
    })
    public DatabaseType database_type = DatabaseType.SQLITE;

    @Comment(" ")

    @Comment("MongoDB credentials - In case you are using Mongo like me!")
    public String mongo_uri = " ";
    public String mongo_database_name = "mythicaleconomy";

    @Comment(" ")
    @Comment(" ")

    // ========================
    // ECONOMY SETTINGS
    // ========================
    @Comment("Default Economy Settings - Change as per your liking")
    public double default_balance = 1000;

    @Comment(" ")
    @Comment(" ")

    // ========================
    // TRANSLATIONS
    // ========================
    @Comment({
        "Translations - Customize all the messages to your liking",
        "PLAYER - Displays the player name (PLACEHOLDER)",
        "BALANCE - Displays the player balance (PLACEHOLDER)",
        "AMOUNT - Applies for certain commands"
    })

    // ----- ERROR MESSAGES -----
    public String error_player_only        = "§l§7[§dMythical-Economy§7] §r§cThis command can only be run by a player or OP.";
    public String error_invalid_amount     = "§l§7[§dMythical-Economy§7] §r§cPlease provide a valid number (1 - 9999999999)...";
    public String error_config             = "§l§7[§dMythical-Economy§7] §r§cA configuration issue was detected! Please report to a server admin.";
    public String error_internal           = "§l§7[§dMythical-Economy§7] §r§cAn internal error occurred while updating the balance.";
    public String error_pay_failure_1      = "§l§7[§dMythical-Economy§7] §r§cYou cannot send money to yourself.";
    public String error_pay_failure_2      = "§l§7[§dMythical-Economy§7] §r§cYou don't have enough money in your balance to make this transfer.";
    public String error_account_creation   = "§l§7[§dMythical-Economy§7] §r§cFailed to create your Economy account. Please contact a server admin.";

    @Comment(" ")

    // ----- USAGE MESSAGES -----
    public String usage_add_money          = "§l§7[§dMythical-Economy§7] §r§cUsage: /addmoney <player> <amount>";
    public String usage_pay_money          = "§l§7[§dMythical-Economy§7] §r§cUsage: /pay <player> <amount>";
    public String usage_reduce_money       = "§l§7[§dMythical-Economy§7] §r§cUsage: /reducemoney <player> <amount>";
    public String usage_set_money          = "§l§7[§dMythical-Economy§7] §r§cUsage: /setmoney <player> <amount>";

    @Comment(" ")
    
    // ----- SUCCESS MESSAGES -----
    public String new_player_notify        = "§l§7[§dMythical-Economy§7] §rWelcome to the server §ePLAYER!§r You have been granted §eBALANCE$§r as a starting bonus!";
    public String self_balance_output      = "§l§7[§dMythical-Economy§7] §rYou have §eBALANCE$§r available!";
    public String player_not_found         = "§l§7[§dMythical-Economy§7] §r§cMentioned player not found!";
    public String player_balance_output    = "§l§7[§dMythical-Economy§7] §r§ePLAYER§r has §eBALANCE$§r available!";
    public String add_money_output         = "§l§7[§dMythical-Economy§7] Successfully added §eAMOUNT$§r to §ePLAYER's§r balance!";
    public String reduce_money_output      = "§l§7[§dMythical-Economy§7] Successfully removed §eAMOUNT$§r from §ePLAYER's§r balance!";
    public String set_money_output         = "§l§7[§dMythical-Economy§7] §r§ePLAYER's§r balance has been updated to §eBALANCE!";
}
