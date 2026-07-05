package com.oneonlybob.docker.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class HttpMessage {
    public final Headers headers;
    private String body;

    public HttpMessage() {
        this(new Headers());
    }

    public HttpMessage(Headers headers) {
        this.headers=headers;
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
}