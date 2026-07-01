package com.crecraftstudios.velocityplus;

import com.oneonlybob.docker.Container;

import java.util.HashMap;

public class Docker {
    private static final HashMap<String, Container> containers = new HashMap<>();

    public static void addContainer(Container container) {
        containers.put(container.getName(), container);
    }

    public static Container getContainer(String name) {
        return containers.get(name);
    }

    public static void removeContainer(String name) {
        containers.remove(name);
    }
}