package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.mythicalgames.economy.EconomyAPI;

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
            sender.sendMessage(plugin.getConfig().getString("error-player-only"));
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getConfig().getString("usage-set-money"));
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
                String notFoundMessage = plugin.getConfig().getString("player-not-found");
                if (notFoundMessage == null || notFoundMessage.isEmpty()) {
                    sender.sendMessage(plugin.getConfig().getString("error-config"));
                } else {
                    sender.sendMessage(notFoundMessage.replace("PLAYER", targetName));
                }
                return false;
            }

            EconomyAPI.getAPI().setBalance(uuid, amount);
            
            String formatTemplate = plugin.getConfig().getString("set-money-output");
            if (formatTemplate == null || formatTemplate.isEmpty()) {
                sender.sendMessage(plugin.getConfig().getString("error-config"));
                return false;
            }

            String formatted = formatTemplate
                    .replace("PLAYER", targetName)
                    .replace("BALANCE", String.format("%.2f", amount));

            sender.sendMessage(formatted);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(plugin.getConfig().getString("error-internal"));
            return false;
        }
    }
}

