package com.oneonlybob.docker;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.oneonlybob.docker.network.Request;
import com.oneonlybob.docker.network.Response;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

public class Docker {
    private static UnixClient client;
    private static boolean enabled=false;

    private static final HashMap<String, Container> containers = new HashMap<>();

    public static void addContainer(Container container) {
        if (!enabled)
            return;

        containers.put(container.getName(), container);
    }

    public static Container getContainer(String name) {
        return containers.get(name);
    }

    public static void removeContainer(String name) {
        containers.remove(name);
    }

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
            res.reason="Docker socket not enabled";
            return res;
        }

        return client.connect(request);
    }
}