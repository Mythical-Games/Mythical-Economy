# Mythical-Economy

**An advanced economy plugin for PowerNukkitX with a rich API and robust database support!**

---

## Overview

Mythical-Economy is a powerful and extensible economy plugin designed for PowerNukkitX servers. It provides seamless integration with other plugins and supports both **SQLite** and **MongoDB** as backend databases.

Unlike many economy plugins that rely on player usernames, Mythical-Economy stores and manages player balances using their **UUIDs**. This approach ensures consistency and reliability even when players change their usernames, providing a stable and future-proof economy system for your server.

---

## Features

- **UUID-Based Economy**  
  All player data is stored and retrieved using universally unique identifiers (UUIDs) instead of usernames, preventing errors due to name changes or duplicates.

- **Multiple Database Support**  
  Choose between SQLite (local file-based) or MongoDB (cloud or networked) databases, allowing flexible data storage tailored to your server's needs.

- **Extensive API**  
  Offers a clean and straightforward API to allow other plugin developers to interact with the economy system effortlessly.

- **PowerNukkitX Compatible**  
  Built specifically for PowerNukkitX servers, leveraging the latest API for optimal performance and compatibility.

- **Command Suite**  
  Includes commands to check balances, add, reduce, and set money with proper permissions and aliases.

- **Configurable Messages**  
  Easily customize messages and outputs through the config file, supporting localization and personalized server branding.

---

## Installation

1. Download the latest Mythical-Economy `.jar` from the [Releases](https://github.com/Mythical-Games/Mythical-Economy/releases).

2. Place the `.jar` file in your server’s `plugins` directory.

3. Start the server to generate the default config file.

4. Configure your preferred database type (`SQLITE` or `MONGODB`) in `config.yml`.

---

## Configuration Highlights

```yaml
database-type: SQLITE # or MONGODB

# For MongoDB:
mongo-uri: "mongodb://username:password@host:port"
mongo-database-name: "yourDatabaseName"

# Customize messages:
player-not-found: "Player PLAYER was not found."
self-balance-output: "Your current balance is BALANCE."
player-balance-output: "Player PLAYER has BALANCE coins."
set-money-output: "Set PLAYER's balance to AMOUNT."
