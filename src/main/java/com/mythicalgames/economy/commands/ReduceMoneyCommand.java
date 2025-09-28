package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.mythicalgames.economy.EconomyAPI;
import com.mythicalgames.economy.database.DatabaseHandler;

public class ReduceMoneyCommand extends Command {

    private final EconomyAPI plugin;

    public ReduceMoneyCommand(EconomyAPI plugin) {
        super("reducemoney", "Reduces money from a player's balance", "/reducemoney <player> <amount>");
        this.setAliases(new String[]{"rm"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer() && !sender.isOp()) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cThis command can only be run by a player or OP.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cUsage: /reducemoney <player> <amount>");
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
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cAmount must be ≥ 0.");
            return false;
        }

        DatabaseHandler database = plugin.getDatabase();

        try {
            String uuid = database.getUUIDByUsername(targetName);
            if (uuid == null || !database.hasAccount(uuid)) {
                String playerNotFound = plugin.getConfig().getString("player-not-found");
                if (playerNotFound == null || playerNotFound.isEmpty()) {
                    sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                } else {
                    sender.sendMessage(playerNotFound.replace("PLAYER", targetName));
                }
                return false;
            }

            boolean success = database.subtractBalance(uuid, amount);
            if (!success) {
                sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cPlayer doesn't have enough funds.");
                return false;
            }

            String formatTemplate = plugin.getConfig().getString("reduce-money-output");
            if (formatTemplate == null || formatTemplate.isEmpty()) {
                sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                return false;
            }

            String formatted = formatTemplate
                    .replace("PLAYER", targetName)
                    .replace("AMOUNT", String.format("%.2f", amount));

            sender.sendMessage(formatted);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cAn internal error occurred while trying to reduce the balance.");
            return false;
        }
    }
}

