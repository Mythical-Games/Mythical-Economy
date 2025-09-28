package com.mythicalgames.economy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class EconomyInfoCommand extends Command {

    public EconomyInfoCommand() {
        super("economy", "Displays information about the economy plugin", "/economy");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("==§l§7[§b EconomyAPI §7] ==");
        sender.sendMessage("§eThis server is running §dMythical-Economy Version 1.0.0-SNAPSHOT.");
        sender.sendMessage("§eJoin our discord server at §bdiscord.gg/KX5QSBUQJq");
        return true;
    }
}

