package com.crecraftstudios.velocityplus.commands;

import com.crecraftstudios.velocityplus.Messages;
import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.VelocityBrigadierMessage;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

public class FindCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("find")
                .requires(source -> source.hasPermission(Permissions.Commands.FIND))
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            proxy.getAllPlayers().forEach(player -> builder.suggest(
                                    player.getUsername(),
                                    VelocityBrigadierMessage.tooltip(MiniMessage.miniMessage().deserialize(player.getUsername()))
                            ));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String username = ctx.getArgument("player", String.class);
                            proxy.getPlayer(username).ifPresent(player -> {
                                Optional<ServerConnection> server = player.getCurrentServer();
                                if (!server.isPresent())
                                    ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.FIND_ONLINE_BUT_NOT_IN_SERVER));//Component.text("Player is online but not in a server. Strange???"));
                                else {
                                    ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.FIND_ONLINE, username, server.get().getServerInfo().getName()));//Component.text(username+" is currently on "+server.get().getServerInfo().getName()));
                                }
                            });

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();

        return new BrigadierCommand(command);
    }
}