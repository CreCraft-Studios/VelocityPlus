package com.crecraftstudios.velocitycore.commands;

import com.crecraftstudios.velocitycore.json.Messages;
import com.crecraftstudios.velocitycore.Permissions;
import com.crecraftstudios.velocitycore.VelocityCore;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class AlertCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("alert")
                .requires(source -> source.hasPermission(Permissions.Commands.ALERT))
                .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                        .executes(ctx -> {

                            String msg = ctx.getArgument("message", String.class);
                            proxy.getAllPlayers().forEach(player-> {
                                //player.sendMessage(Component.text("[ALERT] ", NamedTextColor.RED, TextDecoration.BOLD)
                                        //.append(Component.text(msg).style(Style.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))));
                                player.sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.ALERT));
                            });

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();

        return new BrigadierCommand(command);
    }
}