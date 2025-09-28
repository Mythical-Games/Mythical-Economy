package com.mythicalgames.economy.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import com.mythicalgames.economy.EconomyAPI;

public class PayCommand extends Command {

    private final EconomyAPI plugin;

    public PayCommand(EconomyAPI plugin) {
        super("pay", "Send money to another player", "/pay <player> <amount>");
        this.setAliases(new String[]{"send", "givemoney"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getString("error-player-only"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getConfig().getString("usage-pay-money"));
            return true;
        }

        Player playerSender = (Player) sender;
        String senderUUID = playerSender.getUniqueId().toString();
        String targetName = args[0];
        String amountStr = args[1];

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfig().getString("error-invalid-amount"));
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.getConfig().getString("error-invalid-amount"));
            return true;
        }

        try {
            String targetUUID = EconomyAPI.getAPI().getUUIDByUsername(targetName);
            if (targetUUID == null || !EconomyAPI.getAPI().hasAccount(targetUUID)) {
                String notFound = plugin.getConfig().getString("player-not-found");
                if (notFound == null || notFound.isEmpty()) {
                    sender.sendMessage(plugin.getConfig().getString("error-config"));
                } else {
                    sender.sendMessage(notFound.replace("PLAYER", targetName));
                }
                return true;
            }

            if (senderUUID.equals(targetUUID)) {
                sender.sendMessage(plugin.getConfig().getString("error-pay-failure-1"));
                return true;
            }

            double senderBalance = EconomyAPI.getAPI().getBalance(senderUUID);
            if (senderBalance < amount) {
                sender.sendMessage(plugin.getConfig().getString("error-pay-failure-2"));
                return true;
            }

            EconomyAPI.getAPI().subtractBalance(senderUUID, amount);
            EconomyAPI.getAPI().addBalance(targetUUID, amount);

            sender.sendMessage("§l§7[§dMythical-Economy§7] §r§fYou sent §e" + amount + "§f to §b" + targetName + "§a.");

            Player targetPlayer = plugin.getServer().getPlayerExact(targetName);
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.sendMessage("§l§7[§dMythical-Economy§7] §r§fYou received §e" + amount + "§f from §b" + sender.getName() + "§a.");
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(plugin.getConfig().getString("error-internal"));
            return true;
        }
    }
}

