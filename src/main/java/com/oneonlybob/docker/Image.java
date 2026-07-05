package com.oneonlybob.docker;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.oneonlybob.docker.network.Method;
import com.oneonlybob.docker.network.Request;
import com.oneonlybob.docker.network.Response;

public class Image {
    private String name;
    private String tag;

    private final boolean hasImage;

    public Image(String name, String tag) {
        this.name=name;
        this.tag=tag;

        Request request = Request.builder()
                .setMethod(Method.GET)
                .setPath("/images/{}/json", this.name)
                .build();

        Response response = Docker.send(request);
        this.hasImage = response.ok();
    }

    public String getImage() {
        return this.name+":"+this.tag;
    }

    public void pull() {
        if (this.hasImage)
            return;

        Request request = Request.builder()
                .setMethod(Method.POST)
                .setPath("images/create")
                .setQuery("fromImage={}&tag={}", this.name, this.tag)
                .build();

        Response res = Docker.send(request);

        if (!res.ok())
            VelocityPlus.get().logger.error("Docker image pull failed. Docker returned {}", res.statusCode);
    }
}