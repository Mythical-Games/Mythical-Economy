package com.mythicalgames.economy.events;

import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.EventHandler;
import cn.nukkit.Player;

import com.mythicalgames.economy.EconomyAPI;
import com.mythicalgames.economy.database.DatabaseHandler;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerUsername = player.getName();
        String playerUUID = player.getUniqueId().toString();

        try {
            DatabaseHandler database = EconomyAPI.getInstance().getDatabase();

            // Check if the player has an account using UUID
            if (!database.hasAccount(playerUUID)) {

                double defaultMoney = EconomyAPI.getInstance().getConfig().getDouble("default-balance", 0.0);

                if (defaultMoney == 0.0) {
                    EconomyAPI.getInstance().getLogger().warning("DEFAULT-BALANCE is not set. Please configure ECONOMY-SETTINGS");
                    player.sendMessage("§c[EconomyAPI] Default balance is not configured. Please contact an administrator.");
                    return;
                }

                // Get the message format from the config
                String formatTemplate = EconomyAPI.getInstance().getConfig().getString("new-player-notify", "");

                if (formatTemplate == null || formatTemplate.isEmpty()) {
                    EconomyAPI.getInstance().getLogger().warning("NEW PLAYER NOTIFY OUTPUT format not set in the config");
                    player.sendMessage("§c[EconomyAPI] New player notify message format is not set. Please contact an administrator.");
                    return;
                }

                // Create the account with UUID and username
                boolean created = database.createAccount(playerUUID, playerUsername, defaultMoney);

                if (created) {
                    // Replace placeholders with actual values
                    String message = formatTemplate
                        .replace("PLAYER", playerUsername)
                        .replace("BALANCE", String.valueOf(defaultMoney));

                    player.sendMessage(message);
                } else {
                    EconomyAPI.getInstance().getLogger().error("Failed to create account for player " + playerUsername + " (" + playerUUID + ")");
                    player.sendMessage("§c[EconomyAPI] Failed to create your economy account. Please contact an administrator.");
                }
            }

        } catch (Exception e) {
            EconomyAPI.getInstance().getLogger().error("An error occurred while processing player join for " + playerUsername + " (" + playerUUID + "): " + e.getMessage());
            player.sendMessage("§c[EconomyAPI] An unexpected error occurred. Please contact an administrator.");
        }
    }
}
