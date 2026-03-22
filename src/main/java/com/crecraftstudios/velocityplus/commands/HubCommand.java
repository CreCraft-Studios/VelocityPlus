package com.crecraftstudios.velocityplus.commands;

import com.crecraftstudios.velocityplus.Messages;
import com.crecraftstudios.velocityplus.Permissions;
import com.crecraftstudios.velocityplus.VelocityPlus;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class HubCommand {
    public static BrigadierCommand createCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> command = BrigadierCommand.literalArgumentBuilder("hub")
                .requires(source->source.hasPermission(Permissions.Commands.HUB))
                .executes(ctx->{
                    if (ctx.getSource() instanceof Player player) {
                        Optional<RegisteredServer> server = proxy.getServer(VelocityPlus.get().config().get("lobby").getAsString());
                        if (server.isEmpty())
                            player.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.HUB_FAILED));//Component.text("It appears the hub command isn't setup correctly"));

                        player.createConnectionRequest(server.get()).fireAndForget();
                    } else ctx.getSource().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.HUB_CONSOLE));//Component.text("Umm, hello. Only a player can execute this command."));

                    return Command.SINGLE_SUCCESS;
                })
                .build();

        return new BrigadierCommand(command);
    }
}