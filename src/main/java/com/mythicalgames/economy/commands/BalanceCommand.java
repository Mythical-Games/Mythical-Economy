package com.mythicalgames.economy.commands;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.permission.OpPermissionCalculator;

import com.mythicalgames.economy.MythicalEconomy;

import java.util.UUID;

public class BalanceCommand extends Command {

    public BalanceCommand() {
        super("balance", "Display your balance", "mythical.economy.bal");
        this.aliases.add("bal");
        OpPermissionCalculator.NON_OP_PERMISSIONS.addAll(this.permissions);
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

                UUID senderUUID = sender.getController().getLoginData().getUuid();

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
                                "Error fetching balance for " + sender.getController().getOriginName() + ": " + ex.getMessage());
                        return null;
                    });

                return context.success();
            });
    }
}
