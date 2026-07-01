package com.crecraftstudios.velocityplus.json;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.crecraftstudios.velocityplus.utils.IOUtils;

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
            Path target = VelocityPlus.get().directory.toAbsolutePath().resolve(this.fileName);
            File file = new File(VelocityPlus.get().directory+"/"+this.fileName);

            if (Files.notExists(file.toPath())) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(this.fileName)) {
                    if (in==null) {
                        VelocityPlus.get().logger.error("config.json not found in jar file");
                        return;
                    }

                    Files.createDirectories(target.getParent());
                    Files.copy(in, target);
                }
            } else target = new File(VelocityPlus.get().directory+"/"+this.fileName).toPath();

            this.json= IOUtils.loadJsonObject(target.toString());
        } catch(IOException err) {
            ExceptionUtils.printException(err);
        }
    }
}