package com.crecraftstudios.velocitycore.api;

import com.crecraftstudios.velocitycore.VelocityCore;
import com.crecraftstudios.velocitycore.service.BanService;

public class VelocityCoreAPI {
    private static VelocityCoreAPI instance;

    private final BanService banService;

    public VelocityCoreAPI(VelocityCore vp) {
        instance=this;

        this.banService=new BanService(vp);
    }

    public static VelocityCoreAPI get() {
        return instance;
    }

    public BanService getBanService() {
        return this.banService;
    }
}