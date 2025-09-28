package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.mythicalgames.economy.EconomyAPI;
import com.mythicalgames.economy.database.DatabaseHandler;

public class AddMoneyCommand extends Command {

    private final EconomyAPI plugin;

    public AddMoneyCommand(EconomyAPI plugin) {
        super("addmoney", "Add money to a player's balance", "/addmoney <player> <amount>");
        this.setAliases(new String[]{"am"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cUsage: /addmoney <player> <amount>");
            return false;
        }

        String targetName = args[0];
        String amountStr = args[1];

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cInvalid amount: " + amountStr);
            return false;
        }

        if (amount < 0) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cMoney cannot be less than 0.");
            return false;
        }

        DatabaseHandler database = plugin.getDatabase();

        try {
            String uuid = database.getUUIDByUsername(targetName);
            if (uuid == null || !database.hasAccount(uuid)) {
                String notFound = plugin.getConfig().getString("player-not-found");
                if (notFound == null || notFound.isEmpty()) {
                    sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                } else {
                    sender.sendMessage(notFound.replace("PLAYER", targetName));
                }
                return false;
            }

            database.addBalance(uuid, amount);

            String formatTemplate = plugin.getConfig().getString("add-money-output");
            if (formatTemplate == null || formatTemplate.isEmpty()) {
                sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                return false;
            }

            String formatted = formatTemplate
                    .replace("PLAYER", targetName)
                    .replace("AMOUNT", String.valueOf(amount));

            sender.sendMessage(formatted);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cAn internal error occurred while updating the balance.");
            return false;
        }
    }
}

