package com.crecraftstudios.velocitycore.commands;

import com.crecraftstudios.velocitycore.Permissions;
import com.crecraftstudios.velocitycore.api.VelocityCoreAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

public class BanCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("vban")
                .requires(source -> source.hasPermission(Permissions.Commands.BAN_PERM))
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((ctx, builder)-> {
                            proxy.getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            VelocityCoreAPI.get().getBanService().ban(ctx.getArgument("player", String.class), (ctx.getSource() instanceof Player bannedBy ? bannedBy.getUsername() : "CONSOLE"), "Ban hammer has spoken");
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("reason", StringArgumentType.greedyString())
                                .executes(ctx-> {
                                    VelocityCoreAPI.get().getBanService().ban(ctx.getArgument("player", String.class), (ctx.getSource() instanceof Player bannedBy ? bannedBy.getUsername() : "CONSOLE"), ctx.getArgument("reason", String.class));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();

        return new BrigadierCommand(command);
    }
}