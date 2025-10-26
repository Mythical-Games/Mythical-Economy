package com.mythicalgames.economy.commands;

import java.util.List;
import java.util.UUID;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.permission.PermissionGroups;
import org.allaymc.api.server.Server;

import com.mythicalgames.economy.MythicalEconomy;
import com.mythicalgames.economy.Config;

public class PayCommand extends Command {

    private final Config config;

    public PayCommand(MythicalEconomy plugin) {
        super("pay", "Send money to another player");
        getPermissions().forEach(PermissionGroups.MEMBER::addPermission);
        this.config = plugin.config;
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
            .playerTarget("player")
            .doubleNum("amount")
            .exec(context -> {
                    EntityPlayer sender = context.getSender().asPlayer();

                    if (sender == null) {
                        context.getSender().sendMessage(config.error_player_only);
                        return context.fail();
                    }

                    List<EntityPlayer> targets = context.getResult(0);
                    if (targets.isEmpty()) {
                        sender.sendMessage(config.player_not_found.replace("PLAYER", "unknown"));
                        return context.fail();
                    }

                    EntityPlayer target = targets.get(0);
                    String targetName = target.getOriginName();

                    double amount = context.getResult(1);
                    if (amount <= 0) {
                        sender.sendMessage(config.error_invalid_amount);
                        return context.fail();
                    }

                    UUID senderUUID = sender.getLoginData().getUuid();
                    UUID targetUUID = target.getLoginData().getUuid();

                    if (senderUUID.equals(targetUUID)) {
                        sender.sendMessage(config.error_pay_failure_1);
                        return context.fail();
                    }

                    MythicalEconomy.getAPI().hasAccount(targetUUID).thenAccept(hasAccount -> {
                        if (!hasAccount) {
                            sender.sendMessage(config.player_not_found.replace("PLAYER", targetName));
                            return;
                        }

                        MythicalEconomy.getAPI().getBalance(senderUUID).thenAccept(senderBalance -> {
                            if (senderBalance < amount) {
                                sender.sendMessage(config.error_pay_failure_2);
                                return;
                            }

                            MythicalEconomy.getAPI().subtractBalance(senderUUID, amount)
                                .thenCompose(v -> MythicalEconomy.getAPI().addBalance(targetUUID, amount))
                                .thenAccept(v -> {
                                    sender.sendMessage("§l§7[§dMythical-Economy§7] §r§fYou sent §e" + amount + "$ §fto §b" + targetName + "§a.");

                                    EntityPlayer targetPlayer = Server.getInstance()
                                        .getPlayerManager()
                                        .getOnlinePlayerByName(targetName);
                                    if (targetPlayer != null) {
                                        targetPlayer.sendMessage("§l§7[§dMythical-Economy§7] §r§fYou received §e" + amount + "$ §ffrom §b" + sender.getOriginName() + "§a.");
                                    }
                                })
                                .exceptionally(ex -> {
                                    ex.printStackTrace();
                                    sender.sendMessage(config.error_internal);
                                    return null;
                                });
                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            sender.sendMessage(config.error_internal);
                            return null;
                        });
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        sender.sendMessage(config.error_internal);
                        return null;
                    });

                return context.success();
            });
    }
}
