package com.oneonlybob.docker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oneonlybob.docker.network.Method;
import com.oneonlybob.docker.network.Response;

public class Container {
    private String imgName;
    private String imgTag;
    private String containerName;
    private String img;

    private Docker docker;

    private final boolean hasImage;
    private final boolean hasContainer;

    public Container(String imageName, String imageTag, String containerName) {
        this.imgName=imageName;
        this.imgTag=imageTag;
        this.containerName=containerName;
        this.img=imageName+":"+imageTag;

        this.docker=new Docker();
        this.docker.get().headers.add("Connection", "close");
        this.docker.get().headers.add("Host", "localhost");

        this.docker.get().setMethod(Method.GET);
        this.docker.get().setPath("/images/"+this.imgName+"/json");
        this.hasImage = this.docker.get().connect().ok();

        this.docker.get().setPath("/containers/"+this.containerName+"/json");
        this.hasContainer = this.docker.get().connect().ok();
    }

    public String get(String object) {
        this.docker.get().setMethod(Method.GET);
        this.docker.get().setPath("/containers/"+this.containerName+"/json");
        Response response = this.docker.get().connect();
        if (response.ok())
            return response.getBodyAsString();
        else return "";
    }

    public void pull() {
        if (!this.hasImage) {
            this.docker.get().setMethod(Method.POST);
            this.docker.get().setPath("/images/create?fromImage="+this.imgName+"&tag="+this.imgTag);
            this.docker.get().connect();
        }
    }

    public void create() {
        if (!this.hasContainer) {
            this.pull();

            JsonObject json = new JsonObject();
            json.addProperty("Image", this.imgName);
            JsonArray env = new JsonArray();
            env.add("EULA=TRUE");
            json.add("Env", env);

            this.docker.get().setMethod(Method.POST);
            this.docker.get().setPath("/containers/create?name="+this.containerName);
            this.docker.get().setBody(json.toString());
            this.docker.get().headers.replace("Content-Type", "application/json");
            this.docker.get().connect();
        }
    }

    public void start() {
        this.docker.get().setMethod(Method.POST);
        this.docker.get().clearBody();
        this.docker.get().setPath("/containers/"+this.containerName+"/start");
        this.docker.get().connect();
    }

    public void stop() {
        this.docker.get().setMethod(Method.POST);
        this.docker.get().clearBody();
        this.docker.get().setPath("/containers/"+this.containerName+"/stop");
        this.docker.get().connect();
    }
}