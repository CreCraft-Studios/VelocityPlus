package com.crecraftstudios.velocitycore;

import com.crecraftstudios.velocitycore.json.Messages;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class Maintenance {
    private boolean inMaintenance=false;
    private int time;
    private ScheduledTask timer=null;

    public boolean inMaintenance() {
        return inMaintenance;
    }

    public void enter(int time) {
        if (this.inMaintenance || this.timer!=null)
            return;

        this.time=time;
        VelocityCore.get().proxy.getAllPlayers().forEach(player->player.sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_ENTERING, String.valueOf(this.time))));

        this.timer = VelocityCore.get().proxy.getScheduler().buildTask(VelocityCore.get(), ()->{
            this.time-=1;

            if (this.time==30 || this.time<=10)
                VelocityCore.get().proxy.getAllPlayers().forEach(player->player.sendMessage(VelocityCore.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_ENTERING, String.valueOf(this.time))));

            if (this.time<=0) {
                this.timer.cancel();
                VelocityCore.get().proxy.getAllPlayers().forEach(player->{
                    if (!player.hasPermission(Permissions.CONNECT_IN_MAINTENANCE))
                        player.disconnect(VelocityCore.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_CURRENT));
                });

                this.timer=null;
                this.inMaintenance=true;
            }
        })
                .repeat(1, TimeUnit.SECONDS)
                .schedule();
    }

    public void exit() {
        if (this.timer!=null)
            this.timer.cancel();

        this.inMaintenance=false;
        this.timer=null;
    }
}