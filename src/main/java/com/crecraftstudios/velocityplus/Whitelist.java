package com.crecraftstudios.velocityplus;

import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.crecraftstudios.velocityplus.utils.IOUtils;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.UUID;

public class Whitelist {
    private JsonObject whitelist;
    private boolean enabled;

    public JsonObject get() {
        return this.whitelist;
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
        add(uuid.toString(), username);
    }

    public void add(String uuid, String username) {
        this.whitelist.addProperty(uuid, username);
        this.save();
    }

    public boolean isWhitelisted(String uuid) {
        return this.whitelist.has(uuid);
    }

    public void remove(String uuid) {
        this.whitelist.remove(uuid);
        this.save();
    }

    public void load() {
        try {
            String dir = VelocityPlus.get().directory+"/whitelist.json";
            IOUtils.createDirIfNeeded(dir);
            this.whitelist = IOUtils.loadJsonObject(dir);

            if (VelocityPlus.get().config().has("enabled") && VelocityPlus.get().config().get("enabled").getAsBoolean())
                this.enabled = VelocityPlus.get().config().get("enabled").getAsBoolean();

            else this.enabled=true;
        } catch (IOException err) {
            ExceptionUtils.printException(err);
        }
    }

    public void save() {
        try {
            IOUtils.saveJsonObject(VelocityPlus.get().directory+"/whitelist.json", this.whitelist);
        } catch(IOException err) {
            ExceptionUtils.printException(err);
        }
    }
}