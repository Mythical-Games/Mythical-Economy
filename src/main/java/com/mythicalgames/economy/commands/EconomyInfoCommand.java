package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;

public class EconomyInfoCommand extends Command {

    public EconomyInfoCommand() {
        super("economy", "Displays information about the economy plugin", "/economy");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        sender.sendMessage("§l§7[§bEconomyAPI§7] §r§aWelcome to the Economy System!");
        sender.sendMessage("§eThis server is running §dMythical-Economy Version 1.0.0-SNAPSHOT.");
        sender.sendMessage("§eJoin our discord server at §bdiscord.gg/KX5QSBUQJq");
        sender.sendMessage("§7Plugin developed by Acktar and the Mythical Games Team");

        return true;
    }
}

