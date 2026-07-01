package com.oneonlybob.docker.network;

public enum Method {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    UPDATE("UPDATE");

    final String value;
    Method(String stringValue) {
        this.value =stringValue;
    }

    public String toString() {
        return this.value;
    }
}