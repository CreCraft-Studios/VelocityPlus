package com.crecraftstudios.velocityplus;

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
        VelocityPlus.get().proxy.getAllPlayers().forEach(player->player.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_ENTERING, String.valueOf(this.time))));

        this.timer = VelocityPlus.get().proxy.getScheduler().buildTask(VelocityPlus.get(), ()->{
            this.time-=1;

            if (this.time==30 || this.time<=10)
                VelocityPlus.get().proxy.getAllPlayers().forEach(player->player.sendMessage(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_ENTERING, String.valueOf(this.time))));

            if (this.time<=0) {
                this.timer.cancel();
                VelocityPlus.get().proxy.getAllPlayers().forEach(player->{
                    if (!player.hasPermission(Permissions.CONNECT_IN_MAINTENANCE))
                        player.disconnect(VelocityPlus.get().messages.getMessage(Messages.Keys.Message.MAINTENANCE_CURRENT));
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