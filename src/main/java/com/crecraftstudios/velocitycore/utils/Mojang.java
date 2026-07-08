package com.crecraftstudios.velocitycore.utils;

import com.crecraftstudios.velocitycore.VelocityCore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Mojang {
    public static CompletableFuture<UUID> getPlayerUUID(String username) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/minecraft/profile/lookup/name/%s".formatted(username)))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String body = response.body();

                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (!json.has("id")) {
                        VelocityCore.get().logger.error("Can't add %s as id is null".formatted(username));
                        return null;
                    }

                    return UUID.fromString(json.get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                })
                .exceptionally(err -> {
                    VelocityCore.get().logger.error(err.getMessage());
                    return null;
                });
    }
}