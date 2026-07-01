package com.oneonlybob.docker;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;

import java.io.FileNotFoundException;

public class Docker {
    private UnixClient client;

    public Docker() {
        this("");
    }

    public Docker(String path) {
        try {
            this.client = new UnixClient("/var/run/docker.sock", path);
        } catch(FileNotFoundException err) {
            ExceptionUtils.printException(err);
        }
    }

    public UnixClient get() {
        return this.client;
    }
}