package com.mythicalgames.economy.commands;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.permission.Permission;

import com.mythicalgames.economy.MythicalEconomy;

import java.util.List;
import java.util.UUID;

public class AddMoneyCommand extends Command {

    private final MythicalEconomy plugin;

    public AddMoneyCommand(MythicalEconomy plugin) {
        super("addmoney", "Add money to a player's balance");
        this.plugin = plugin;
        this.aliases.add("am");
        getPermissions().add(Permission.create("mythical.economy.add"));
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
            .playerTarget("Player")
            .doubleNum("amount")
            .exec(context -> {
                EntityPlayer sender = context.getSender().asPlayer();

                if (sender == null && !context.getSender().isOperator()) {
                    context.addOutput(plugin.config.error_player_only);
                    return context.fail();
                }

                List<EntityPlayer> targets = context.getResult(0);
                if (targets.isEmpty()) {
                    if (sender != null) sender.sendMessage(plugin.config.player_not_found.replace("PLAYER", "unknown"));
                    return context.fail();
                }

                EntityPlayer target = targets.get(0);
                String targetName = target.getOriginName();
                double amount = context.getResult(1);

                if (amount <= 0) {
                    if (sender != null) sender.sendMessage(plugin.config.error_invalid_amount);
                    return context.fail();
                }

                UUID targetUUID = target.getLoginData().getUuid();

                MythicalEconomy.getAPI().hasAccount(targetUUID)
                    .thenAccept(hasAccount -> {
                        if (!hasAccount) {
                            if (sender != null)
                                sender.sendMessage(plugin.config.player_not_found.replace("PLAYER", targetName));
                                context.fail();
                                return;
                            }

                MythicalEconomy.getAPI().addBalance(targetUUID, amount)
                    .thenAccept(v -> {
                        String formatted = plugin.config.add_money_output
                            .replace("PLAYER", targetName)
                            .replace("AMOUNT", String.format("%.2f", amount));
                        if (sender != null) sender.sendMessage(formatted);
                            context.success();
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        if (sender != null) sender.sendMessage(plugin.config.error_internal);
                            context.fail();
                            return null;
                        });

                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        if (sender != null) sender.sendMessage(plugin.config.error_internal);
                            context.fail();
                            return null;
                        });

                return context.success();
            });
    }
}
