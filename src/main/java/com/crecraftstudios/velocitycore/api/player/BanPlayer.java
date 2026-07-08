package com.crecraftstudios.velocitycore.api.player;

import com.velocitypowered.api.proxy.Player;
import jdk.jshell.spi.ExecutionControl;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface BanPlayer {
    void ban(String player, String bannedBy, String reason);
    void tempBan(String player, String bannedBy, String reason) throws ExecutionControl.NotImplementedException;
    void unban(UUID player);
    void unban(long banId);
    boolean isBanned(UUID uuid);
    boolean isBanned(Player player);
    Component getBanMessage(UUID bannedPlayerUUID);
    Component getBanMessage(Player player);
}