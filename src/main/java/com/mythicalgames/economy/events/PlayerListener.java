package com.mythicalgames.economy.events;

import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.EventHandler;
import cn.nukkit.Player;

import com.mythicalgames.economy.EconomyAPI;
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUsername = player.getName();
        String playerUUID = player.getUniqueId().toString();

        try {
            // Check if the player has an account using UUID
            if (!EconomyAPI.getAPI().hasAccount(playerUUID)) {

                double defaultMoney = EconomyAPI.getInstance().getConfig().getDouble("default-balance", 0.0);

                if (defaultMoney == 0.0) {
                    EconomyAPI.getInstance().getLogger().warning(EconomyAPI.getInstance().getConfig().getString("error-config"));
                    player.sendMessage(EconomyAPI.getInstance().getConfig().getString("error-config"));
                    return;
                }

                // Get the message format from the config
                String formatTemplate = EconomyAPI.getInstance().getConfig().getString("new-player-notify", "");

                if (formatTemplate == null || formatTemplate.isEmpty()) {
                    EconomyAPI.getInstance().getLogger().warning(EconomyAPI.getInstance().getConfig().getString("error-config"));
                    player.sendMessage(EconomyAPI.getInstance().getConfig().getString("error-config"));
                    return;
                }

                // Create the account with UUID and username
                boolean created = EconomyAPI.getAPI().createAccount(playerUUID, playerUsername, defaultMoney);

                if (created) {
                    // Replace placeholders with actual values
                    String message = formatTemplate
                        .replace("PLAYER", playerUsername)
                        .replace("BALANCE", String.valueOf(defaultMoney));

                    player.sendMessage(message);
                } else {
                    EconomyAPI.getInstance().getLogger().error("Failed to create account for player " + playerUsername + " (" + playerUUID + ")");
                    player.sendMessage(EconomyAPI.getInstance().getConfig().getString("error-account-creation"));
                }
            }

        } catch (Exception e) {
            EconomyAPI.getInstance().getLogger().error("An error occurred while processing player join for " + playerUsername + " (" + playerUUID + "): " + e.getMessage());
            player.sendMessage(EconomyAPI.getInstance().getConfig().getString("error-internal"));
        }
    }
}
