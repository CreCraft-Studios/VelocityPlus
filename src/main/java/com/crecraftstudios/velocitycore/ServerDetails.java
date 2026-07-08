package com.crecraftstudios.velocitycore;

import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.Component;

public class ServerDetails {
    private final Component MOTD;
    private Favicon icon;
    private int maxPlayers;

    public ServerDetails(Component MOTD, Favicon icon, int maxPlayers) {
        this.MOTD=MOTD;
        this.icon=icon;
        this.maxPlayers=maxPlayers;
    }

    public ServerDetails(Component MOTD) {
        this.MOTD=MOTD;
        this.maxPlayers=0;
    }

    public Component getMOTD() {
        return this.MOTD;
    }

    public Favicon getIcon() {
        return this.icon;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int max) {
        this.maxPlayers=max;
    }

    public void setIcon(Favicon icon) {
        this.icon=icon;
    }
}