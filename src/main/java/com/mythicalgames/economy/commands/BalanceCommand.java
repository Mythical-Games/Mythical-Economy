package com.mythicalgames.economy.commands;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.permission.PermissionGroups;

import com.mythicalgames.economy.MythicalEconomy;

import java.util.UUID;

public class BalanceCommand extends Command {

    public BalanceCommand() {
        super("balance", "Display your balance");
        this.aliases.add("bal");
        getPermissions().forEach(PermissionGroups.MEMBER::addPermission);
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
            .exec(context -> {
                EntityPlayer sender = context.getSender().asPlayer();
                if (sender == null) {
                    context.addOutput("§cYou must be a player to run this command.");
                    return context.fail();
                }

                UUID senderUUID = sender.getLoginData().getUuid();

                MythicalEconomy.getAPI().getBalance(senderUUID)
                    .thenAccept(balance -> {
                        String template = MythicalEconomy.getInstance().config.self_balance_output;
                        if (template == null || template.isEmpty()) {
                            sender.sendMessage(MythicalEconomy.getInstance().config.error_config);
                            return;
                        }
                        sender.sendMessage(template.replace("BALANCE", String.format("%.2f", balance)));
                    }).exceptionally(ex -> {
                        sender.sendMessage(MythicalEconomy.getInstance().config.error_internal);
                        MythicalEconomy.getInstance().getPluginLogger().error(
                                "Error fetching balance for " + sender.getOriginName() + ": " + ex.getMessage());
                        return null;
                    });

                return context.success();
            });
    }
}
