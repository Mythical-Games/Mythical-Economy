package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.mythicalgames.economy.EconomyAPI;
import com.mythicalgames.economy.database.DatabaseHandler;

public class SetMoneyCommand extends Command {

    private final EconomyAPI plugin;

    public SetMoneyCommand(EconomyAPI plugin) {
        super("setmoney", "Sets a player's balance", "/setmoney <player> <amount>");
        this.setAliases(new String[]{"sm"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer() && !sender.isOp()) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cThis command can only be run by a player or OP.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cUsage: /setmoney <player> <amount>");
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
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cMoney must be greater than or equal to 0.");
            return false;
        }

        DatabaseHandler database = plugin.getDatabase();

        try {
            String uuid = database.getUUIDByUsername(targetName);
            if (uuid == null || !database.hasAccount(uuid)) {
                String notFoundMessage = plugin.getConfig().getString("player-not-found");
                if (notFoundMessage == null || notFoundMessage.isEmpty()) {
                    sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                } else {
                    sender.sendMessage(notFoundMessage.replace("PLAYER", targetName));
                }
                return false;
            }

            database.setBalance(uuid, amount);
            
            String formatTemplate = plugin.getConfig().getString("set-money-output");
            if (formatTemplate == null || formatTemplate.isEmpty()) {
                sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cA Configuration issue was detected! Please report to a server admin.");
                return false;
            }

            String formatted = formatTemplate
                    .replace("PLAYER", targetName)
                    .replace("BALANCE", String.format("%.2f", amount));

            sender.sendMessage(formatted);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§l§7[§bEconomyAPI§7] §r§cAn internal error occurred while updating the balance.");
            return false;
        }
    }
}

