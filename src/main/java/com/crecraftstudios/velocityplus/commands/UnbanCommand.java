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
import com.velocitypowered.api.proxy.ProxyServer;

public class UnbanCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("vunban")
                .requires(source-> source.hasPermission(Permissions.Commands.UNBAN_PERM))
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .executes(ctx-> {
                            Mojang.getPlayerUUID(ctx.getArgument("player", String.class)).thenApply(uuid-> {
                                if (uuid==null)
                                    ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MOJANG_PLAYER_NOT_FOUND, ctx.getArgument("player", String.class)));
                                else {
                                    VelocityPlus.get().bans.unban(uuid);
                                    ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.PLAYER_NOW_UNBANNED, ctx.getArgument("player", String.class)));
                                }

                                return Command.SINGLE_SUCCESS;
                            });

                            return Command.SINGLE_SUCCESS;
                        })).build();

        return new BrigadierCommand(command);
    }
}