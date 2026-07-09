# Velocity Core

> A powerful all-in-one plugin for Velocity proxies.

Velocity Core is designed to simplify network administration by moving common proxy-wide functionality into a single plugin. Instead of maintaining multiple plugins across every backend server, Velocity Core provides centralized management directly from the proxy.

## Features

- Network-wide alerts
- Proxy-level bans
- Global whitelist
- Maintenance mode
- Cross-server private messaging
- Find players across the network
- Hub/Lobby system
- Runtime server registration
- Developer API
- Lightweight and easy to integrate

---

# Commands

| Command | Description |
|---------|-------------|
| `/alert <message>` | Broadcast a message to every player connected to the proxy. |
| `/ban <player> <reason>` | Ban a player across the entire network at the proxy level. |
| `/unban <player>` | Remove a player's ban. |
| `/global-whitelist enable\|disable` | Enable or disable the global whitelist. |
| `/global-whitelist add <player>` | Add a player to the whitelist. |
| `/global-whitelist remove <player>` | Remove a player from the whitelist. |
| `/global-whitelist list` | Display all currently whitelisted players. |
| `/find <player>` | Find which backend server a player is connected to. |
| `/hub` | Send yourself to the configured lobby server. |
| `/maintenance enable [time]` | Enable maintenance mode. Optional countdown (5–120 seconds). |
| `/maintenance disable` | Disable maintenance mode. |
| `/pmsg <player> <message>` | Send a private message to a player anywhere on the network. |
| `/register-server <name> <address> <port>` | Register a backend server without editing `velocity.toml`. |
| `/unregister-server <name>` | Remove a registered backend server. |
| `/set-lobby [server]` | Set the current server or a specified server as the lobby. |

> **Note**
>
> Servers registered using `/register-server` exist **only in memory**. They are **not persisted** after the proxy shuts down or restarts.

---

# Permissions

| Permission | Description |
|------------|-------------|
| `velocitycore.admin.alert` | Use `/alert` |
| `velocitycore.admin.ban.perm` | Use `/ban` |
| `velocitycore.admin.unban.perm` | Use `/unban` |
| `velocitycore.admin.whitelist` | Manage the global whitelist |
| `velocitycore.find` | Use `/find` |
| `velocitycore.hub` | Use `/hub` |
| `velocitycore.admin.maintenance` | Manage maintenance mode |
| `velocitycore.admin.msg` | Send private messages |
| `velocitycore.admin.server.register` | Register backend servers |
| `velocitycore.admin.server.unregister` | Unregister backend servers |
| `velocitycore.admin.setlobby` | Configure the lobby server |
| `velocitycore.admin.enter-on-maintenance` | Bypass maintenance mode and remain connected |

---

# Maintenance Mode

When maintenance mode is enabled:

- Players without permission cannot join.
- Players already online without permission are disconnected.
- Administrators with the appropriate permission remain connected.
- An optional countdown between **5** and **120** seconds can be specified before maintenance begins.

---

# Developer API

Velocity Core includes a lightweight API for plugin developers.

## Getting the API

```java
// Package:
// com.crecraftstudios.velocitycore.api

VelocityCoreAPI api = VelocityCoreAPI.get();
```

---

## Ban Service

Currently the API exposes the `BanService`.

```java
VelocityCoreAPI api = VelocityCoreAPI.get();

BanService banService = api.getBanService();

banService.ban(
    "Steve",
    "Console",
    "You have been banned."
);
```

The `ban()` and `unban()` methods accept:

- `Player`
- `UUID`
- `String` (player name)

---

# Events

Velocity Core currently provides the following events.

## `PlayerBannedEvent`

Called whenever a player is banned.

You may modify:

- Ban source
- Ban reason

The target player cannot be changed.

---

## `RequestStartServerEvent`

Fired when a player attempts to connect to a backend server that is currently offline.

Velocity Core **does not** start servers automatically.

Instead, this event allows another plugin to:

1. Detect the request.
2. Start the backend server.
3. Allow Velocity Core to automatically connect the waiting player once the server becomes available.

---

# Why Velocity Core?

Instead of relying on multiple plugins synchronized across every backend server, Velocity Core centralizes network management at the proxy level.

This means:

- Less configuration
- Fewer plugins to maintain
- Easier administration
- Cleaner network architecture
- A simple API for developers

---

# License

This project is licensed under the **GUNI General Public License v3.0**.

See the `LICENSE` file for full details.

---

**Copyright © 2026 CreCraft Studios**