package com.crecraftstudios.velocityplus.network;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpServer {
    private com.sun.net.httpserver.HttpServer server;

    public void start() {
        new Thread(this::runServer, "HttpServerThread").start();
    }

    private void runServer() {
        try {
            VelocityPlus.get().logger.info("Starting HTTP server");
            this.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8586), 0);
        } catch(IOException err) {
            VelocityPlus.get().logger.error(err.getMessage()+"\nStacktrace: "+ExceptionUtils.createStacktrace(err));
        }
        this.server.createContext("/api/", exchange -> {
            try {
               if ("POST".equals(exchange.getRequestMethod())) {
                   byte[] body = exchange.getRequestBody().readAllBytes();
                   String payload = new String(body);
                   JsonObject json = JsonParser.parseString(payload).getAsJsonObject();

                   String path = exchange.getRequestURI().getPath();
                   switch(path) {
                       case "/api/register" -> this.handleRegister(json);
                       case "/api/online" -> this.handleOnline(json.get("name").getAsString());
                       case "/api/offline" -> this.handleOffline(json.get("name").getAsString());
                       case "api/unregister" -> this.handleUnregister(json.get("name").getAsString());
                       default -> VelocityPlus.get().logger.info("{} what the heck is this path????", path);
                   }

                   String response = "OK";
                   byte[] bytes = response.getBytes();
                   exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                   exchange.sendResponseHeaders(200, bytes.length);

                   try (OutputStream os = exchange.getResponseBody()) {
                       os.write(bytes);
                       os.flush();
                   }
               } else exchange.sendResponseHeaders(405, -1);

            } catch(IOException err) {
                VelocityPlus.get().logger.error(err.getMessage()+"\nStacktrace: "+ ExceptionUtils.createStacktrace(err));

                String response = "Server Error";
                byte[] bytes = response.getBytes();
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(500, bytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                    os.flush();
                }
            }
            finally {
                exchange.close();
            }
        });

        server.start();
        VelocityPlus.get().logger.info("HTTP server started");
    }

    private void handleRegister(JsonObject json) {
        String name = json.get("name").getAsString();
        String host = json.get("host").getAsString();
        int port = json.get("port").getAsInt();

        VelocityPlus.get().proxy.getServer(name).orElseGet(()-> {
            VelocityPlus.get().logger.info("Registering server {} with host {}", name, host);
            return VelocityPlus.get().proxy.registerServer(new ServerInfo(name, new InetSocketAddress(host, port)));
        });

        if (json.get("online").getAsBoolean())
            this.handleOnline(name);
    }

    private void handleUnregister(String name) {
        VelocityPlus.get().proxy.getServer(name).ifPresent((server)->VelocityPlus.get().proxy.unregisterServer(server.getServerInfo()));
    }

    private void handleOffline(String name) {
        VelocityPlus.get().serverIsOffline(name);
        VelocityPlus.get().logger.info("{} is set to offline", name);
    }

    private void handleOnline(String name) {
        VelocityPlus.get().serverIsOnline(name);
        VelocityPlus.get().logger.info("{} is set to online", name);
    }
}