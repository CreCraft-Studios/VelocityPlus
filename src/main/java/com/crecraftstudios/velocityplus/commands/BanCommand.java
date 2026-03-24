package com.crecraftstudios.velocityplus.commands;

import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.json.Messages;
import com.crecraftstudios.velocityplus.utils.Mojang;
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
                            banPlayer(proxy, ctx.getSource(), ctx.getArgument("player", String.class), (ctx.getSource() instanceof Player bannedBy ? bannedBy.getUsername() : "CONSOLE"), "Ban hammer has spoken");
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("reason", StringArgumentType.greedyString())
                                .executes(ctx-> {
                                    banPlayer(proxy, ctx.getSource(), ctx.getArgument("player", String.class), (ctx.getSource() instanceof Player bannedBy ? bannedBy.getUsername() : "CONSOLE"), ctx.getArgument("reason", String.class));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();

        return new BrigadierCommand(command);
    }

    private static void banPlayer(final ProxyServer proxy, CommandSource source, String _playerToBan, String bannedBy, String reason) {
        proxy.getPlayer(_playerToBan).ifPresentOrElse(playerToBan -> {
            long banId = VelocityPlus.get().bans.permBanPlayer(playerToBan.getUniqueId(), bannedBy, reason);
            playerToBan.disconnect(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.PERM_BANNED, reason, String.valueOf(banId)));
            source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.PLAYER_NOW_BANNED, _playerToBan));
        }, ()->{
            Mojang.getPlayerUUID(_playerToBan).thenApply(uuid -> {
                if (uuid==null)
                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MOJANG_PLAYER_NOT_FOUND, _playerToBan));
                else {
                    VelocityPlus.get().bans.permBanPlayer(uuid, bannedBy, reason);
                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.PLAYER_NOW_BANNED, _playerToBan));
                }

                return Command.SINGLE_SUCCESS;
            });
        });
    }
}