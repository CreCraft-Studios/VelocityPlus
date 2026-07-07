package com.crecraftstudios.velocityplus.api.event.server;

import com.velocitypowered.api.proxy.server.RegisteredServer;

public class RequestStartServerEvent {
    private final RegisteredServer server;

    private boolean cancelled;

    public RequestStartServerEvent(RegisteredServer server) {
        this.server=server;
        this.cancelled =false;
    }

    public RegisteredServer getServer() {
        return this.server;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}