package com.crecraftstudios.velocitycore.utils;

import com.crecraftstudios.velocitycore.VelocityCore;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Collection;
import java.util.Optional;

public class ServerUtils {
    public static RegisteredServer getRegisteredServer(String domain) {
        if (!VelocityCore.get().config().has("domains") || !VelocityCore.get().config().get("domains").isJsonObject())
            return null;

        JsonObject json = VelocityCore.get().config().getAsJsonObject("domains");
        if (!json.has(domain))
            return null;

        Optional<RegisteredServer> server = VelocityCore.get().proxy.getServer(json.get(domain).getAsString());
        return server.orElse(null);
    }

    public static void pingAllRegisteredServers() {
        Collection<RegisteredServer> servers = VelocityCore.get().proxy.getAllServers();

        for(RegisteredServer s : servers) {
            s.ping().whenComplete((ping, err)->{
                if (err!=null)
                    return;

                VelocityCore.get().serverIsOnline(s.getServerInfo().getName());
            });
        }
    }
}