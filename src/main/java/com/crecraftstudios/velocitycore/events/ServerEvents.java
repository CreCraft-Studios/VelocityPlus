package com.crecraftstudios.velocitycore.events;

import com.crecraftstudios.velocitycore.QueueManager;
import com.crecraftstudios.velocitycore.VelocityCore;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class ServerEvents {

    @Subscribe
    public void onServerPreConnection(ServerPreConnectEvent event) {
        if (event.getPreviousServer()!=null)
            return;

        String requestedServerName = event.getOriginalServer().getServerInfo().getName();
        if (!VelocityCore.get().isServerOnline(requestedServerName)) {
            Optional<RegisteredServer> fallback = VelocityCore.get().proxy.getServer(VelocityCore.get().config().get("fallback").getAsString());

            fallback.ifPresent(server ->{
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server));
                VelocityCore.get().queueManager.addQueue(new QueueManager.Queue(event.getPlayer(), event.getOriginalServer()));
                VelocityCore.get().queueManager.startServer(requestedServerName);
            });
        }
    }
}