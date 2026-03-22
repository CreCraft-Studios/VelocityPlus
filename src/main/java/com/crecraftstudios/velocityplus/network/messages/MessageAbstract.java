package com.crecraftstudios.velocityplus.network.messages;

import com.crecraftstudios.velocityplus.exceptions.DuplicateNetworkKeyException;
import com.crecraftstudios.velocityplus.network.NetworkManager;

public abstract class MessageAbstract implements IMessage{
    private final String name;

    public MessageAbstract(String name) {
        this.name=name;
    }

    public String getName() {
        return this.name;
    }

    public final void register() throws DuplicateNetworkKeyException {
        NetworkManager.register(this);
    }
}