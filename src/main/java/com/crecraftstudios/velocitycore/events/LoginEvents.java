package com.crecraftstudios.velocitycore.events;

import com.crecraftstudios.velocitycore.api.VelocityCoreAPI;
import com.crecraftstudios.velocitycore.json.Messages;
import com.crecraftstudios.velocitycore.Permissions;
import com.crecraftstudios.velocitycore.VelocityCore;
import com.google.gson.JsonObject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;

import java.util.Locale;
import java.util.Objects;

public class LoginEvents {
    @Deprecated(forRemoval = true)
    public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        event.getPlayer().getVirtualHost().ifPresent(address -> {
            if (VelocityCore.get().config().has("root_domain")) {
                if (Objects.equals(VelocityCore.get().config().get("root_domain").getAsString(), address.getHostString().toLowerCase(Locale.ROOT)))
                    return;
            }

            if (!VelocityCore.get().config().has("domains") && !VelocityCore.get().config().get("domains").isJsonObject())
                return;

            JsonObject hosts = VelocityCore.get().config().getAsJsonObject("domains");

            String domain = address.getHostString().toLowerCase(Locale.ROOT);
            if (!hosts.has(domain))
                return;

            String serverName = hosts.get(domain).getAsString();
            if (!VelocityCore.get().isServerOnline(serverName)) {
                if (VelocityCore.get().config().has("fallback"))
                    VelocityCore.get().proxy.getServer(VelocityCore.get().config().get("fallback").getAsString()).ifPresent(event::setInitialServer);
            } else VelocityCore.get().proxy.getServer(serverName).ifPresent(event::setInitialServer);
        });
    }

    @Subscribe
    public void onLoginRequest(LoginEvent event) {
        if (!VelocityCore.get().whitelist.isWhitelisted(event.getPlayer().getUniqueId()))
            event.setResult(ResultedEvent.ComponentResult.denied(VelocityCore.get().messages.getMessage(Messages.Keys.Message.NOT_WHITELISTED)));

        if (VelocityCoreAPI.get().getBanService().isBanned(event.getPlayer()))
            event.setResult(ResultedEvent.ComponentResult.denied(VelocityCoreAPI.get().getBanService().getBanMessage(event.getPlayer())));
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        if (VelocityCore.get().inMaintenanceMode() && !event.getPlayer().hasPermission(Permissions.CONNECT_IN_MAINTENANCE))
            event.getPlayer().disconnect(VelocityCore.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_CURRENT));
    }
}