package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.mythicalgames.economy.EconomyAPI;
public class AddMoneyCommand extends Command {

    private final EconomyAPI plugin;

    public AddMoneyCommand(EconomyAPI plugin) {
        super("addmoney", "Add money to a player's balance", "/addmoney <player> <amount>");
        this.setAliases(new String[]{"am"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer() && !sender.isOp()) {
            sender.sendMessage(plugin.getConfig().getString("error-player-only"));
            return false;
        }
        
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfig().getString("usage-add-money"));
            return false;
        }

        String targetName = args[0];
        String amountStr = args[1];

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfig().getString("error-invalid-amount"));
            return false;
        }

        if (amount < 0) {
            sender.sendMessage(plugin.getConfig().getString("error-invalid-amount"));
            return false;
        }

        try {
            String uuid = EconomyAPI.getAPI().getUUIDByUsername(targetName);
            if (uuid == null || !EconomyAPI.getAPI().hasAccount(uuid)) {
                String notFound = plugin.getConfig().getString("player-not-found");
                if (notFound == null || notFound.isEmpty()) {
                    sender.sendMessage(plugin.getConfig().getString("error-config"));
                } else {
                    sender.sendMessage(notFound.replace("PLAYER", targetName));
                }
                return false;
            }

            EconomyAPI.getAPI().addBalance(uuid, amount);

            String formatTemplate = plugin.getConfig().getString("add-money-output");
            if (formatTemplate == null || formatTemplate.isEmpty()) {
                sender.sendMessage(plugin.getConfig().getString("error-config"));
                return false;
            }

            String formatted = formatTemplate
                    .replace("PLAYER", targetName)
                    .replace("AMOUNT", String.valueOf(amount));

            sender.sendMessage(formatted);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(plugin.getConfig().getString("error-internal"));
            return false;
        }
    }
}

