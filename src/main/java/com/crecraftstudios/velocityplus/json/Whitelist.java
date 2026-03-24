package com.crecraftstudios.velocityplus.json;

import com.crecraftstudios.velocityplus.VelocityPlus;
import java.util.UUID;

public class Whitelist extends Json{
    private boolean enabled;

    public Whitelist() {
        super("whitelist");
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void enable() {
        this.enabled=true;
        this.updateConfig();
    }

    public void disable() {
        this.enabled=false;
        this.updateConfig();
    }

    private void updateConfig() {
        VelocityPlus.get().config().addProperty("enabled", this.enabled);
        VelocityPlus.get().config.save();
    }

    public void add(UUID uuid, String username) {
        this.get().addProperty(uuid.toString(), username);
        this.save();
    }

    public boolean isWhitelisted(UUID uuid) {
        return this.get().has(uuid.toString());
    }

    public void remove(String uuid) {
        this.get().remove(uuid);
        this.save();
    }
}