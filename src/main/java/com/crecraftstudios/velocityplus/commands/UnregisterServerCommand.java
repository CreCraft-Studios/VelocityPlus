package com.crecraftstudios.velocityplus.commands;

import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.json.Messages;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class UnregisterServerCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("unregister-server")
                .requires(source -> source.hasPermission(Permissions.Commands.UNREGISTER_SERVER))
                .then(BrigadierCommand.requiredArgumentBuilder("server name", StringArgumentType.word())
                        .suggests((ctx, builder)->{
                            proxy.getAllServers().forEach(s->builder.suggest(s.getServerInfo().getName()));
                            return builder.buildFuture();
                        })
                        .executes(ctx->{
                            String serverName = ctx.getArgument("server name", String.class);
                            Optional<RegisteredServer> server = proxy.getServer(serverName);
                            if (server.isEmpty()) {
                                ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.REGISTERED_SERVER_NOT_FOUND, serverName));
                                return Command.SINGLE_SUCCESS;
                            }

                            proxy.unregisterServer(server.get().getServerInfo());

                            return Command.SINGLE_SUCCESS;
                        })).build();

        return new BrigadierCommand(command);
    }
}