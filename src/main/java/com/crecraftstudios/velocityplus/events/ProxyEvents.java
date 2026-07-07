package com.crecraftstudios.velocityplus.events;

import com.crecraftstudios.velocityplus.json.Messages;
import com.crecraftstudios.velocityplus.ServerDetails;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.utils.ServerUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.Optional;

public class ProxyEvents {
    //private final HashMap<String, ServerDetails> cacheServers = new HashMap<>();

    /**Velocity already has this feature, no need to reinvent the wheel*/
    @Deprecated(forRemoval = true)
    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();
        ServerPing.Builder builder = ping.asBuilder();

        if (VelocityPlus.get().inMaintenanceMode()) {
            builder.description(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_CURRENT_PING));
            builder.maximumPlayers(0);
            event.setPing(builder.build());
        } else event.getConnection().getVirtualHost().ifPresent(host -> {
            String domain = host.getHostString();

            String MOTD = VelocityPlus.get().config().get("motd").getAsJsonObject().get(domain).getAsString();
            if (MOTD==null)
                return;

            builder.description(MiniMessage.miniMessage().deserialize(MOTD));
            event.setPing(builder.build());


            /*RegisteredServer info = ServerUtils.getRegisteredServer(domain);

            if (info==null)
                return;

            if (this.cacheServers.containsKey(info.getServerInfo().getName())) {
                ServerDetails details = this.cacheServers.get(info.getServerInfo().getName());

                builder.description(details.getMOTD());
                builder.maximumPlayers(details.getMaxPlayers());
                event.setPing(builder.build());
            }
            else {
                info.ping().thenAccept(result -> {
                    Component MOTD = result.getDescriptionComponent();
                    Optional<Favicon> icon = result.getFavicon();
                    Optional<ServerPing.Players> maxPlayers = result.getPlayers();

                    ServerDetails details = new ServerDetails(MOTD);
                    icon.ifPresent(details::setIcon);
                    maxPlayers.ifPresent(players -> details.setMaxPlayers(players.getMax()));

                    this.cacheServers.put(info.getServerInfo().getName(), details);

                    builder.description(details.getMOTD());
                    builder.maximumPlayers(details.getMaxPlayers());
                    event.setPing(builder.build());
                });
            }*/
        });
    }
}