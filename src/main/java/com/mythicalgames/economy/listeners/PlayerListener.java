package com.mythicalgames.economy.listeners;

import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerJoinEvent;
import org.allaymc.api.entity.interfaces.EntityPlayer;

import com.mythicalgames.economy.MythicalEconomy;

import java.util.UUID;

public class PlayerListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        EntityPlayer player = event.getPlayer();
        String playerUsername = player.getOriginName();
        UUID playerUUID = player.getLoginData().getUuid();

        MythicalEconomy.getAPI().hasAccount(playerUUID).thenAccept(hasAccount -> {
            if (!hasAccount) {

                double defaultMoney = MythicalEconomy.getInstance().config.default_balance;

                if (defaultMoney == 0.0) {
                    MythicalEconomy.getInstance().getPluginLogger().error(MythicalEconomy.getInstance().config.error_config);
                    player.sendMessage(MythicalEconomy.getInstance().config.error_config);
                    return;
                }

                String formatTemplate = MythicalEconomy.getInstance().config.new_player_notify;

                if (formatTemplate == null || formatTemplate.isEmpty()) {
                    MythicalEconomy.getInstance().getPluginLogger().error(MythicalEconomy.getInstance().config.error_config);
                    player.sendMessage(MythicalEconomy.getInstance().config.error_config);
                    return;
                }

                MythicalEconomy.getAPI().createAccount(playerUUID, playerUsername, defaultMoney)
                        .thenAccept(created -> {
                            if (created) {
                                String message = formatTemplate
                                        .replace("PLAYER", playerUsername)
                                        .replace("BALANCE", String.valueOf(defaultMoney));
                                player.sendMessage(message);
                            } else {
                                MythicalEconomy.getInstance().getPluginLogger().error(
                                        "Failed to create account for player " + playerUsername + " (" + playerUUID + ")");
                                player.sendMessage(MythicalEconomy.getInstance().config.error_account_creation);
                            }
                        }).exceptionally(ex -> {
                            MythicalEconomy.getInstance().getPluginLogger().error(
                                    "An error occurred while creating account for " + playerUsername + " (" + playerUUID + "): " + ex.getMessage());
                            player.sendMessage(MythicalEconomy.getInstance().config.error_internal);
                            return null;
                        });
            }
        }).exceptionally(ex -> {
            MythicalEconomy.getInstance().getPluginLogger().error(
                    "An error occurred while checking account for " + playerUsername + " (" + playerUUID + "): " + ex.getMessage());
            player.sendMessage(MythicalEconomy.getInstance().config.error_internal);
            return null;
        });
    }
}
