package com.crecraftstudios.velocityplus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

        Optional<RegisteredServer> server = VelocityPlus.get().proxy.getServer(serverName);
        if (server.isEmpty()) {
            VelocityPlus.get().logger.error("Can't start server {} as it's not found", serverName);
            return;
        }

        JsonArray containerArray = VelocityPlus.get().config().get("docker").getAsJsonArray();

        containerArray.forEach(con->{
            JsonObject obj = con.getAsJsonObject();

            if (!Objects.equals(obj.get("server_name").getAsString(), serverName))
                return;

            this.queuedServers.put(serverName, server.get());

        });
    }

    public record Queue(Player player, RegisteredServer server) {}
}