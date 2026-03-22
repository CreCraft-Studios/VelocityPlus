package com.crecraftstudios.velocityplus.network;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.exceptions.DuplicateNetworkKeyException;
import com.crecraftstudios.velocityplus.network.messages.MessageAbstract;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.util.HashMap;

public class NetworkManager {
    /// CHANNEL_BACKEND is for messages sent from server to proxy and proxy to server. Never to or from a player/client
    public static final MinecraftChannelIdentifier CHANNEL_BACKEND = MinecraftChannelIdentifier.from("velocityplus:backend");

    private static final HashMap<String, MessageAbstract> MESSAGES = new HashMap<>();

    public void initialize(ProxyServer proxy) {
        proxy.getChannelRegistrar().register(CHANNEL_BACKEND);

        //TO-Do, implement this part when we get networks
        /*try {

        } catch(DuplicateNetworkKeyException err) {
            VelocityPlus.get().logger.error(err.getMessage());
        }*/
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!CHANNEL_BACKEND.equals(event.getIdentifier()))
            return;

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        if (!(event.getSource() instanceof ServerConnection server))
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String type = input.readUTF();
        if (MESSAGES.containsKey(type))
            MESSAGES.get(type).onMessage(input);
        else VelocityPlus.get().logger.warn("Got message key "+type+" but isn't registered. Ignoring this message");
    }

    public static void register(MessageAbstract msg) throws DuplicateNetworkKeyException {
        if (!MESSAGES.containsKey(msg.getName()))
            MESSAGES.put(msg.getName(), msg);
        else throw new DuplicateNetworkKeyException(msg.getName());
    }
}