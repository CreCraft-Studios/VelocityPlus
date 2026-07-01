package com.oneonlybob.docker.network;

import java.util.HashMap;

public class Headers {
    private final HashMap<String, String> headers = new HashMap<>();

    /**Adds the header to the headers map. It automatically appends the value to the header if the header already exists*/
    public void add(String key, String value) {
        if (!this.contains(key))
            this.headers.put(key.toLowerCase(), value);
        else {
            String newValue = this.get(key) + ", "+value;
            this.headers.replace(key.toLowerCase(), newValue);
        }
    }

    public String get(String key) {
        return this.headers.get(key.toLowerCase());
    }
    /**Use this method instead of add if you want to completely replace the value of an existing header*/
    public void replace(String key, String value) {
        if (this.contains(key))
            this.headers.remove(key.toLowerCase());
        this.add(key, value);
    }

    public void remove(String key) {
        if (this.contains(key))
            this.headers.remove(key.toLowerCase());
    }

    public boolean contains(String key) {
        return this.headers.containsKey(key.toLowerCase());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.headers.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\r\n"));
        sb.append("\r\n");
        return sb.toString();
    }
}