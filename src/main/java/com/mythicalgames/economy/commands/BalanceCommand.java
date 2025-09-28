package com.mythicalgames.economy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.mythicalgames.economy.EconomyAPI;
import com.mythicalgames.economy.database.DatabaseHandler;

public class BalanceCommand extends Command {

    private final EconomyAPI plugin;

    public BalanceCommand(EconomyAPI plugin) {
        super("balance", "Display your or another player's balance", "/balance [player]");
        this.setAliases(new String[]{"bal", "money"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        DatabaseHandler database = plugin.getDatabase();

        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player playerSender = (Player) sender;

        try {
            if (args.length == 0) {
                // Self balance
                String uuid = playerSender.getUniqueId().toString();
                double balance = database.getBalance(uuid);

                String formatTemplate = plugin.getConfig().getString("self-balance-output");
                if (formatTemplate == null || formatTemplate.isEmpty()) {
                    sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                    return true;
                }

                String message = formatTemplate.replace("BALANCE", String.valueOf(balance));
                sender.sendMessage(message);
                return true;

            } else {
                // Other player balance
                String targetName = args[0];
                String targetUUID = database.getUUIDByUsername(targetName);

                if (targetUUID == null || !database.hasAccount(targetUUID)) {
                    String notFound = plugin.getConfig().getString("player-not-found");
                    if (notFound == null || notFound.isEmpty()) {
                        sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                    } else {
                        sender.sendMessage(notFound);
                    }
                    return true;
                }

                double balance = database.getBalance(targetUUID);

                String formatTemplate = plugin.getConfig().getString("player-balance-output");
                if (formatTemplate == null || formatTemplate.isEmpty()) {
                    sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                    return true;
                }

                String message = formatTemplate
                        .replace("PLAYER", targetName)
                        .replace("BALANCE", String.valueOf(balance));

                sender.sendMessage(message);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cAn internal error occurred while trying to retrieve the balance.");
            return true;
        }
    }
}
