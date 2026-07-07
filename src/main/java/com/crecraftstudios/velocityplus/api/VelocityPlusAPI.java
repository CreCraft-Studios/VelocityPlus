package com.crecraftstudios.velocityplus.api;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.service.BanService;

public class VelocityPlusAPI {
    private static VelocityPlusAPI instance;

    private final BanService banService;

    public VelocityPlusAPI(VelocityPlus vp) {
        instance=this;

        this.banService=new BanService(vp);
    }

    public static VelocityPlusAPI get() {
        return instance;
    }

    public BanService getBanService() {
        return this.banService;
    }
}