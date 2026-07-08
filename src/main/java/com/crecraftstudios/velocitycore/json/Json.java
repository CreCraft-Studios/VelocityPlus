package com.crecraftstudios.velocitycore.json;

import com.crecraftstudios.velocitycore.VelocityCore;
import com.crecraftstudios.velocitycore.utils.ExceptionUtils;
import com.crecraftstudios.velocitycore.utils.IOUtils;
import com.google.gson.JsonObject;

import java.io.IOException;

public class Json {
    protected JsonObject json;
    protected final String fileName;

    public Json(String fileName) {
        this.fileName =fileName+".json";
    }

    public JsonObject get() {
        return this.json;
    }

    public void load() {
        try {
            String dir = VelocityCore.get().directory+"/"+this.fileName;
            IOUtils.createDirIfNeeded(dir);
            this.json = IOUtils.loadJsonObject(dir);
        } catch(IOException err) {
            ExceptionUtils.printException(err);
        }
    }

    public void save() {
        try {
            IOUtils.saveJsonObject(VelocityCore.get().directory+"/"+this.fileName, this.json);
        } catch (IOException err) {
            ExceptionUtils.printException(err);
        }
    }
}