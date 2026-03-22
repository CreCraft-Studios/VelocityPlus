package com.crecraftstudios.velocityplus.commands;

import com.crecraftstudios.velocityplus.Messages;
import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WhitelistCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("global-whitelist")
                .requires(source->source.hasPermission(Permissions.Commands.WHITELIST))
                .then(BrigadierCommand.requiredArgumentBuilder("action", StringArgumentType.word())
                        .suggests((ctx, builder)->{
                            builder.suggest("list");
                            builder.suggest("add");
                            builder.suggest("remove");
                            builder.suggest("disable");
                            builder.suggest("enable");
                            return builder.buildFuture();
                        })
                        .executes(ctx->{
                            String action = ctx.getArgument("action", String.class);
                            CommandSource source = ctx.getSource();
                            switch (action) {
                                case "list":
                                    for (JsonElement value : VelocityPlus.get().whitelist().asMap().values()) {
                                        source.sendMessage(Component.text(value.getAsString()));
                                    }
                                    break;
                                case "disable":
                                    VelocityPlus.get().whitelist.disable();
                                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.WHITELIST_DISABLED));
                                    break;
                                case "enable":
                                    VelocityPlus.get().whitelist.enable();
                                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.WHITELIST_ENABLED));
                                    break;
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    String action = ctx.getArgument("action", String.class);
                                    String player = ctx.getArgument("player", String.class).toLowerCase();
                                    CommandSource source = ctx.getSource();
                                    switch (action) {
                                        case "add":
                                            getUUID(player).thenApply(uuid -> {
                                                if (uuid==null) {
                                                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.WHITELIST_ERROR));
                                                    return Command.SINGLE_SUCCESS;
                                                }

                                                VelocityPlus.get().whitelist.add(uuid, player);
                                                source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.WHITELIST_ADD_PLAYER, player));
                                                return Command.SINGLE_SUCCESS;
                                            });
                                            break;
                                        case "remove":
                                            Iterator<Map.Entry<String, JsonElement>> json = VelocityPlus.get().whitelist().entrySet().iterator();

                                            while (json.hasNext()) {
                                                Map.Entry<String, JsonElement> entry = json.next();

                                                String _username = entry.getValue().getAsString();
                                                if (_username.equalsIgnoreCase(player)) {
                                                    json.remove();
                                                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.WHITELIST_REMOVE_PLAYER));
                                                    break;
                                                }
                                                source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.WHITELIST_NOT_FOUND, player));
                                            }
                                            break;
                                    }

                                    return Command.SINGLE_SUCCESS;
                                }))
                ).build();

        return new BrigadierCommand(command);
    }

    private static CompletableFuture<String> getUUID(String username) {
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
                        VelocityPlus.get().logger.error("Can't add %s as id is null".formatted(username));
                        return null;
                    }

                    return json.get("id").getAsString();
                })
                .exceptionally(err -> {
                    VelocityPlus.get().logger.error(err.getMessage());
                   return null;
                });
    }
}