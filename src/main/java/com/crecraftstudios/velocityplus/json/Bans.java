package com.crecraftstudios.velocityplus.json;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Bans{
    private final TempBans tempBans;
    private final PermBans permBans;
    private final BanIds banIds;

    public Bans() {
        this.tempBans=new TempBans();
        this.permBans=new PermBans();
        this.banIds=new BanIds();

        this.tempBans.load();
        this.permBans.load();
        this.banIds.load();
    }

    public TempBans getTempBans() {
        return this.tempBans;
    }

    public PermBans getPermBans() {
        return this.permBans;
    }

    public static class TempBans extends Json {
        public TempBans() {
            super("temp-bans");
        }
    }

    public static class PermBans extends Json {
        public PermBans() {
            super("bans");
        }
    }

    public static class BanIds extends Json {
        public BanIds() {
            super("ban-ids");
        }
    }

    public boolean isBanned(UUID bannedPlayer) {
        return this.tempBans.get().has(bannedPlayer.toString()) || this.permBans.get().has(bannedPlayer.toString());
    }

    public Component getBanMessage(UUID bannedPlayer) {
        JsonObject banObj = (this.tempBans.get().has(bannedPlayer.toString()) ? this.tempBans.get().getAsJsonObject(bannedPlayer.toString()) : this.permBans.get().getAsJsonObject(bannedPlayer.toString()));
        if (banObj.has("duration"))
            return VelocityPlus.get().messages.getMessage(Messages.Keys.Message.TEMP_BANNED, banObj.get("reason").getAsString(), Instant.ofEpochMilli(banObj.get("duration").getAsLong()).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm ss 'UTC'")), banObj.get("ban-id").getAsString());
        else return VelocityPlus.get().messages.getMessage(Messages.Keys.Message.PERM_BANNED, banObj.get("reason").getAsString(), banObj.get("ban-id").getAsString());
    }

    public void tempBanPlayer(UUID bannedPlayer, String bannedBy, String reason, LocalDateTime duration) {
        JsonObject banObj = this.createBanObject(bannedPlayer, bannedBy, reason);
        banObj.addProperty("duration", duration.toEpochSecond(ZoneOffset.UTC));
        this.tempBans.get().add(bannedPlayer.toString(), banObj);
        this.tempBans.save();
        this.banIds.save();
    }

    public long permBanPlayer(UUID bannedPlayer, String bannedBy, String reason) {
        JsonObject banObj = this.createBanObject(bannedPlayer, bannedBy, reason);
        this.permBans.get().add(bannedPlayer.toString(), banObj);
        this.permBans.save();
        this.banIds.save();

        return banObj.get("ban-id").getAsLong();
    }

    private JsonObject createBanObject(UUID bannedPlayer, String bannedBy, String reason) {
        JsonObject banObj = new JsonObject();
        banObj.addProperty("reason", reason);
        banObj.addProperty("banned-by", bannedBy);
        banObj.addProperty("banned_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ss")));
        long banId = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        banObj.addProperty("ban-id", banId);
        this.banIds.get().addProperty(String.valueOf(banId), bannedPlayer.toString());
        return banObj;
    }

    public void unban(UUID uuid) {
        if (this.tempBans.get().has(uuid.toString())) {
            this.tempBans.get().remove(uuid.toString());
            this.tempBans.save();
        } else if (this.permBans.get().has(uuid.toString())) {
            this.permBans.get().remove(uuid.toString());
            this.permBans.save();
        }
    }

    public void unban(long banId) {
        if (this.banIds.get().has(String.valueOf(banId))) {
            this.unban(UUID.fromString(this.banIds.get().get(String.valueOf(banId)).getAsString()));
            this.banIds.get().remove(String.valueOf(banId));
            this.banIds.save();
        }
    }
}