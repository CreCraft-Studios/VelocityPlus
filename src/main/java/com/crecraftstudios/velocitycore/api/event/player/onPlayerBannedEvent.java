package com.crecraftstudios.velocitycore.api.event.player;

public class onPlayerBannedEvent {
    private final String username;
    private String reason;
    private String source;

    public onPlayerBannedEvent(String player, String source, String reason) {
        this.username =player;
        this.source=source;
        this.reason=reason;
    }

    public String getPlayerUsername() {
        return this.username;
    }

    public String getReason() {
        return this.reason;
    }

    public String getSource() {
        return this.source;
    }

    public void setReason(String reason) {
        this.reason=reason;
    }

    public void setSource(String source) {
        this.source=source;
    }
}