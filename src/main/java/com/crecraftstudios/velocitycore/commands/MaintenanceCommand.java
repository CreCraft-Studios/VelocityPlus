package com.crecraftstudios.velocitycore.commands;

import com.crecraftstudios.velocitycore.json.Messages;
import com.crecraftstudios.velocitycore.Permissions;
import com.crecraftstudios.velocitycore.VelocityCore;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

public class MaintenanceCommand {
    public static BrigadierCommand createCommand() {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("maintenance")
                .requires(source->source.hasPermission(Permissions.Commands.MAINTENANCE))
                .then(BrigadierCommand.requiredArgumentBuilder("status", StringArgumentType.word())
                        .suggests((ctx, builder)-> {
                            builder.suggest("enable");
                            builder.suggest("disable");
                            return builder.buildFuture();
                        })
                        .executes(ctx->{
                            String status = ctx.getArgument("status", String.class);
                            if (status.equalsIgnoreCase("enable")) {
                                VelocityCore.get().enterMaintenanceMode(60);
                            } else if (status.equalsIgnoreCase("disable")) {
                                VelocityCore.get().exitMaintenanceMode();
                            } else ctx.getSource().sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.MAINTENANCE_UNKNOWN, status));

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(BrigadierCommand.requiredArgumentBuilder("time", IntegerArgumentType.integer(5, 120))
                                .executes(ctx->{
                                    String status = ctx.getArgument("status", String.class);
                                    if (status.equalsIgnoreCase("enable")) {
                                        VelocityCore.get().enterMaintenanceMode(ctx.getArgument("time", Integer.class));
                                    } else ctx.getSource().sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Commands.MAINTENANCE_UNKNOWN));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();

        return new BrigadierCommand(command);
    }
}