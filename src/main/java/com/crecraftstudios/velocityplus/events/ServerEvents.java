package com.crecraftstudios.velocityplus.events;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class ServerEvents {

    @Subscribe
    public void onServerPreConnection(ServerPreConnectEvent event) {
        if (event.getPreviousServer()!=null)
            return;

        String name = event.getOriginalServer().getServerInfo().getName();
        if (!VelocityPlus.get().isServerOnline(name)) {
            Optional<RegisteredServer> fallback = VelocityPlus.get().proxy.getServer(VelocityPlus.get().config().get("fallback").getAsString());

            fallback.ifPresent(server ->{
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server));
            });
        }
    }
}