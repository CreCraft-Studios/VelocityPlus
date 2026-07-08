package com.crecraftstudios.velocitycore.commands;

import com.crecraftstudios.velocitycore.json.Messages;
import com.crecraftstudios.velocitycore.Permissions;
import com.crecraftstudios.velocitycore.VelocityCore;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class SetLobbyCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("set-lobby")
                .requires(source->source.hasPermission(Permissions.Commands.SET_LOBBY))
                .executes(ctx -> {
                    if (ctx.getSource() instanceof Player player) {
                        Optional<ServerConnection> server = player.getCurrentServer();
                        VelocityCore.get().config().addProperty("lobby", server.get().getServerInfo().getName());
                        VelocityCore.get().config.save();

                        player.sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.LOBBY_SET, server.get().getServerInfo().getName()));
                    } else ctx.getSource().sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.LOBBY_CONSOLE_EXECUTED));

                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("server", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            proxy.getAllServers().forEach(server -> builder.suggest(
                                    server.getServerInfo().getName()
                            ));
                            return builder.buildFuture();
                        })
                        .executes(ctx-> {
                            String serverName = ctx.getArgument("server", String.class);
                            Optional<RegisteredServer> server = proxy.getServer(serverName);
                            if (server.isEmpty())
                                ctx.getSource().sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.LOBBY_NOT_FOUND, serverName));

                            VelocityCore.get().config().addProperty("lobby", server.get().getServerInfo().getName());
                            VelocityCore.get().config.save();

                            ctx.getSource().sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.LOBBY_SET));

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();

        return new BrigadierCommand(command);
    }
}