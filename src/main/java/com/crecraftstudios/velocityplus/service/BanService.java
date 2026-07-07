package com.crecraftstudios.velocityplus.service;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.api.event.player.onPlayerBannedEvent;
import com.crecraftstudios.velocityplus.api.player.BanPlayer;
import com.crecraftstudios.velocityplus.json.Messages;
import com.crecraftstudios.velocityplus.utils.Mojang;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import jdk.jshell.spi.ExecutionControl;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class BanService implements BanPlayer {
    VelocityPlus vp;

    public BanService(VelocityPlus vp) {
        this.vp=vp;
    }

    @Override
    public void ban(String player, String bannedBy, String reason) {
        VelocityPlus.get().proxy.getPlayer(player).ifPresentOrElse(playerToBan ->{
            this.vp.proxy.getEventManager().fire(new onPlayerBannedEvent(player, bannedBy, reason)).thenAccept(e->{
                long banId = this.permBanPlayer(playerToBan.getUniqueId(), e.getSource(), e.getReason());
                playerToBan.disconnect(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.PERM_BANNED, e.getReason(), String.valueOf(banId)));

                VelocityPlus.get().proxy.getPlayer(bannedBy).ifPresent(source->{
                    source.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.PLAYER_NOW_BANNED, player));
                });
            });
        }, ()-> {
            Mojang.getPlayerUUID(player).thenAccept(uuid -> {
                Optional<Player> source = VelocityPlus.get().proxy.getPlayer(bannedBy);
                if (source.isEmpty())
                    return;

                if (uuid==null)
                    source.get().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MOJANG_PLAYER_NOT_FOUND, player));
                else {
                    this.vp.proxy.getEventManager().fire(new onPlayerBannedEvent(player, bannedBy, reason)).thenAccept(e->{
                        this.permBanPlayer(uuid, e.getSource(), e.getReason());
                        source.get().sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Commands.PLAYER_NOW_BANNED, player));
                    });
                }
            });
        });
    }

    @Override
    public void tempBan(String player, String bannedBy, String reason) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("This API is not implemented yet");
    }

    public boolean isBanned(UUID player) {
        return this.vp.bans.getTempBans().get().has(player.toString()) || this.vp.bans.getPermBans().get().has(player.toString());
    }

    @Override
    public boolean isBanned(Player player) {
        return this.isBanned(player.getUniqueId());
    }

    @Override
    public Component getBanMessage(UUID bannedPlayer) {
        JsonObject banObj = (this.vp.bans.getTempBans().get().has(bannedPlayer.toString()) ? this.vp.bans.getTempBans().get().getAsJsonObject(bannedPlayer.toString()) : this.vp.bans.getPermBans().get().getAsJsonObject(bannedPlayer.toString()));
        if (banObj.has("duration"))
            return VelocityPlus.get().messages.getMessage(Messages.Keys.Message.TEMP_BANNED, banObj.get("reason").getAsString(), Instant.ofEpochMilli(banObj.get("duration").getAsLong()).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm ss 'UTC'")), banObj.get("ban-id").getAsString());
        else return VelocityPlus.get().messages.getMessage(Messages.Keys.Message.PERM_BANNED, banObj.get("reason").getAsString(), banObj.get("ban-id").getAsString());
    }

    @Override
    public Component getBanMessage(Player player) {
        return this.getBanMessage(player.getUniqueId());
    }

    private long permBanPlayer(UUID bannedPlayer, String bannedBy, String reason) {
        JsonObject banObj = this.createBanObject(bannedPlayer, bannedBy, reason);
        this.vp.bans.getPermBans().get().add(bannedPlayer.toString(), banObj);
        this.vp.bans.getPermBans().save();
        this.vp.bans.getBanIds().save();

        return banObj.get("ban-id").getAsLong();
    }

    private void tempBanPlayer(UUID bannedPlayer, String bannedBy, String reason, LocalDateTime duration) {
        JsonObject banObj = this.createBanObject(bannedPlayer, bannedBy, reason);
        banObj.addProperty("duration", duration.toEpochSecond(ZoneOffset.UTC));
        this.vp.bans.getTempBans().get().add(bannedPlayer.toString(), banObj);
        this.vp.bans.getTempBans().save();
        this.vp.bans.getBanIds().save();
    }

    private JsonObject createBanObject(UUID bannedPlayer, String bannedBy, String reason) {
        JsonObject banObj = new JsonObject();
        banObj.addProperty("reason", reason);
        banObj.addProperty("banned-by", bannedBy);
        banObj.addProperty("banned_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ss")));
        long banId = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        banObj.addProperty("ban-id", banId);
        this.vp.bans.getBanIds().get().addProperty(String.valueOf(banId), bannedPlayer.toString());
        return banObj;
    }

    public void unban(UUID uuid) {
        if (this.vp.bans.getTempBans().get().has(uuid.toString())) {
            this.vp.bans.getTempBans().get().remove(uuid.toString());
            this.vp.bans.getTempBans().save();
        } else if (this.vp.bans.getPermBans().get().has(uuid.toString())) {
            this.vp.bans.getPermBans().get().remove(uuid.toString());
            this.vp.bans.getPermBans().save();
        }
    }

    public void unban(long banId) {
        if (this.vp.bans.getBanIds().get().has(String.valueOf(banId))) {
            this.unban(UUID.fromString(this.vp.bans.getBanIds().get().get(String.valueOf(banId)).getAsString()));
            this.vp.bans.getBanIds().get().remove(String.valueOf(banId));
            this.vp.bans.getBanIds().save();
        }
    }
}