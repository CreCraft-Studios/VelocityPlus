package com.oneonlybob.docker.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Response {
    public int statusCode=StatusCodes.INTERNAL_SERVER_ERROR;
    public String reason="Exception";
    public final Headers headers;

    private String body;

    public Response() {
        this.headers=new Headers();
    }

    public void setBody(String body) {
        this.body=body;
    }

    public String getBodyAsString() {
        return this.body;
    }

    public JsonObject getBodyAsJson() {
        return JsonParser.parseString(this.body).getAsJsonObject();
    }

    public boolean ok() {
        return this.statusCode==StatusCodes.OK;
    }

    public boolean noContent() {
        return this.statusCode==StatusCodes.NO_CONTENT;
    }

    public boolean notFound() {
        return this.statusCode==StatusCodes.NOT_FOUND;
    }

    public boolean serverError() {
        return this.statusCode==StatusCodes.INTERNAL_SERVER_ERROR;
    }
}