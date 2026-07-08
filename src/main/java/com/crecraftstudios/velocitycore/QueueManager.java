package com.crecraftstudios.velocitycore;

import com.crecraftstudios.velocitycore.api.event.server.RequestStartServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.*;

public class QueueManager {
    private final HashMap<String, ArrayList<Queue>> queuedPlayers = new HashMap<>();
    private final HashMap<String, RegisteredServer> queuedServers = new HashMap<>();

    public void addQueue(Queue queue) {
        if (this.queuedPlayers.containsKey(queue.server.getServerInfo().getName()))
            this.queuedPlayers.get(queue.server.getServerInfo().getName()).add(queue);
        else this.queuedPlayers.put(queue.server.getServerInfo().getName(), new ArrayList<>(Arrays.asList(queue)));
    }

    /**Call this method when VelocityPlus gets an online status note from the server*/
    public void moveNeededPlayers(String serverName) {
        if (!this.queuedPlayers.containsKey(serverName))
            return;

        for(Queue queue : this.queuedPlayers.get(serverName)) {
            queue.player.createConnectionRequest(queue.server);
        }

        this.queuedPlayers.remove(serverName);
        this.queuedServers.remove(serverName);
    }

    public void startServer(String serverName) {
        if (this.queuedServers.containsKey(serverName))
            return;

        Optional<RegisteredServer> server = VelocityCore.get().proxy.getServer(serverName);
        if (server.isEmpty()) {
            VelocityCore.get().logger.error("Can't find registered server {}, can't fire event", serverName);
            return;
        }

        VelocityCore.get().proxy.getEventManager().fire(new RequestStartServerEvent(server.get())).thenAccept((e)->{
            if (!e.isCancelled())
                this.queuedServers.put(serverName, server.get());
        });
    }

    public record Queue(Player player, RegisteredServer server) {}
}