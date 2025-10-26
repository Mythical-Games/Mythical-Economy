package com.mythicalgames.economy.commands;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.permission.Permission;

import com.mythicalgames.economy.MythicalEconomy;

import java.util.List;
import java.util.UUID;

public class SetMoneyCommand extends Command {

    private final MythicalEconomy plugin;

    public SetMoneyCommand(MythicalEconomy plugin) {
        super("setmoney", "Set a player's balance");
        this.plugin = plugin;
        this.aliases.add("sm");
        getPermissions().add(Permission.create("mythical.economy.set"));
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
            .playerTarget("player")
            .doubleNum("amount")
            .exec(context -> {
                    EntityPlayer sender = context.getSender().asPlayer();

                    if (sender == null && !context.getSender().isOperator()) {
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

                    if (amount < 0) {
                        context.getSender().sendMessage(plugin.config.error_invalid_amount);
                        return context.fail();
                    }

                    UUID targetUUID = target.getLoginData().getUuid();

                    MythicalEconomy.getAPI().hasAccount(targetUUID).thenAccept(hasAccount -> {
                        if (!hasAccount) {
                            context.getSender().sendMessage(plugin.config.player_not_found.replace("PLAYER", targetName));
                            return;
                        }

                        MythicalEconomy.getAPI().setBalance(targetUUID, amount).thenAccept(v -> {
                            String formatted = plugin.config.set_money_output
                                    .replace("PLAYER", targetName)
                                    .replace("BALANCE", String.format("%.2f", amount));
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
                
                return context.success();
            });
    }
}
