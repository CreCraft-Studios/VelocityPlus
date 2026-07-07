package com.crecraftstudios.velocityplus.commands;

import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.json.Messages;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Optional;

public class RegisterServerCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("register-server")
                .requires(source -> source.hasPermission(Permissions.Commands.REGISTER_SERVER))
                .then(BrigadierCommand.requiredArgumentBuilder("server name", StringArgumentType.word())
                        .then(BrigadierCommand.requiredArgumentBuilder("address", StringArgumentType.word())
                                .then(BrigadierCommand.requiredArgumentBuilder("port", IntegerArgumentType.integer()))
                                .executes(ctx->{
                                    String name = ctx.getArgument("server name", String.class);
                                    String address = ctx.getArgument("address", String.class);
                                    int port = ctx.getArgument("port", Integer.class);

                                    Optional<RegisteredServer> isRegistered = server.getServer(name);
                                    if (isRegistered.isPresent())
                                        ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.DUPLICATE_REGISTERED_SERVER, name));
                                    else server.registerServer(new ServerInfo(name, new InetSocketAddress(address, port)));

                                    return Command.SINGLE_SUCCESS;
                                }))).build();

        return new BrigadierCommand(command);
    }
}