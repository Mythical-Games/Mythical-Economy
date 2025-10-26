package com.mythicalgames.economy.commands;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.permission.Permission;

import com.mythicalgames.economy.MythicalEconomy;

import java.util.List;
import java.util.UUID;

public class ReduceMoneyCommand extends Command {

    private final MythicalEconomy plugin;

    public ReduceMoneyCommand(MythicalEconomy plugin) {
        super("reducemoney", "Reduces money from a player's balance");
        this.aliases.add("rm");
        this.plugin = plugin;
        getPermissions().add(Permission.create("mythical.economy.remove"));
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
            .playerTarget("player")
            .doubleNum("amount")
            .exec(context -> {
                EntityPlayer senderPlayer = context.getSender().asPlayer();

                if (senderPlayer == null && !context.getSender().isOperator()) {
                    context.getSender().sendMessage(plugin.config.error_player_only);
                    return context.fail();
                }

                List<EntityPlayer> targets = context.getResult(0);
                if (targets.isEmpty()) {
                    context.getSender().sendMessage(plugin.config.player_not_found.replace("PLAYER", "unknown"));
                    return context.fail();
                }

                EntityPlayer target = targets.get(0);
                String targetName = target.getOriginName();
                double amount = context.getResult(1);

                if (amount <= 0) {
                    context.getSender().sendMessage(plugin.config.error_invalid_amount);
                    return context.fail();
                }

                MythicalEconomy.getAPI().getUUIDByUsername(targetName).thenAccept(targetUUIDStr -> {
                    if (targetUUIDStr == null) {
                        context.getSender().sendMessage(plugin.config.player_not_found.replace("PLAYER", targetName));
                        return;
                    }

                    UUID targetUUID = UUID.fromString(targetUUIDStr);

                    MythicalEconomy.getAPI().hasAccount(targetUUID).thenAccept(hasAccount -> {
                        if (!hasAccount) {
                            context.getSender().sendMessage(plugin.config.player_not_found.replace("PLAYER", targetName));
                            return;
                        }

                        MythicalEconomy.getAPI().subtractBalance(targetUUID, amount).thenAccept(success -> {
                            if (!success) {
                                context.getSender().sendMessage(plugin.config.error_pay_failure_2);
                                return;
                            }

                            String formatted = plugin.config.reduce_money_output
                                    .replace("PLAYER", targetName)
                                    .replace("AMOUNT", String.format("%.2f", amount));
                            context.getSender().sendMessage(formatted);

                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            context.getSender().sendMessage(plugin.config.error_internal);
                            return null;
                        });

                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        context.getSender().sendMessage(plugin.config.error_internal);
                        return null;
                    });

                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    context.getSender().sendMessage(plugin.config.error_internal);
                    return null;
                });
                
                return context.success();
            });
    }
}



