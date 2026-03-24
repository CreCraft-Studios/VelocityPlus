package com.crecraftstudios.velocityplus.events;

import com.crecraftstudios.velocityplus.json.Messages;
import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.google.gson.JsonObject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;

import java.util.Locale;
import java.util.Objects;

public class LoginEvents {
    @Subscribe
    public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        event.getPlayer().getVirtualHost().ifPresent(address -> {
            if (VelocityPlus.get().config().has("root_domain")) {
                if (Objects.equals(VelocityPlus.get().config().get("root_domain").getAsString(), address.getHostString().toLowerCase(Locale.ROOT)))
                    return;
            }

            if (!VelocityPlus.get().config().has("domains") && !VelocityPlus.get().config().get("domains").isJsonObject())
                return;

            JsonObject hosts = VelocityPlus.get().config().getAsJsonObject("domains");

            String domain = address.getHostString().toLowerCase(Locale.ROOT);
            if (!hosts.has(domain))
                return;

            String serverName = hosts.get(domain).getAsString();
            if (!VelocityPlus.get().isServerOnline(serverName)) {
                if (VelocityPlus.get().config().has("fallback"))
                    VelocityPlus.get().proxy.getServer(VelocityPlus.get().config().get("fallback").getAsString()).ifPresent(event::setInitialServer);
            } else VelocityPlus.get().proxy.getServer(serverName).ifPresent(event::setInitialServer);
        });
    }

    @Subscribe
    public void onLoginRequest(LoginEvent event) {
        if (!VelocityPlus.get().whitelist.isWhitelisted(event.getPlayer().getUniqueId()))
            event.setResult(ResultedEvent.ComponentResult.denied(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.NOT_WHITELISTED)));

        if (VelocityPlus.get().bans.isBanned(event.getPlayer().getUniqueId()))
            event.setResult(ResultedEvent.ComponentResult.denied(VelocityPlus.get().bans.getBanMessage(event.getPlayer().getUniqueId())));
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        if (VelocityPlus.get().inMaintenanceMode() && !event.getPlayer().hasPermission(Permissions.CONNECT_IN_MAINTENANCE))
            event.getPlayer().disconnect(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_CURRENT));
    }
}