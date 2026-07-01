package com.crecraftstudios.velocityplus;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class QueueManager {
    private HashMap<String, ArrayList<Queue>> queuedServers = new HashMap<>();

    public void addQueue(Queue queue) {
        if (this.queuedServers.containsKey(queue.server.getServerInfo().getName()))
            this.queuedServers.get(queue.server.getServerInfo().getName()).add(queue);
        else this.queuedServers.put(queue.server.getServerInfo().getName(), new ArrayList<>(Arrays.asList(queue)));
    }

    public void moveNeededPlayers(String serverName) {
        if (!this.queuedServers.containsKey(serverName))
            return;

        for(Queue queue : this.queuedServers.get(serverName)) {
            queue.player.createConnectionRequest(queue.server);
        }

        this.queuedServers.remove(serverName);
    }

    public static class Queue {
        public final Player player;
        public final RegisteredServer server;

        public Queue(Player player, RegisteredServer server) {
            this.player=player;
            this.server=server;
        }
    }
}