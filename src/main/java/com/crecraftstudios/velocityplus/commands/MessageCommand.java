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
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class MessageCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("pmsg")
                .requires(source -> source.hasPermission(Permissions.Commands.MESSAGE))
                .then(BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                        .suggests((ctx, builder)->{
                            proxy.getAllPlayers().forEach(player -> builder.suggest(
                                    player.getUsername(),
                                    VelocityBrigadierMessage.tooltip(Component.text(player.getUsername()))
                            ));
                            return builder.buildFuture();
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    proxy.getPlayer(ctx.getArgument("player", String.class)).ifPresent(player->{
                                        String message = ctx.getArgument("message", String.class);
                                        Component component;
                                        if (ctx.getSource() instanceof Player senderPlayer)
                                            component = VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.MESSAGE_PLAYER, senderPlayer.getUsername(), message);
                                        else component = VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.MESSAGE_CONSOLE, message);

                                        player.sendMessage(component);
                                    });

                                    return Command.SINGLE_SUCCESS;
                        })))
                .build();

        return new BrigadierCommand(command);
    }
}