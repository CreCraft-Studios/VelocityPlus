package com.oneonlybob.docker;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.oneonlybob.docker.network.Request;
import com.oneonlybob.docker.network.Response;

import java.io.FileNotFoundException;

public class Docker {
    private static UnixClient client;
    private static boolean enabled=false;

    public static void init() {
        try {
            client = new UnixClient("/var/run/docker.sock");
            enabled=true;
        } catch (FileNotFoundException err) {
            ExceptionUtils.printException(err);
        }
    }

    public static Response send(Request request) {
        if (!enabled) {
            Response res = new Response();
            res.reason="Docker socket not enabled in VelocityPlus";
            return res;
        }

        return client.connect(request);
    }
}