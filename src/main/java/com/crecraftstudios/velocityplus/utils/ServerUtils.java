package com.crecraftstudios.velocityplus.utils;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Collection;
import java.util.Optional;

public class ServerUtils {
    public static RegisteredServer getRegisteredServer(String domain) {
        if (!VelocityPlus.get().config().has("domains") || !VelocityPlus.get().config().get("domains").isJsonObject())
            return null;

        JsonObject json = VelocityPlus.get().config().getAsJsonObject("domains");
        if (!json.has(domain))
            return null;

        Optional<RegisteredServer> server = VelocityPlus.get().proxy.getServer(json.get(domain).getAsString());
        return server.orElse(null);
    }

    public static void pingAllRegisteredServers() {
        Collection<RegisteredServer> servers = VelocityPlus.get().proxy.getAllServers();

        for(RegisteredServer s : servers) {
            s.ping().whenComplete((ping, err)->{
                if (err!=null)
                    return;

                VelocityPlus.get().serverIsOnline(s.getServerInfo().getName());
            });
        }
    }
}