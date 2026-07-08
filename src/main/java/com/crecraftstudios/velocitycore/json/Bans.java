package com.crecraftstudios.velocitycore.json;

public class Bans {
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

    public BanIds getBanIds() {
        return this.banIds;
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
}