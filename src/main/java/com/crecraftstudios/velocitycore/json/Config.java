package com.crecraftstudios.velocitycore.json;

import com.crecraftstudios.velocitycore.VelocityCore;
import com.crecraftstudios.velocitycore.utils.ExceptionUtils;
import com.crecraftstudios.velocitycore.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config extends Json {
    public Config() {
        super("config");
    }

    @Override
    public void load() {
        try {
            Path target = VelocityCore.get().directory.toAbsolutePath().resolve(this.fileName);
            File file = new File(VelocityCore.get().directory+"/"+this.fileName);

            if (Files.notExists(file.toPath())) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(this.fileName)) {
                    if (in==null) {
                        VelocityCore.get().logger.error("config.json not found in jar file");
                        return;
                    }

                    Files.createDirectories(target.getParent());
                    Files.copy(in, target);
                }
            } else target = new File(VelocityCore.get().directory+"/"+this.fileName).toPath();

            this.json= IOUtils.loadJsonObject(target.toString());
        } catch(IOException err) {
            ExceptionUtils.printException(err);
        }
    }
}