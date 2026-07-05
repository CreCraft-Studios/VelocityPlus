package com.crecraftstudios.velocityplus.events;

import com.crecraftstudios.velocityplus.QueueManager;
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

        String requestedServerName = event.getOriginalServer().getServerInfo().getName();
        if (!VelocityPlus.get().isServerOnline(requestedServerName)) {
            Optional<RegisteredServer> fallback = VelocityPlus.get().proxy.getServer(VelocityPlus.get().config().get("fallback").getAsString());

            fallback.ifPresent(server ->{
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server));
                VelocityPlus.get().queueManager.addQueue(new QueueManager.Queue(event.getPlayer(), event.getOriginalServer()));
                VelocityPlus.get().queueManager.startServer(requestedServerName);
            });
        }
    }
}