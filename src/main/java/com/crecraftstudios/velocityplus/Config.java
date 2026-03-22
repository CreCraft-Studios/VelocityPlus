package com.crecraftstudios.velocityplus;

import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.crecraftstudios.velocityplus.utils.IOUtils;
import com.google.gson.JsonObject;
import java.io.IOException;

public class Config {
    private JsonObject json;

    public JsonObject get() {
        return this.json;
    }

    public void load() {
        try {
            String dir = VelocityPlus.get().directory+"/config.json";
            IOUtils.createDirIfNeeded(dir);
            this.json = IOUtils.loadJsonObject(dir);
        } catch(IOException err) {
            ExceptionUtils.printException(err);
        }
    }

    public void save() {
        try {
            IOUtils.saveJsonObject(VelocityPlus.get().directory+"/config.json", this.json);
        } catch (IOException err) {
            ExceptionUtils.printException(err);
        }
    }
}